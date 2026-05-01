package com.growing.app.mcp;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.mcp.McpToolUtils;

import java.lang.reflect.Field;

/**
 * Resolves the authenticated userId inside an MCP {@code @Tool} method.
 *
 * <p>Strategy: the session ID embedded in the {@link McpSyncServerExchange} (available
 * via Spring AI's {@link ToolContext}) is used to look up the userId that
 * {@link McpAuthFilter} stored in {@link McpSessionStore} when the HTTP POST arrived.
 * This is thread-safe regardless of which thread Spring AI dispatches tool calls to.
 */
public final class McpRequestContext {

    private static final Logger log = LoggerFactory.getLogger(McpRequestContext.class);

    private McpRequestContext() {}

    /**
     * Resolves the userId for the current MCP tool invocation.
     *
     * @param toolContext Spring AI ToolContext injected by the framework into the {@code @Tool} method
     * @param sessionStore the session store populated by {@link McpAuthFilter}
     */
    public static Long requireUserId(ToolContext toolContext, McpSessionStore sessionStore) {
        McpSyncServerExchange exchange = McpToolUtils.getMcpExchange(toolContext)
                .orElseThrow(() -> new IllegalStateException(
                        "No MCP exchange in ToolContext — ensure the tool is called via the MCP server"));
        String sessionId = extractSessionId(exchange);
        log.info("[McpRequestContext] looking up sessionId={}", sessionId);
        return sessionStore.requireUserId(sessionId);
    }

    /**
     * Extracts the external session ID (the one used in the POST query param {@code ?sessionId=X})
     * by walking the private field chain:
     * {@code McpSyncServerExchange.exchange → McpAsyncServerExchange.session
     * → McpServerSession.transport → WebMvcMcpSessionTransport.sessionId}.
     *
     * <p>Note: {@code McpServerSession.getId()} returns an *internal* UUID that differs from
     * the external session ID used as the POST URL parameter. We must follow the transport
     * reference to get the correct key that {@link McpAuthFilter} registered in the store.
     */
    static String extractSessionId(McpSyncServerExchange exchange) {
        try {
            Field asyncField = McpSyncServerExchange.class.getDeclaredField("exchange");
            asyncField.setAccessible(true);
            Object asyncExchange = asyncField.get(exchange);

            Field sessionField = asyncExchange.getClass().getDeclaredField("session");
            sessionField.setAccessible(true);
            Object session = sessionField.get(asyncExchange);

            Field transportField = session.getClass().getDeclaredField("transport");
            transportField.setAccessible(true);
            Object transport = transportField.get(session);

            Field sessionIdField = transport.getClass().getDeclaredField("sessionId");
            sessionIdField.setAccessible(true);
            return (String) sessionIdField.get(transport);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Cannot extract session ID from MCP exchange: " + e.getMessage(), e);
        }
    }
}
