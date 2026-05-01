package com.growing.app.mcp;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory map from MCP session ID to authenticated userId.
 *
 * <p>Populated by {@link McpAuthFilter} on every POST to {@code /mcp/message?sessionId=X}.
 * Read by {@link McpRequestContext} inside {@code @Tool} methods, which may run on a thread
 * different from the original HTTP request thread (inside Reactor's {@code .block()} call),
 * making ThreadLocal and RequestContextHolder unreliable.
 */
@Component
public class McpSessionStore {

    private final ConcurrentHashMap<String, Long> sessions = new ConcurrentHashMap<>();

    public void register(String sessionId, Long userId) {
        sessions.put(sessionId, userId);
    }

    public Long requireUserId(String sessionId) {
        Long userId = sessions.get(sessionId);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "No authenticated session for MCP sessionId '" + sessionId
                            + "' — re-authenticate and reconnect the MCP client");
        }
        return userId;
    }

    public void remove(String sessionId) {
        sessions.remove(sessionId);
    }
}
