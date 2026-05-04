package com.mcp.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.mcp.demo.config.AgentGuidanceProperties;
import com.mcp.demo.config.AgentGuidanceProperties.ToolMapping;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MarkdownToolGuidance {

	private static final Logger AGENT_GUIDANCE = LoggerFactory.getLogger("com.mcp.demo.guidance");

	private static final Logger log = LoggerFactory.getLogger(MarkdownToolGuidance.class);

	private final AgentGuidanceProperties properties;

	public MarkdownToolGuidance(AgentGuidanceProperties properties) {
		this.properties = properties;
	}

	public String buildAugmentation(boolean toolsAllowed, Collection<String> toolNames) {
		if (!properties.enabled()) {
			AGENT_GUIDANCE.debug("Skipping guidance: app.chat.agent-guidance.enabled=false");
			return "";
		}
		if (!toolsAllowed) {
			AGENT_GUIDANCE.debug(
					"Skipping guidance: MCP tools not exposed for this turn (mcp-tools gating)"
			);
			return "";
		}
		if (toolNames == null || toolNames.isEmpty()) {
			AGENT_GUIDANCE.debug("Skipping guidance: no MCP tool callbacks registered");
			return "";
		}

		String base = normalizeBasePath(properties.basePath());

		StringBuilder out = new StringBuilder();
		Set<String> loadedRelativePaths = new LinkedHashSet<>();
		Set<String> loadedSkillFiles = new LinkedHashSet<>();
		Set<String> loadedRuleFiles = new LinkedHashSet<>();
		List<String> matchedPatterns = new ArrayList<>();
		Set<String> toolsHitByAnyPattern = new LinkedHashSet<>();

		for (ToolMapping mapping : properties.mappings().orElse(List.of())) {
			if (mapping == null) {
				continue;
			}
			Pattern pat = compilePattern(mapping.toolPattern());
			if (pat == null) {
				continue;
			}
			List<String> matchedTools =
					toolNames.stream().filter(n -> pat.matcher(n).find()).toList();
			if (matchedTools.isEmpty()) {
				continue;
			}

			matchedPatterns.add(mapping.toolPattern());
			toolsHitByAnyPattern.addAll(matchedTools);

			AGENT_GUIDANCE.info(
					"Guidance mapping matched: pattern='{}', tools={}, skills='{}', rules='{}'",
					mapping.toolPattern(),
					matchedTools,
					blankToDash(mapping.skillsFile()),
					blankToDash(mapping.rulesFile())
			);

			appendMarkdown(
					out,
					base,
					mapping.skillsFile(),
					"Skills",
					loadedRelativePaths,
					loadedSkillFiles,
					loadedRuleFiles,
					true
			);
			appendMarkdown(
					out,
					base,
					mapping.rulesFile(),
					"Rules",
					loadedRelativePaths,
					loadedSkillFiles,
					loadedRuleFiles,
					false
			);
		}

		String result = out.toString().trim();
		if (result.isEmpty()) {
			if (matchedPatterns.isEmpty()) {
				AGENT_GUIDANCE.debug("No mapping pattern matched tools {}", toolNames);
			}
			else {
				AGENT_GUIDANCE.warn(
						"{} pattern(s) matched tools {} but no markdown content was loaded under {}.",
						matchedPatterns.size(),
						toolsHitByAnyPattern,
						base
				);
			}
			return "";
		}

		AGENT_GUIDANCE.info(
				"Guidance applied for this turn: skillFiles={}, ruleFiles={}, matchedPatterns={}, toolsMatched={}",
				loadedSkillFiles,
				loadedRuleFiles,
				matchedPatterns,
				toolsHitByAnyPattern
		);

		return result;
	}

	private void appendMarkdown(
			StringBuilder out,
			String base,
			String relativePath,
			String heading,
			Set<String> loadedRelativePaths,
			Set<String> loadedSkillFiles,
			Set<String> loadedRuleFiles,
			boolean skillsSection
	) {

		if (relativePath == null || relativePath.isBlank()) {
			AGENT_GUIDANCE.debug("Guidance: skipping {} -> no file configured", heading);
			return;
		}
		String normalized = relativePath.replace('\\', '/').replaceFirst("^/+", "");
		if (!loadedRelativePaths.add(normalized)) {
			AGENT_GUIDANCE.debug("Guidance: skipping duplicate resource '{}'", normalized);
			return;
		}

		String text;
		try (InputStream in = openResource(base, normalized)) {
			if (in == null) {
				AGENT_GUIDANCE.warn("Guidance file missing or unreadable: {}{}", base, normalized);
				return;
			}
			text = new String(in.readAllBytes(), StandardCharsets.UTF_8).trim();
		}
		catch (IOException e) {
			log.warn("Could not read guidance file {}: {}", base + normalized, e.toString());
			AGENT_GUIDANCE.warn("Guidance read failed for {}{}: {}", base, normalized, e.toString());
			return;
		}

		if (text.isEmpty()) {
			AGENT_GUIDANCE.debug("Guidance file is empty: {}{}", base, normalized);
			return;
		}

		if (out.length() > 0) {
			out.append("\n\n");
		}
		out.append("### ").append(heading).append(" (").append(normalized).append(")\n");
		out.append(text);

		if (skillsSection) {
			loadedSkillFiles.add(normalized);
		}
		else {
			loadedRuleFiles.add(normalized);
		}

		AGENT_GUIDANCE.info(
				"Guidance loaded {} from '{}' (chars={})",
				heading.toLowerCase(),
				normalized,
				text.length()
		);
	}

	private static String normalizeBasePath(String basePath) {
		if (basePath == null || basePath.isBlank()) {
			return "classpath:guidance/";
		}
		return basePath.endsWith("/") ? basePath : basePath + "/";
	}

	private static Pattern compilePattern(String pattern) {
		if (pattern == null || pattern.isBlank()) {
			return null;
		}
		return Pattern.compile(pattern);
	}

	private static InputStream openResource(String base, String normalizedRelativePath) {
		String path = base + normalizedRelativePath;
		if (path.startsWith("classpath:")) {
			String classpathLocation = path.substring("classpath:".length()).replaceFirst("^/+", "");
			return Thread.currentThread()
					.getContextClassLoader()
					.getResourceAsStream(classpathLocation);
		}
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
	}

	private static String blankToDash(String s) {
		return s == null || s.isBlank() ? "-" : s;
	}
}
