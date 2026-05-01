package com.growing.app.mcp;

import com.growing.app.service.AuthService;
import com.growing.app.service.JobApplicationService;
import com.growing.app.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Integration tests for the MCP SSE transport and McpAuthFilter.
 * Uses an H2 in-memory database (MySQL-compat mode) and mocked AuthService
 * so no live MySQL is required.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    // H2 in-memory DB replacing MySQL for tests
    "spring.datasource.url=jdbc:h2:mem:mcp_it;DB_CLOSE_DELAY=-1;MODE=MySQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    // JWT config for tests — deterministic secret, short TTL
    "jwt.secret=test-secret-for-it-tests-must-be-at-least-32-bytes-long!",
    "jwt.expiration=3600000"
})
class McpSseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private AuthService authService;

    @MockBean
    private JobApplicationService jobApplicationService;

    private String validToken;

    @BeforeEach
    void setUp() {
        validToken = jwtUtil.generateToken("testmcp");
        when(authService.getUserIdByUsername("testmcp")).thenReturn(42L);
    }

    // ── 4.1 ── unauthenticated SSE request is rejected by the filter ──────────

    @Test
    void sse_noAuth_returns401() throws IOException {
        HttpURLConnection conn = openGetConn("/mcp/sse");
        conn.connect();

        assertEquals(401, conn.getResponseCode(),
            "McpAuthFilter must reject GET /mcp/sse without an Authorization header");

        conn.disconnect();
    }

    // ── 4.2 ── authenticated SSE request receives 200 + text/event-stream ─────

    @Test
    void sse_withValidAuth_returns200AndTextEventStream() throws IOException {
        HttpURLConnection conn = openGetConn("/mcp/sse");
        conn.setRequestProperty("Authorization", "Bearer " + validToken);
        conn.connect();

        assertEquals(200, conn.getResponseCode());
        String contentType = conn.getHeaderField("Content-Type");
        assertNotNull(contentType, "Content-Type header must be present");
        assertTrue(contentType.contains("text/event-stream"),
            "Expected text/event-stream, got: " + contentType);

        conn.disconnect();
    }

    // ── 4.3 ── SSE stream contains the sessionId endpoint event ───────────────

    @Test
    void sse_withValidAuth_streamContainsSessionId() throws IOException {
        HttpURLConnection conn = openGetConn("/mcp/sse");
        conn.setRequestProperty("Authorization", "Bearer " + validToken);
        conn.connect();

        assertEquals(200, conn.getResponseCode());

        boolean found = false;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            // Spring AI sends "event:endpoint\ndata:/mcp/message?sessionId=<uuid>\n\n"
            // immediately on connect — should appear within the first 10 lines.
            String line;
            for (int i = 0; i < 10 && (line = reader.readLine()) != null; i++) {
                if (line.startsWith("data:") && line.contains("sessionId=")) {
                    found = true;
                    break;
                }
            }
        } finally {
            conn.disconnect();
        }

        assertTrue(found, "SSE stream must contain a data line with sessionId=");
    }

    // ── 4.4 ── POST to /mcp/message with valid JWT is not rejected by filter ──

    @Test
    void message_withValidAuth_notRejectedByFilter() throws IOException {
        HttpURLConnection conn = openPostConn("/mcp/message?sessionId=it-test-session");
        conn.setRequestProperty("Authorization", "Bearer " + validToken);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Minimal JSON-RPC initialize request
        byte[] body = "{\"jsonrpc\":\"2.0\",\"method\":\"initialize\",\"id\":1,\"params\":{}}"
            .getBytes(StandardCharsets.UTF_8);
        conn.getOutputStream().write(body);
        conn.getOutputStream().flush();

        int status = conn.getResponseCode();
        assertNotEquals(401, status,
            "McpAuthFilter must not reject a POST with a valid Bearer JWT; status=" + status);

        conn.disconnect();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private HttpURLConnection openGetConn(String path) throws IOException {
        HttpURLConnection conn = (HttpURLConnection)
            new URL("http://localhost:" + port + path).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        return conn;
    }

    private HttpURLConnection openPostConn(String path) throws IOException {
        HttpURLConnection conn = (HttpURLConnection)
            new URL("http://localhost:" + port + path).openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        return conn;
    }
}
