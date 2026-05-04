package com.mcp.demo.service;

import java.util.Set;
import java.util.stream.Collectors;

import com.mcp.demo.config.ChatAppProperties;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class McpChatService {

	private static final Logger log = LoggerFactory.getLogger(McpChatService.class);

	private static final Logger AGENT_GUIDANCE = LoggerFactory.getLogger("com.mcp.demo.guidance");

	private final McpAssistant mcpAssistant;

	private final ChatAppProperties chatAppProperties;

	private final MarkdownToolGuidance markdownToolGuidance;

	private final McpDirectToolService directToolService;

	public McpChatService(
			McpAssistant mcpAssistant,
			ChatAppProperties chatAppProperties,
			MarkdownToolGuidance markdownToolGuidance,
			McpDirectToolService directToolService
	) {
		this.mcpAssistant = mcpAssistant;
		this.chatAppProperties = chatAppProperties;
		this.markdownToolGuidance = markdownToolGuidance;
		this.directToolService = directToolService;
	}

	public String chat(String userMessage) {
		boolean toolsAllowed = chatAppProperties.allowsToolsForMessage(userMessage);

		log.debug("Incoming chat message='{}', toolsAllowed={}", userMessage, toolsAllowed);

		String systemPrompt = buildSystemPrompt(toolsAllowed);
		String normalizedMessage = userMessage != null ? userMessage : "";
		String fullPrompt = systemPrompt + "\n\nUser request:\n" + normalizedMessage;
		String reply = toolsAllowed ? mcpAssistant.chatWithTools(fullPrompt) : mcpAssistant.chat(fullPrompt);
		if (reply == null) {
			reply = "";
		}
		log.debug("Assistant reply length={}", reply.length());
		return reply;
	}

	private String buildSystemPrompt(boolean toolsAllowed) {
		AGENT_GUIDANCE.debug(
				"Resolving guidance for toolsAllowed={}",
				toolsAllowed);

		String base = chatAppProperties.systemPrompt();
		Set<String> toolNames = directToolService.listTools().stream()
				.map(tool -> tool.name())
				.collect(Collectors.toSet());
		String augmentation = markdownToolGuidance.buildAugmentation(toolsAllowed, toolNames);
		if (augmentation.isEmpty()) {
			AGENT_GUIDANCE.debug("No guidance appended; toolsAllowed={}, toolCount={}", toolsAllowed, toolNames.size());
			return base;
		}

		AGENT_GUIDANCE.info(
				"Guidance appended ({} chars); toolsAllowed={}, toolCount={}",
				augmentation.length(),
				toolsAllowed,
				toolNames.size()
		);

		return base + "\n\n## Applicable guidance for tools in this request\n" + augmentation;
	}
}
