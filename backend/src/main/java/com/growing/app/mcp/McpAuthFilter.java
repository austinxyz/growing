package com.growing.app.mcp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

/**
 * Servlet filter that validates the Bearer JWT on MCP requests and rejects
 * unauthenticated connections early (before any tool code runs).
 *
 * <p>For POST {@code /mcp/message?sessionId=X} requests it also registers the
 * resolved userId in {@link McpSessionStore} so that {@link McpRequestContext}
 * can look it up inside {@code @Tool} methods, which may run on a thread that
 * does not have access to Spring's {@code RequestContextHolder}.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class McpAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(McpAuthFilter.class);

    private final McpAuthService mcpAuthService;
    private final McpSessionStore mcpSessionStore;

    public McpAuthFilter(McpAuthService mcpAuthService, McpSessionStore mcpSessionStore) {
        this.mcpAuthService = mcpAuthService;
        this.mcpSessionStore = mcpSessionStore;
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
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String authHeader = request.getHeader("Authorization");
        String sessionId = request.getParameter("sessionId");
        log.info("[McpAuthFilter] {} {} | sessionId={} | hasAuth={}", method, uri, sessionId, authHeader != null);
        try {
            Long userId = mcpAuthService.getUserIdFromRequest(request);
            log.info("[McpAuthFilter] userId={} | registering sessionId={}", userId, sessionId);
            if (sessionId != null && !sessionId.isBlank()) {
                mcpSessionStore.register(sessionId, userId);
            }
        } catch (ResponseStatusException e) {
            log.warn("[McpAuthFilter] Auth failed: {} {}", e.getStatusCode().value(), e.getReason());
            response.sendError(e.getStatusCode().value(),
                    e.getReason() != null ? e.getReason() : "Unauthorized");
            return;
        }
        chain.doFilter(request, response);
    }
}
