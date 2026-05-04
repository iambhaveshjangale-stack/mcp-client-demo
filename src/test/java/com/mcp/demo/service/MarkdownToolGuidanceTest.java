package com.mcp.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.mcp.demo.config.AgentGuidanceProperties;
import com.mcp.demo.config.AgentGuidanceProperties.ToolMapping;
import org.junit.jupiter.api.Test;

class MarkdownToolGuidanceTest {

	@Test
	void loadsMarkdownWhenToolNameMatchesPattern() {
		ToolMapping mapping = mapping(
				"(?i)patient-http__Find_Patient",
				"bmad-mcp-patient-context/SKILL.md",
				"bmad-mcp-patient-context/RULE.md");
		var guidance = new MarkdownToolGuidance(properties(mapping));
		String out = guidance.buildAugmentation(true, java.util.List.of("patient-http__Find_Patient"));
		assertThat(out).contains("Skills");
		assertThat(out).contains("bmad-mcp-patient-context/SKILL.md");
		assertThat(out).contains("Ground responses");
		assertThat(out).contains("Rules");
		assertThat(out).contains("bmad-mcp-patient-context/RULE.md");
		assertThat(out).contains("Must");
	}

	@Test
	void noAugmentationWhenToolsDisallowed() {
		ToolMapping mapping = mapping(".*", "bmad-mcp-patient-context/SKILL.md", "");
		var guidance = new MarkdownToolGuidance(properties(mapping));
		assertThat(guidance.buildAugmentation(false, java.util.List.of("anything"))).isEmpty();
	}

	private static AgentGuidanceProperties properties(ToolMapping mapping) {
		return new AgentGuidanceProperties() {
			@Override
			public boolean enabled() {
				return true;
			}

			@Override
			public String basePath() {
				return "classpath:guidance/";
			}

			@Override
			public java.util.Optional<java.util.List<ToolMapping>> mappings() {
				return java.util.Optional.of(java.util.List.of(mapping));
			}
		};
	}

	private static ToolMapping mapping(String toolPattern, String skillsFile, String rulesFile) {
		return new ToolMapping() {
			@Override
			public String toolPattern() {
				return toolPattern;
			}

			@Override
			public String skillsFile() {
				return skillsFile;
			}

			@Override
			public String rulesFile() {
				return rulesFile;
			}
		};
	}
}
