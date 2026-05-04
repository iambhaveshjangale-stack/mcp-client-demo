package com.mcp.demo.model;

import java.util.Map;

public record McpToolInvokeRequest(String toolName, Map<String, Object> arguments) {
}
