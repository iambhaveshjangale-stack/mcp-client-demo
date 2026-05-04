package com.mcp.demo.model;

public record McpToolDescriptorResponse(
		String name,
		String description,
		String inputSchema,
		String originalToolName
) {
}
