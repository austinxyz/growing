package com.growing.app.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.growing.app.dto.ActiveProgressDTO;
import com.growing.app.dto.JobApplicationDTO;
import com.growing.app.dto.NextActionType;
import com.growing.app.dto.PriorityLevel;
import com.growing.app.service.JobApplicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ToolContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class McpJobToolsTest {

    @Mock private JobApplicationService jobApplicationService;
    @Mock private McpSessionStore mcpSessionStore;
    @Spy  private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @InjectMocks
    private McpJobTools tools;

    @Mock private ToolContext toolContext;

    private static final long USER_ID = 100L;

    // --- list_applications ---------------------------------------------------

    private JobApplicationDTO appDto(Long id, String company, String pos, String status) {
        JobApplicationDTO d = new JobApplicationDTO();
        d.setId(id);
        d.setCompanyName(company);
        d.setPositionName(pos);
        d.setApplicationStatus(status);
        d.setSubmissionType("Direct");
        d.setStatusUpdatedAt(LocalDateTime.now().minusDays(5));
        d.setCreatedAt(LocalDateTime.now().minusDays(10));
        d.setQualifications("LONG TEXT BODY"); // should NOT appear in summary
        d.setResponsibilities("LONG TEXT BODY"); // should NOT appear in summary
        d.setNotes("private notes");
        d.setInterviewStageCount(3);
        d.setInterviewRecordCount(1);
        return d;
    }

    @Test
    void listApplications_noFilter_callsGetAllAndReturnsTrimmedSummaries() throws Exception {
        try (MockedStatic<McpRequestContext> mrc = mockStatic(McpRequestContext.class)) {
            mrc.when(() -> McpRequestContext.requireUserId(toolContext, mcpSessionStore))
               .thenReturn(USER_ID);
            when(jobApplicationService.getAllApplicationsByUserId(USER_ID))
                    .thenReturn(List.of(
                            appDto(1L, "Adobe", "Sr EM", "Interviewing"),
                            appDto(2L, "Pinterest", "Sr SWE", "Applied")));

            String json = tools.listApplications(null, toolContext);

            verify(jobApplicationService).getAllApplicationsByUserId(USER_ID);
            verify(jobApplicationService, never()).getApplicationsByStatus(anyLong(), anyString());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> parsed = objectMapper.readValue(json, List.class);
            assertEquals(2, parsed.size());
            assertEquals("Adobe", parsed.get(0).get("companyName"));
            assertEquals("Sr EM", parsed.get(0).get("positionName"));
            assertEquals("Interviewing", parsed.get(0).get("applicationStatus"));
            assertEquals("Direct", parsed.get(0).get("submissionType"));
            assertNotNull(parsed.get(0).get("daysSinceApplied"));
            assertEquals(3, parsed.get(0).get("interviewStageCount"));
            assertFalse(parsed.get(0).containsKey("qualifications"),
                    "qualifications must be omitted from summary");
            assertFalse(parsed.get(0).containsKey("responsibilities"));
            assertFalse(parsed.get(0).containsKey("notes"));
        }
    }

    @Test
    void listApplications_withStatusFilter_callsByStatus() {
        try (MockedStatic<McpRequestContext> mrc = mockStatic(McpRequestContext.class)) {
            mrc.when(() -> McpRequestContext.requireUserId(toolContext, mcpSessionStore))
               .thenReturn(USER_ID);
            when(jobApplicationService.getApplicationsByStatus(USER_ID, "Interviewing"))
                    .thenReturn(List.of(appDto(1L, "Adobe", "EM", "Interviewing")));

            tools.listApplications("Interviewing", toolContext);

            verify(jobApplicationService).getApplicationsByStatus(USER_ID, "Interviewing");
            verify(jobApplicationService, never()).getAllApplicationsByUserId(anyLong());
        }
    }

    @Test
    void listApplications_chineseStatusFilter_passesThrough() {
        try (MockedStatic<McpRequestContext> mrc = mockStatic(McpRequestContext.class)) {
            mrc.when(() -> McpRequestContext.requireUserId(toolContext, mcpSessionStore))
               .thenReturn(USER_ID);
            when(jobApplicationService.getApplicationsByStatus(USER_ID, "面试中"))
                    .thenReturn(List.of());

            tools.listApplications("面试中", toolContext);

            verify(jobApplicationService).getApplicationsByStatus(USER_ID, "面试中");
        }
    }

    @Test
    void listApplications_emptyResult_returnsEmptyArray() throws Exception {
        try (MockedStatic<McpRequestContext> mrc = mockStatic(McpRequestContext.class)) {
            mrc.when(() -> McpRequestContext.requireUserId(toolContext, mcpSessionStore))
               .thenReturn(USER_ID);
            when(jobApplicationService.getAllApplicationsByUserId(USER_ID))
                    .thenReturn(List.of());

            String json = tools.listApplications(null, toolContext);

            assertEquals("[]", json.trim());
        }
    }

    // --- get_application_detail ---------------------------------------------

    @Test
    void getApplicationDetail_owned_returnsFullDtoIncludingHeavyFields() throws Exception {
        try (MockedStatic<McpRequestContext> mrc = mockStatic(McpRequestContext.class)) {
            mrc.when(() -> McpRequestContext.requireUserId(toolContext, mcpSessionStore))
               .thenReturn(USER_ID);
            JobApplicationDTO full = appDto(42L, "Adobe", "Sr EM", "Interviewing");
            when(jobApplicationService.getApplicationById(42L, USER_ID)).thenReturn(full);

            String json = tools.getApplicationDetail(42L, toolContext);

            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> parsed = objectMapper.readValue(json, java.util.Map.class);
            assertEquals(42, ((Number) parsed.get("id")).intValue());
            assertEquals("LONG TEXT BODY", parsed.get("qualifications"));
            assertEquals("LONG TEXT BODY", parsed.get("responsibilities"));
            verify(jobApplicationService).getApplicationById(42L, USER_ID);
        }
    }

    @Test
    void getApplicationDetail_notOwned_returns403Error() throws Exception {
        try (MockedStatic<McpRequestContext> mrc = mockStatic(McpRequestContext.class)) {
            mrc.when(() -> McpRequestContext.requireUserId(toolContext, mcpSessionStore))
               .thenReturn(USER_ID);
            when(jobApplicationService.getApplicationById(42L, USER_ID))
                    .thenThrow(new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.FORBIDDEN, "Forbidden"));

            String json = tools.getApplicationDetail(42L, toolContext);

            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> parsed = objectMapper.readValue(json, java.util.Map.class);
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> err = (java.util.Map<String, Object>) parsed.get("error");
            assertEquals(403, ((Number) err.get("code")).intValue());
        }
    }

    @Test
    void getApplicationDetail_notFound_returns404Error() throws Exception {
        try (MockedStatic<McpRequestContext> mrc = mockStatic(McpRequestContext.class)) {
            mrc.when(() -> McpRequestContext.requireUserId(toolContext, mcpSessionStore))
               .thenReturn(USER_ID);
            when(jobApplicationService.getApplicationById(999L, USER_ID))
                    .thenThrow(new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.NOT_FOUND, "Not found"));

            String json = tools.getApplicationDetail(999L, toolContext);

            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> parsed = objectMapper.readValue(json, java.util.Map.class);
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> err = (java.util.Map<String, Object>) parsed.get("error");
            assertEquals(404, ((Number) err.get("code")).intValue());
        }
    }

    // --- get_active_progress ------------------------------------------------

    private ActiveProgressDTO progressDto(Long id, String company, String status,
                                          PriorityLevel priority) {
        return new ActiveProgressDTO(
                id, 50L, company, "Position", status, 3, "micro",
                10, 5, priority,
                NextActionType.WAITING_FEEDBACK, "⏳ 等待反馈", null,
                "Direct");
    }

    @Test
    void getActiveProgress_default_callsActiveOnly() throws Exception {
        try (MockedStatic<McpRequestContext> mrc = mockStatic(McpRequestContext.class)) {
            mrc.when(() -> McpRequestContext.requireUserId(toolContext, mcpSessionStore))
               .thenReturn(USER_ID);
            when(jobApplicationService.getActiveProgress(USER_ID))
                    .thenReturn(List.of(progressDto(1L, "Adobe", "Interviewing", PriorityLevel.WAITING)));

            String json = tools.getActiveProgress(false, toolContext);

            verify(jobApplicationService).getActiveProgress(USER_ID);
            verify(jobApplicationService, never()).getClosedProgress(anyLong());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> parsed = objectMapper.readValue(json, List.class);
            assertEquals(1, parsed.size());
            assertEquals("WAITING", parsed.get(0).get("priorityLevel"));
        }
    }

    @Test
    void getActiveProgress_includeClosed_concatsBoth() throws Exception {
        try (MockedStatic<McpRequestContext> mrc = mockStatic(McpRequestContext.class)) {
            mrc.when(() -> McpRequestContext.requireUserId(toolContext, mcpSessionStore))
               .thenReturn(USER_ID);
            when(jobApplicationService.getActiveProgress(USER_ID))
                    .thenReturn(List.of(progressDto(1L, "Adobe", "Interviewing", PriorityLevel.WAITING)));
            when(jobApplicationService.getClosedProgress(USER_ID))
                    .thenReturn(List.of(progressDto(99L, "Z", "Rejected", PriorityLevel.WAITING)));

            String json = tools.getActiveProgress(true, toolContext);

            verify(jobApplicationService).getActiveProgress(USER_ID);
            verify(jobApplicationService).getClosedProgress(USER_ID);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> parsed = objectMapper.readValue(json, List.class);
            assertEquals(2, parsed.size());
        }
    }

    @Test
    void getActiveProgress_nullIncludeClosed_treatedAsFalse() {
        try (MockedStatic<McpRequestContext> mrc = mockStatic(McpRequestContext.class)) {
            mrc.when(() -> McpRequestContext.requireUserId(toolContext, mcpSessionStore))
               .thenReturn(USER_ID);
            when(jobApplicationService.getActiveProgress(USER_ID)).thenReturn(List.of());

            tools.getActiveProgress(null, toolContext);

            verify(jobApplicationService).getActiveProgress(USER_ID);
            verify(jobApplicationService, never()).getClosedProgress(anyLong());
        }
    }
}
