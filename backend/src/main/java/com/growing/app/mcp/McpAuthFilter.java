package com.growing.app.mcp;

import com.growing.app.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
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
 *
 * <p>Fast path: if the session is already registered, only the JWT signature is
 * verified (pure CPU). The DB user-lookup is skipped to avoid the N+1 caused by
 * the EAGER-fetched careerPaths on the User entity.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class McpAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(McpAuthFilter.class);

    private static final String BEARER_PREFIX = "Bearer ";

    private final McpAuthService mcpAuthService;
    private final McpSessionStore mcpSessionStore;
    private final JwtUtil jwtUtil;

    public McpAuthFilter(McpAuthService mcpAuthService, McpSessionStore mcpSessionStore, JwtUtil jwtUtil) {
        this.mcpAuthService = mcpAuthService;
        this.mcpSessionStore = mcpSessionStore;
        this.jwtUtil = jwtUtil;
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
        log.debug("[McpAuthFilter] {} {} | sessionId={}", method, uri, sessionId);

        // Fast path: session already registered — only validate JWT signature (no DB query)
        if (sessionId != null && !sessionId.isBlank() && mcpSessionStore.lookup(sessionId) != null) {
            if (!isJwtValid(authHeader, response)) return;
            chain.doFilter(request, response);
            return;
        }

        // Slow path: new session — full validation + DB user lookup + register
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

    /** Validates JWT signature + expiry without hitting the DB. Returns false and writes 401 on failure. */
    private boolean isJwtValid(String authHeader, HttpServletResponse response) throws IOException {
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization header");
            return false;
        }
        try {
            jwtUtil.getUsernameFromToken(authHeader.substring(BEARER_PREFIX.length()).trim());
            return true;
        } catch (ExpiredJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Token expired — please re-login to growing and copy a fresh token");
            return false;
        } catch (RuntimeException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT: " + e.getMessage());
            return false;
        }
    }
}
