package com.growing.app.mcp;

/**
 * Thread-local holder for the userId resolved from the Bearer JWT on the current
 * MCP request. Populated by {@link McpAuthFilter} on the request thread; read by
 * {@code @Tool} methods.
 *
 * <p>Requires {@code spring.ai.mcp.server.type=SYNC} so tool invocation stays on
 * the request thread (ASYNC would dispatch to a different thread and lose the
 * thread-local).
 */
public final class McpRequestContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    private McpRequestContext() {}

    public static void setUserId(Long id) {
        USER_ID.set(id);
    }

    public static Long requireUserId() {
        Long id = USER_ID.get();
        if (id == null) {
            throw new IllegalStateException(
                    "No userId in MCP request context — was McpAuthFilter applied?");
        }
        return id;
    }

    public static void clear() {
        USER_ID.remove();
    }
}
