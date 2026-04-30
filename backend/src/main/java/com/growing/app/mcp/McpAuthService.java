package com.growing.app.mcp;

import com.growing.app.service.AuthService;
import com.growing.app.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Validates the Bearer JWT on incoming MCP requests and resolves it to a userId.
 * Reuses the project's existing {@link JwtUtil} and {@link AuthService} so MCP
 * traffic shares the same identity model as the REST controllers.
 */
@Service
public class McpAuthService {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    public McpAuthService(JwtUtil jwtUtil, AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    public Long getUserIdFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Missing or invalid Authorization header (expected 'Bearer <token>')");
        }
        String token = header.substring(BEARER_PREFIX.length()).trim();

        String username;
        try {
            username = jwtUtil.getUsernameFromToken(token);
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Token expired — please re-login to growing and copy a fresh token");
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid JWT: " + e.getMessage());
        }

        Long userId = authService.getUserIdByUsername(username);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Unknown user in JWT subject: " + username);
        }
        return userId;
    }
}
