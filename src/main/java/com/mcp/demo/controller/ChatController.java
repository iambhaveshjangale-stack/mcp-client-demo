package com.mcp.demo.controller;

import com.mcp.demo.model.ChatMessageRequest;
import com.mcp.demo.model.ChatReplyResponse;
import com.mcp.demo.service.McpChatService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/chat")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChatController {

	private static final Logger log = LoggerFactory.getLogger(ChatController.class);

	@Inject
	McpChatService chatService;

	@POST
	public ChatReplyResponse chat(ChatMessageRequest request) {
		String message = request != null ? request.message() : null;
		log.debug("POST /api/chat message={}", message);
		String reply = chatService.chat(message);
		log.debug("POST /api/chat replyLength={}", reply.length());
		return new ChatReplyResponse(reply);
	}
}
