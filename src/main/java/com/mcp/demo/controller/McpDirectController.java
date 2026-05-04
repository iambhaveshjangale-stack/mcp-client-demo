package com.mcp.demo.controller;

import java.util.List;
import java.util.Map;

import com.mcp.demo.model.McpToolDescriptorResponse;
import com.mcp.demo.model.McpToolInvokeRequest;
import com.mcp.demo.model.McpToolInvokeResponse;
import com.mcp.demo.service.McpDirectToolService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/mcp")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class McpDirectController {

	private static final Logger log = LoggerFactory.getLogger(McpDirectController.class);

	@Inject
	McpDirectToolService directToolService;

	@GET
	@Path("/tools")
	public List<McpToolDescriptorResponse> listTools() {
		List<McpToolDescriptorResponse> tools = directToolService.listTools();
		log.debug("GET /api/mcp/tools count={}", tools.size());
		return tools;
	}

	@POST
	@Path("/invoke")
	public McpToolInvokeResponse invoke(McpToolInvokeRequest request) {
		String toolName = request != null ? request.toolName() : null;
		Map<String, Object> arguments =
				request != null && request.arguments() != null ? request.arguments() : Map.of();
		String result = directToolService.invokeTool(toolName, arguments);
		return new McpToolInvokeResponse(toolName, result);
	}
}
