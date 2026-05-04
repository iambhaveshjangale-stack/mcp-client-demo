package com.mcp.demo.config;

import java.util.regex.Pattern;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "app.chat")
public interface ChatAppProperties {

	@WithDefault("""
			You are a helpful assistant.
			Use MCP tools only when the user clearly asks for patient/medical data or actions that those tools provide.
			For greetings, small talk, general knowledge, or unrelated chat, answer directly without calling tools.""")
	String systemPrompt();

	McpTools mcpTools();

	default boolean allowsToolsForMessage(String message) {
		return mcpTools().allowsToolsForMessage(message);
	}

	enum ToolsMode {
		ALWAYS,
		AUTO,
		NEVER
	}

	interface McpTools {
		@WithDefault("AUTO")
		ToolsMode mode();

		@WithDefault("(?i)(patient|patients|find\\s+patient|medical\\s+history|john\\s+doe|list\\s+all\\s+patients|show\\s+.*history|date\\s+of\\s+birth|registration)")
		String autoTriggerPattern();

		default boolean allowsToolsForMessage(String message) {
			return switch (mode()) {
				case ALWAYS -> true;
				case NEVER -> false;
				case AUTO -> {
					if (message == null || message.isBlank()) {
						yield false;
					}
					yield Pattern.compile(autoTriggerPattern()).matcher(message).find();
				}
			};
		}
	}
}
