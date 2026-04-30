package com.growing.app.mcp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

/**
 * Servlet filter that authenticates incoming MCP requests by Bearer JWT and stores
 * the resolved userId in {@link McpRequestContext} for the duration of the
 * request thread. Tools running on the same thread (SYNC mode) can then read the
 * userId without needing direct access to the request.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class McpAuthFilter extends OncePerRequestFilter {

    private final McpAuthService mcpAuthService;

    public McpAuthFilter(McpAuthService mcpAuthService) {
        this.mcpAuthService = mcpAuthService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/mcp/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        Long userId;
        try {
            userId = mcpAuthService.getUserIdFromRequest(request);
        } catch (ResponseStatusException e) {
            response.sendError(e.getStatusCode().value(),
                    e.getReason() != null ? e.getReason() : "Unauthorized");
            return;
        }

        McpRequestContext.setUserId(userId);
        try {
            chain.doFilter(request, response);
        } finally {
            McpRequestContext.clear();
        }
    }
}
