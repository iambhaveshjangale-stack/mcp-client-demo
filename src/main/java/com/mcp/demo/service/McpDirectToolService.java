package com.mcp.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcp.demo.model.McpToolDescriptorResponse;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.service.tool.ToolExecutionResult;
import io.quarkiverse.langchain4j.mcp.runtime.McpClientName;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class McpDirectToolService {

	private static final Logger log = LoggerFactory.getLogger(McpDirectToolService.class);

	@Inject
	ObjectMapper objectMapper;

	@Inject
	@McpClientName("patient-http")
	McpClient mcpClient;
	private volatile List<McpToolDescriptorResponse> tools = List.of();

	@PostConstruct
	public void init() {
		refreshTools();
	}

	public List<McpToolDescriptorResponse> listTools() {
		if (tools.isEmpty()) {
			refreshTools();
		}
		return tools;
	}

	private synchronized void refreshTools() {
		List<McpToolDescriptorResponse> loaded = new ArrayList<>();
		for (var tool : mcpClient.listTools()) {
			JsonObjectSchema inputSchema = tool.parameters();
			loaded.add(new McpToolDescriptorResponse(
					tool.name(),
					tool.description(),
					inputSchema != null ? inputSchema.toString() : null,
					tool.name()
			));
		}
		this.tools = List.copyOf(loaded);
		log.debug("Loaded {} MCP tools from client {}", this.tools.size(), mcpClient.key());
	}

	public String invokeTool(String toolName, Map<String, Object> arguments) {
		if (toolName == null || toolName.isBlank()) {
			throw new IllegalArgumentException("toolName is required");
		}

		String resolvedName = resolveToolName(toolName.trim());

		final String argsJson;
		try {
			argsJson = objectMapper.writeValueAsString(arguments);
		}
		catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Could not serialize arguments to JSON", e);
		}

		log.info("External MCP invoke: tool={}, argsJsonLength={}", toolName, argsJson.length());

		ToolExecutionResult result = mcpClient.executeTool(ToolExecutionRequest.builder()
				.name(resolvedName)
				.arguments(argsJson)
				.build());
		return result != null && result.resultText() != null ? result.resultText() : "";
	}

	private String resolveToolName(String toolName) {
		boolean exists = listTools().stream().anyMatch(tool -> toolName.equals(tool.name()));
		if (!exists) {
			throw new IllegalArgumentException(
					"Unknown MCP tool \"" + toolName + "\". Call GET /api/mcp/tools for names."
			);
		}
		return toolName;
	}
}
