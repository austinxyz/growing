package com.growing.app.service.progress;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.growing.app.dto.NextActionType;
import com.growing.app.dto.PriorityLevel;
import com.growing.app.entity.JobApplication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * 面试进展看板的纯计算逻辑。无 Spring 依赖，方便单测。
 */
public final class ProgressCalculator {

    private ProgressCalculator() {}

    /**
     * 把任意 status 输入归一化到 4 个 canonical key:
     *   "Applied" / "Screening" / "Interviewing" / "Offer".
     * 支持英文（含 legacy "PhoneScreen"/"Onsite"）和中文存储值。
     * 其他输入（NotApplied / Rejected / Withdrawn / 未知）原样返回。
     */
    public static String normalizeStatus(String status) {
        if (status == null) return null;
        return switch (status) {
            case "Applied", "已投递"           -> "Applied";
            case "Screening", "PhoneScreen", "筛选中" -> "Screening";
            case "Interviewing", "Onsite", "面试中"   -> "Interviewing";
            case "Offer"                       -> "Offer";
            default                            -> status;
        };
    }

    /**
     * 从 statusHistory JSON 找首次 Applied 时间戳；fallback 到 createdAt。
     * Returns days between then and now (truncated to whole days).
     */
    public static int daysSinceApplied(JobApplication app, ObjectMapper mapper) {
        LocalDateTime startTs = parseStatusHistoryAppliedAt(app.getStatusHistory(), mapper);
        if (startTs == null) startTs = app.getCreatedAt();
        if (startTs == null) return 0;
        return (int) ChronoUnit.DAYS.between(startTs, LocalDateTime.now());
    }

    static LocalDateTime parseStatusHistoryAppliedAt(String json, ObjectMapper mapper) {
        if (json == null || json.isBlank()) return null;
        try {
            List<Map<String, Object>> entries = mapper.readValue(
                    json, new TypeReference<List<Map<String, Object>>>() {});
            for (Map<String, Object> entry : entries) {
                if ("Applied".equals(entry.get("status"))) {
                    Object ts = entry.get("timestamp");
                    if (ts != null) return LocalDateTime.parse(ts.toString());
                }
            }
        } catch (Exception ignore) {
            // Fallback path; intentionally swallow — caller logs at boundary if needed.
        }
        return null;
    }

    public static String buildMicroStageLabel(
            String applicationStatus,
            int totalStageCount,
            int completedRecordCount,
            String latestRecordStageName) {
        String s = normalizeStatus(applicationStatus);
        if ("Offer".equals(s)) {
            return "Offer · 决定中";
        }
        String label = displayLabel(s);
        if (totalStageCount == 0) {
            return switch (s == null ? "" : s) {
                case "Applied"      -> "等待初步筛选";
                case "Screening"    -> "Phone Screen · 待安排";
                case "Interviewing" -> "Onsite · 待安排";
                default             -> label;
            };
        }
        if (completedRecordCount == 0 || latestRecordStageName == null) {
            return label + " · 待开始";
        }
        return label + " · " + completedRecordCount + "/" + totalStageCount
                + " (" + latestRecordStageName + " 已完成)";
    }

    private static String displayLabel(String canonicalStatus) {
        if (canonicalStatus == null) return "";
        return switch (canonicalStatus) {
            case "Screening"    -> "Phone Screen";
            case "Interviewing" -> "Onsite";
            default             -> canonicalStatus;
        };
    }

    /**
     * Bundles the three next-action fields exposed on ActiveProgressDTO.
     */
    public record NextAction(NextActionType type, String label, LocalDate date) {}

    public static NextAction buildNextAction(
            PriorityLevel priority,
            LocalDate offerDeadline,
            LocalDate nextInterviewDate,
            String nextInterviewStageName) {
        return switch (priority) {
            case OFFER_DEADLINE -> new NextAction(
                    NextActionType.OFFER_DEADLINE,
                    "⏰ 截止 " + formatShortDate(offerDeadline),
                    offerDeadline);
            case STALLED -> new NextAction(
                    NextActionType.FOLLOW_UP, "📩 跟进 recruiter", null);
            case SCHEDULED -> new NextAction(
                    NextActionType.SCHEDULED_INTERVIEW,
                    "📅 " + (nextInterviewStageName == null ? "面试" : nextInterviewStageName)
                            + " · " + formatShortDate(nextInterviewDate),
                    nextInterviewDate);
            case WAITING -> new NextAction(
                    NextActionType.WAITING_FEEDBACK, "⏳ 等待反馈", null);
        };
    }

    private static String formatShortDate(LocalDate d) {
        if (d == null) return "";
        return d.getMonthValue() + "/" + d.getDayOfMonth();
    }

    /**
     * Days since the most recent sign of progress on this application.
     * Considers both <code>statusUpdatedAt</code> and the latest InterviewRecord
     * activity timestamp (so e.g. logging a new interview round in "Interviewing"
     * state counts as progress even though the macro status hasn't changed).
     *
     * @param latestRecordActivity the most recent InterviewRecord.updatedAt across
     *                             all records for this application, or null if none
     */
    public static int daysSinceLastUpdate(JobApplication app, LocalDateTime latestRecordActivity) {
        LocalDateTime ts = app.getStatusUpdatedAt();
        if (ts == null) ts = app.getCreatedAt();
        if (latestRecordActivity != null && (ts == null || latestRecordActivity.isAfter(ts))) {
            ts = latestRecordActivity;
        }
        if (ts == null) return 0;
        return (int) ChronoUnit.DAYS.between(ts, LocalDateTime.now());
    }

    public static int macroStageStep(String applicationStatus) {
        String s = normalizeStatus(applicationStatus);
        if (s == null) return 0;
        return switch (s) {
            case "Applied"      -> 1;
            case "Screening"    -> 2;
            case "Interviewing" -> 3;
            case "Offer"        -> 4;
            default             -> 0;
        };
    }

    /**
     * Priority precedence (top wins): OFFER_DEADLINE > STALLED > SCHEDULED > WAITING.
     *
     * @param nextInterviewDate nearest future InterviewRecord date, or null if none
     */
    public static PriorityLevel computePriority(
            String applicationStatus,
            LocalDate offerDeadline,
            int daysSinceLastUpdate,
            LocalDate nextInterviewDate) {
        LocalDate today = LocalDate.now();

        if ("Offer".equals(normalizeStatus(applicationStatus))
                && offerDeadline != null
                && !offerDeadline.isBefore(today)
                && ChronoUnit.DAYS.between(today, offerDeadline) <= 14) {
            return PriorityLevel.OFFER_DEADLINE;
        }
        if (isStalled(applicationStatus, daysSinceLastUpdate)) {
            return PriorityLevel.STALLED;
        }
        if (nextInterviewDate != null
                && !nextInterviewDate.isBefore(today)
                && ChronoUnit.DAYS.between(today, nextInterviewDate) <= 7) {
            return PriorityLevel.SCHEDULED;
        }
        return PriorityLevel.WAITING;
    }

    public static boolean isStalled(String applicationStatus, int daysSinceLastUpdate) {
        String s = normalizeStatus(applicationStatus);
        if (s == null) return false;
        return switch (s) {
            case "Applied"      -> daysSinceLastUpdate > 7;
            case "Screening"    -> daysSinceLastUpdate > 10;
            case "Interviewing" -> daysSinceLastUpdate > 14;
            default             -> false;  // Offer / NotApplied / Rejected: never stalled
        };
    }
}
