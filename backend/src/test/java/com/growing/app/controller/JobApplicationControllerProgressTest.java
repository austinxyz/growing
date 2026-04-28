package com.growing.app.controller;

import com.growing.app.dto.ActiveProgressDTO;
import com.growing.app.dto.NextActionType;
import com.growing.app.dto.PriorityLevel;
import com.growing.app.service.AuthService;
import com.growing.app.service.JobApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobApplicationControllerProgressTest {

    @Mock private JobApplicationService jobApplicationService;
    @Mock private AuthService authService;

    @InjectMocks
    private JobApplicationController controller;

    @BeforeEach
    void stubAuth() {
        when(authService.getUsernameFromToken("token")).thenReturn("austinxu");
        when(authService.getUserIdByUsername("austinxu")).thenReturn(100L);
    }

    @Test
    void getActiveProgress_invokesServiceWithResolvedUserId() {
        ActiveProgressDTO dto = new ActiveProgressDTO(
                1L, 50L, "Adobe", "Sr EM, AI Platform", "Onsite",
                3, "Onsite · 2/4 (Tech Round 1 已完成)", 25, 9,
                PriorityLevel.WAITING, NextActionType.WAITING_FEEDBACK,
                "⏳ 等待反馈", null);
        when(jobApplicationService.getActiveProgress(100L)).thenReturn(List.of(dto));

        ResponseEntity<List<ActiveProgressDTO>> response =
                controller.getActiveProgress("Bearer token");

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Adobe", response.getBody().get(0).companyName());
        assertEquals(PriorityLevel.WAITING, response.getBody().get(0).priorityLevel());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getActiveProgress_emptyListReturnsOk() {
        when(jobApplicationService.getActiveProgress(100L)).thenReturn(List.of());

        ResponseEntity<List<ActiveProgressDTO>> response =
                controller.getActiveProgress("Bearer token");

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getClosedProgress_invokesServiceWithResolvedUserId() {
        ActiveProgressDTO dto = new ActiveProgressDTO(
                10L, 60L, "BrickCorp", "Engineer", "Rejected",
                0, "已拒绝", 90, 30,
                PriorityLevel.WAITING, NextActionType.WAITING_FEEDBACK, "—", null);
        when(jobApplicationService.getClosedProgress(100L)).thenReturn(List.of(dto));

        ResponseEntity<List<ActiveProgressDTO>> response =
                controller.getClosedProgress("Bearer token");

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Rejected", response.getBody().get(0).applicationStatus());
        assertEquals(200, response.getStatusCode().value());
    }
}
