package com.growing.app.mcp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.growing.app.dto.ActiveProgressDTO;
import com.growing.app.dto.JobApplicationDTO;
import com.growing.app.service.JobApplicationService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MCP tools exposing the user's job-application data to MCP clients
 * (Claude Desktop / Claude Code) via the streamable transport endpoint.
 *
 * <p>The userId is read from {@link McpRequestContext}, which is populated
 * by {@link McpAuthFilter} on the request thread. Tools never accept a userId
 * argument from the MCP client — every query is automatically scoped to the
 * caller's identity.
 */
@Component
public class McpJobTools {

    private final JobApplicationService jobApplicationService;
    private final ObjectMapper objectMapper;

    public McpJobTools(JobApplicationService jobApplicationService,
                       ObjectMapper objectMapper) {
        this.jobApplicationService = jobApplicationService;
        this.objectMapper = objectMapper;
    }

    @Tool(name = "list_applications",
          description = "List the authenticated user's job applications. Returns a "
                  + "trimmed JSON array (no qualifications/responsibilities/notes/recruiter "
                  + "insights — fetch full detail with get_application_detail). Optional "
                  + "status filter accepts canonical English ('Applied','Screening',"
                  + "'Interviewing','Offer','Rejected','Withdrawn') or Chinese variants "
                  + "('已投递','筛选中','面试中','已拒绝','已撤回').")
    public String listApplications(
            @ToolParam(required = false,
                       description = "Optional applicationStatus filter; omit for all")
            String status) {
        Long userId = McpRequestContext.requireUserId();
        try {
            List<JobApplicationDTO> apps = (status == null || status.isBlank())
                    ? jobApplicationService.getAllApplicationsByUserId(userId)
                    : jobApplicationService.getApplicationsByStatus(userId, status);
            List<Map<String, Object>> summaries = new ArrayList<>(apps.size());
            for (JobApplicationDTO d : apps) summaries.add(toSummary(d));
            return objectMapper.writeValueAsString(summaries);
        } catch (ResponseStatusException e) {
            return errorJson(e);
        } catch (JsonProcessingException e) {
            return errorJson(500, "JSON serialization failure: " + e.getMessage());
        }
    }

    @Tool(name = "get_application_detail",
          description = "Get the full detail of a single job application owned by the "
                  + "authenticated user, including stages, interview records, qualifications, "
                  + "responsibilities, recruiter insights, and notes.")
    public String getApplicationDetail(
            @ToolParam(description = "Application id") Long id) {
        Long userId = McpRequestContext.requireUserId();
        try {
            JobApplicationDTO dto = jobApplicationService.getApplicationById(id, userId);
            return objectMapper.writeValueAsString(dto);
        } catch (ResponseStatusException e) {
            return errorJson(e);
        } catch (JsonProcessingException e) {
            return errorJson(500, "JSON serialization failure: " + e.getMessage());
        }
    }

    @Tool(name = "get_active_progress",
          description = "Return the user's active job applications with derived dashboard "
                  + "fields (priorityLevel, microStageLabel, daysSinceLastUpdate, "
                  + "nextActionType, nextActionLabel). Sorted by priority then days. "
                  + "Set include_closed=true to also include Rejected/Withdrawn applications.")
    public String getActiveProgress(
            @ToolParam(required = false,
                       description = "When true, append closed (Rejected/Withdrawn) applications. Default false.")
            Boolean includeClosed) {
        Long userId = McpRequestContext.requireUserId();
        boolean closed = Boolean.TRUE.equals(includeClosed);
        try {
            List<ActiveProgressDTO> active = jobApplicationService.getActiveProgress(userId);
            List<ActiveProgressDTO> all = new ArrayList<>(active);
            if (closed) all.addAll(jobApplicationService.getClosedProgress(userId));
            return objectMapper.writeValueAsString(all);
        } catch (ResponseStatusException e) {
            return errorJson(e);
        } catch (JsonProcessingException e) {
            return errorJson(500, "JSON serialization failure: " + e.getMessage());
        }
    }

    // ---- helpers ----------------------------------------------------------

    /** Trim a JobApplicationDTO down to summary fields suitable for list output. */
    private Map<String, Object> toSummary(JobApplicationDTO d) {
        Map<String, Object> m = new java.util.LinkedHashMap<>();
        m.put("id", d.getId());
        m.put("companyName", d.getCompanyName());
        m.put("positionName", d.getPositionName());
        m.put("applicationStatus", d.getApplicationStatus());
        m.put("submissionType", d.getSubmissionType());
        m.put("statusUpdatedAt", d.getStatusUpdatedAt());
        m.put("daysSinceApplied", daysSinceApplied(d));
        m.put("interviewStageCount", d.getInterviewStageCount());
        m.put("interviewRecordCount", d.getInterviewRecordCount());
        return m;
    }

    /**
     * Approximate daysSinceApplied from createdAt. The accurate
     * implementation (parsing first Applied timestamp from statusHistory)
     * already lives in ProgressCalculator and surfaces via get_active_progress;
     * here we use createdAt for simplicity in the summary tool.
     */
    private Integer daysSinceApplied(JobApplicationDTO d) {
        if (d.getCreatedAt() == null) return null;
        return (int) ChronoUnit.DAYS.between(d.getCreatedAt(), LocalDateTime.now());
    }

    private String errorJson(ResponseStatusException e) {
        return errorJson(e.getStatusCode().value(),
                e.getReason() != null ? e.getReason() : e.getMessage());
    }

    private String errorJson(int code, String message) {
        try {
            Map<String, Object> err = Map.of(
                    "code", code,
                    "message", message);
            return objectMapper.writeValueAsString(Map.of("error", err));
        } catch (JsonProcessingException ex) {
            return "{\"error\":{\"code\":" + code + ",\"message\":\"serialization failure\"}}";
        }
    }
}
