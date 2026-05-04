package com.mcp.demo.service;

import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.mcp.runtime.McpToolBox;

@RegisterAiService
public interface McpAssistant {

	String chat(@UserMessage String message);

	@McpToolBox
	String chatWithTools(@UserMessage String message);
}
