package com.mcp.demo.config;

import java.util.List;
import java.util.Optional;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "app.chat.agent-guidance")
public interface AgentGuidanceProperties {

	@WithDefault("true")
	boolean enabled();

	@WithDefault("classpath:guidance/")
	String basePath();

	Optional<List<ToolMapping>> mappings();

	interface ToolMapping {
		@WithDefault("")
		String toolPattern();

		@WithDefault("")
		String skillsFile();

		@WithDefault("")
		String rulesFile();
	}
}
