package com.growing.app.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the {@link McpJobTools} methods (annotated with @Tool) as MCP tools
 * available on the Spring AI MCP server. Spring AI auto-config discovers
 * {@code ToolCallbackProvider} beans and serves their tools at the SSE/message
 * transport.
 */
@Configuration
public class McpToolsConfig {

    @Bean
    public ToolCallbackProvider jobToolCallbacks(McpJobTools tools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(tools)
                .build();
    }
}
