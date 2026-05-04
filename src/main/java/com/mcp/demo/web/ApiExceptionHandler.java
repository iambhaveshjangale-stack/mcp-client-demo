package com.mcp.demo.web;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ApiExceptionHandler implements ExceptionMapper<Exception> {
	private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

	@Override
	public Response toResponse(Exception ex) {
		if (ex instanceof IllegalArgumentException) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", ex.getMessage()))
					.build();
		}
		if (ex instanceof IllegalStateException) {
			return Response.status(Response.Status.SERVICE_UNAVAILABLE)
					.entity(Map.of("error", ex.getMessage()))
					.build();
		}
		if (ex instanceof JsonProcessingException) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Map.of("error", "Invalid JSON arguments: " + ex.getMessage()))
					.build();
		}
		log.error("Unhandled API exception", ex);
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(Map.of("error", "Unexpected server error"))
				.build();
	}
}
