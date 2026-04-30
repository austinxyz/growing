package com.growing.app.mcp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class McpAuthFilterTest {

    @Mock private McpAuthService mcpAuthService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain chain;

    @InjectMocks
    private McpAuthFilter filter;

    @AfterEach
    void cleanContext() {
        McpRequestContext.clear();
    }

    @Test
    void mcpPath_validJwt_setsContextAndContinuesChain() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/mcp/message");
        when(mcpAuthService.getUserIdFromRequest(request)).thenReturn(42L);

        // Inside the chain, the userId should be visible to whatever runs next
        java.util.concurrent.atomic.AtomicReference<Long> seen = new java.util.concurrent.atomic.AtomicReference<>();
        doAnswer(inv -> {
            seen.set(McpRequestContext.requireUserId());
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertEquals(42L, seen.get());
        // After filter exits, context is cleared
        assertThrows(IllegalStateException.class, McpRequestContext::requireUserId);
    }

    @Test
    void nonMcpPath_skipsAuthEntirely() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/job-applications");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(mcpAuthService);
    }

    @Test
    void mcpPath_missingJwt_writes401AndAbortsChain() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/mcp/message");
        when(mcpAuthService.getUserIdFromRequest(request))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization"));

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(401), contains("Missing"));
        verifyNoInteractions(chain);
    }

    @Test
    void mcpPath_clearsContextEvenIfChainThrows() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/mcp/message");
        when(mcpAuthService.getUserIdFromRequest(request)).thenReturn(7L);
        doThrow(new RuntimeException("downstream boom")).when(chain).doFilter(request, response);

        assertThrows(RuntimeException.class, () -> filter.doFilter(request, response, chain));

        // Context must be cleared even on exception
        assertThrows(IllegalStateException.class, McpRequestContext::requireUserId);
    }
}
