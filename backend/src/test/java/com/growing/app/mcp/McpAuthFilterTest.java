package com.growing.app.mcp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class McpAuthFilterTest {

    @Mock private McpAuthService mcpAuthService;
    @Mock private McpSessionStore mcpSessionStore;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain chain;

    @InjectMocks
    private McpAuthFilter filter;

    @Test
    void mcpPath_validJwt_noSessionId_continuesChain() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/mcp/sse");
        when(mcpAuthService.getUserIdFromRequest(request)).thenReturn(42L);
        when(request.getParameter("sessionId")).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(mcpSessionStore, never()).register(anyString(), anyLong());
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    void mcpPath_validJwt_withSessionId_registersInStoreAndContinues() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/mcp/message");
        when(mcpAuthService.getUserIdFromRequest(request)).thenReturn(42L);
        when(request.getParameter("sessionId")).thenReturn("sess-abc");

        filter.doFilter(request, response, chain);

        verify(mcpSessionStore).register("sess-abc", 42L);
        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    void nonMcpPath_skipsAuthEntirely() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/job-applications");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(mcpAuthService);
        verifyNoInteractions(mcpSessionStore);
    }

    @Test
    void mcpPath_missingJwt_writes401AndAbortsChain() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/mcp/message");
        when(mcpAuthService.getUserIdFromRequest(request))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization"));

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(401), contains("Missing"));
        verifyNoInteractions(chain);
        verifyNoInteractions(mcpSessionStore);
    }

    @Test
    void mcpPath_expiredJwt_writes401AndAbortsChain() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/mcp/sse");
        when(mcpAuthService.getUserIdFromRequest(request))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Token expired — please re-login"));

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(401), contains("expired"));
        verifyNoInteractions(chain);
    }
}
