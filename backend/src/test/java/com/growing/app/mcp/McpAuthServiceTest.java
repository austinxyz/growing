package com.growing.app.mcp;

import com.growing.app.service.AuthService;
import com.growing.app.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class McpAuthServiceTest {

    @Mock private JwtUtil jwtUtil;
    @Mock private AuthService authService;
    @Mock private HttpServletRequest request;

    @InjectMocks
    private McpAuthService mcpAuthService;

    @BeforeEach
    void resetMocks() {
        // mocks reset by MockitoExtension between tests
    }

    @Test
    void getUserIdFromRequest_validJwt_returnsUserId() {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(jwtUtil.getUsernameFromToken("valid.jwt.token")).thenReturn("austinxu");
        when(authService.getUserIdByUsername("austinxu")).thenReturn(1L);

        Long userId = mcpAuthService.getUserIdFromRequest(request);

        assertEquals(1L, userId);
    }

    @Test
    void getUserIdFromRequest_missingHeader_throws401() {
        when(request.getHeader("Authorization")).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> mcpAuthService.getUserIdFromRequest(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertTrue(ex.getReason() != null && ex.getReason().contains("Missing"));
    }

    @Test
    void getUserIdFromRequest_malformedHeader_throws401() {
        when(request.getHeader("Authorization")).thenReturn("not-a-bearer-token");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> mcpAuthService.getUserIdFromRequest(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void getUserIdFromRequest_invalidSignature_throws401() {
        when(request.getHeader("Authorization")).thenReturn("Bearer bad.signature.here");
        when(jwtUtil.getUsernameFromToken("bad.signature.here"))
                .thenThrow(new JwtException("invalid signature"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> mcpAuthService.getUserIdFromRequest(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void getUserIdFromRequest_expiredToken_throws401WithReloginMessage() {
        when(request.getHeader("Authorization")).thenReturn("Bearer expired.jwt.token");
        when(jwtUtil.getUsernameFromToken("expired.jwt.token"))
                .thenThrow(new ExpiredJwtException(null, null, "expired"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> mcpAuthService.getUserIdFromRequest(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertTrue(ex.getReason() != null
                && ex.getReason().toLowerCase().contains("expired")
                && ex.getReason().toLowerCase().contains("re-login"),
                "expected message to mention expired + re-login, was: " + ex.getReason());
    }

    @Test
    void getUserIdFromRequest_unknownUser_throws401() {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.but.unknown.user");
        when(jwtUtil.getUsernameFromToken("valid.jwt.but.unknown.user")).thenReturn("ghost");
        when(authService.getUserIdByUsername("ghost")).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> mcpAuthService.getUserIdFromRequest(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }
}
