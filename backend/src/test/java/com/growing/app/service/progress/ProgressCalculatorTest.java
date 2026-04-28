package com.growing.app.service.progress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.growing.app.dto.NextActionType;
import com.growing.app.dto.PriorityLevel;
import com.growing.app.entity.JobApplication;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProgressCalculatorTest {

    // --- isStalled: stage-aware thresholds ----------------------------------

    // --- normalizeStatus: map both English & Chinese variants to canonical -

    @Test
    void normalizeStatus_acceptsEnglishCanonical() {
        assertEquals("Applied",      ProgressCalculator.normalizeStatus("Applied"));
        assertEquals("Screening",    ProgressCalculator.normalizeStatus("Screening"));
        assertEquals("Interviewing", ProgressCalculator.normalizeStatus("Interviewing"));
        assertEquals("Offer",        ProgressCalculator.normalizeStatus("Offer"));
    }

    @Test
    void normalizeStatus_mapsChineseToCanonical() {
        assertEquals("Applied",      ProgressCalculator.normalizeStatus("已投递"));
        assertEquals("Screening",    ProgressCalculator.normalizeStatus("筛选中"));
        assertEquals("Interviewing", ProgressCalculator.normalizeStatus("面试中"));
    }

    @Test
    void normalizeStatus_legacyNamesStillSupported() {
        // Old design used PhoneScreen/Onsite — legacy data may exist
        assertEquals("Screening",    ProgressCalculator.normalizeStatus("PhoneScreen"));
        assertEquals("Interviewing", ProgressCalculator.normalizeStatus("Onsite"));
    }

    @Test
    void normalizeStatus_inactiveUnchanged() {
        assertEquals("Rejected",   ProgressCalculator.normalizeStatus("Rejected"));
        assertEquals("NotApplied", ProgressCalculator.normalizeStatus("NotApplied"));
    }

    @Test
    void normalizeStatus_nullSafe() {
        assertNull(ProgressCalculator.normalizeStatus(null));
    }

    @Test
    void appliedStallsAfterEightDays() {
        assertTrue(ProgressCalculator.isStalled("Applied", 8));
    }

    @Test
    void chineseAppliedStallsAfterEightDays() {
        assertTrue(ProgressCalculator.isStalled("已投递", 8));
    }

    @Test
    void chineseInterviewingStallsAfterFifteenDays() {
        assertTrue(ProgressCalculator.isStalled("面试中", 15));
        assertFalse(ProgressCalculator.isStalled("面试中", 14));
    }

    @Test
    void appliedAtSevenDaysIsNotStalled() {
        assertFalse(ProgressCalculator.isStalled("Applied", 7));
    }

    @Test
    void screeningStallsAfterElevenDays() {
        assertTrue(ProgressCalculator.isStalled("Screening", 11));
    }

    @Test
    void screeningAtTenDaysIsNotStalled() {
        assertFalse(ProgressCalculator.isStalled("Screening", 10));
    }

    @Test
    void interviewingStallsAfterFifteenDays() {
        assertTrue(ProgressCalculator.isStalled("Interviewing", 15));
    }

    @Test
    void interviewingAtFourteenDaysIsNotStalled() {
        assertFalse(ProgressCalculator.isStalled("Interviewing", 14));
    }

    @Test
    void offerNeverStalls() {
        assertFalse(ProgressCalculator.isStalled("Offer", 30));
        assertFalse(ProgressCalculator.isStalled("Offer", 100));
    }

    // --- macroStageStep -----------------------------------------------------

    @Test
    void macroStageStepMapsAllFourActiveStatuses() {
        assertEquals(1, ProgressCalculator.macroStageStep("Applied"));
        assertEquals(2, ProgressCalculator.macroStageStep("Screening"));
        assertEquals(3, ProgressCalculator.macroStageStep("Interviewing"));
        assertEquals(4, ProgressCalculator.macroStageStep("Offer"));
    }

    @Test
    void macroStageStepWorksForChineseValues() {
        assertEquals(1, ProgressCalculator.macroStageStep("已投递"));
        assertEquals(2, ProgressCalculator.macroStageStep("筛选中"));
        assertEquals(3, ProgressCalculator.macroStageStep("面试中"));
    }

    // --- daysSinceApplied: statusHistory + fallback ------------------------

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JobApplication appWithHistory(String statusHistory, LocalDateTime createdAt) {
        JobApplication app = new JobApplication();
        app.setStatusHistory(statusHistory);
        app.setCreatedAt(createdAt);
        return app;
    }

    @Test
    void daysSinceApplied_usesStatusHistoryAppliedTimestamp() {
        LocalDateTime tenDaysAgo = LocalDateTime.now().minusDays(10);
        String history = "[{\"status\":\"Applied\",\"timestamp\":\"" + tenDaysAgo + "\"}]";
        JobApplication app = appWithHistory(history, LocalDateTime.now().minusDays(2));

        assertEquals(10, ProgressCalculator.daysSinceApplied(app, MAPPER));
    }

    @Test
    void daysSinceApplied_fallsBackToCreatedAtWhenHistoryNull() {
        JobApplication app = appWithHistory(null, LocalDateTime.now().minusDays(6));

        assertEquals(6, ProgressCalculator.daysSinceApplied(app, MAPPER));
    }

    @Test
    void daysSinceApplied_fallsBackToCreatedAtWhenHistoryMalformed() {
        JobApplication app = appWithHistory("{not valid json", LocalDateTime.now().minusDays(4));

        assertEquals(4, ProgressCalculator.daysSinceApplied(app, MAPPER));
    }

    @Test
    void daysSinceApplied_fallsBackWhenHistoryHasNoAppliedEntry() {
        String history = "[{\"status\":\"NotApplied\",\"timestamp\":\"2026-04-01T10:00:00\"}]";
        JobApplication app = appWithHistory(history, LocalDateTime.now().minusDays(3));

        assertEquals(3, ProgressCalculator.daysSinceApplied(app, MAPPER));
    }

    @Test
    void daysSinceApplied_picksFirstAppliedWhenMultiple() {
        LocalDateTime first = LocalDateTime.now().minusDays(20);
        LocalDateTime second = LocalDateTime.now().minusDays(5);
        String history = "[" +
                "{\"status\":\"Applied\",\"timestamp\":\"" + first + "\"}," +
                "{\"status\":\"PhoneScreen\",\"timestamp\":\"" + second + "\"}" +
                "]";
        JobApplication app = appWithHistory(history, LocalDateTime.now().minusDays(20));

        assertEquals(20, ProgressCalculator.daysSinceApplied(app, MAPPER));
    }

    // --- computePriority ----------------------------------------------------

    private java.time.LocalDate today() { return java.time.LocalDate.now(); }

    @Test
    void priority_offerWithDeadlineWithin14d_isOfferDeadline() {
        PriorityLevel p = ProgressCalculator.computePriority(
                "Offer", today().plusDays(5), 2, null);
        assertEquals(PriorityLevel.OFFER_DEADLINE, p);
    }

    @Test
    void priority_offerWithoutDeadline_isWaiting() {
        PriorityLevel p = ProgressCalculator.computePriority(
                "Offer", null, 30, null);
        assertEquals(PriorityLevel.WAITING, p);
    }

    @Test
    void priority_offerWithDeadlineFarFuture_isWaiting() {
        PriorityLevel p = ProgressCalculator.computePriority(
                "Offer", today().plusDays(30), 5, null);
        assertEquals(PriorityLevel.WAITING, p);
    }

    @Test
    void priority_offerDeadline_outranks_stalled() {
        // Offer is never stalled per isStalled, but if both rules could trigger,
        // OFFER_DEADLINE wins. This documents the precedence.
        PriorityLevel p = ProgressCalculator.computePriority(
                "Offer", today().plusDays(3), 30, today().plusDays(2));
        assertEquals(PriorityLevel.OFFER_DEADLINE, p);
    }

    @Test
    void priority_appliedStalled_isStalled() {
        PriorityLevel p = ProgressCalculator.computePriority(
                "Applied", null, 8, null);
        assertEquals(PriorityLevel.STALLED, p);
    }

    @Test
    void priority_stalled_outranks_scheduled() {
        PriorityLevel p = ProgressCalculator.computePriority(
                "Interviewing", null, 15, today().plusDays(2));
        assertEquals(PriorityLevel.STALLED, p);
    }

    @Test
    void priority_scheduledWithin7d_isScheduled() {
        PriorityLevel p = ProgressCalculator.computePriority(
                "Interviewing", null, 5, today().plusDays(3));
        assertEquals(PriorityLevel.SCHEDULED, p);
    }

    @Test
    void priority_scheduledBeyond7d_isWaiting() {
        PriorityLevel p = ProgressCalculator.computePriority(
                "Interviewing", null, 5, today().plusDays(10));
        assertEquals(PriorityLevel.WAITING, p);
    }

    @Test
    void priority_defaultActive_isWaiting() {
        PriorityLevel p = ProgressCalculator.computePriority(
                "Applied", null, 3, null);
        assertEquals(PriorityLevel.WAITING, p);
    }

    // --- buildMicroStageLabel ----------------------------------------------

    @Test
    void microLabel_interviewingWithStagesAndRecords_combines() {
        String label = ProgressCalculator.buildMicroStageLabel(
                "Interviewing", 4, 2, "Tech Round 1");
        assertEquals("Onsite · 2/4 (Tech Round 1 已完成)", label);
    }

    @Test
    void microLabel_appliedNoStages_fallback() {
        String label = ProgressCalculator.buildMicroStageLabel(
                "Applied", 0, 0, null);
        assertEquals("等待初步筛选", label);
    }

    @Test
    void microLabel_screeningNoRecords_fallback() {
        String label = ProgressCalculator.buildMicroStageLabel(
                "Screening", 0, 0, null);
        assertEquals("Phone Screen · 待安排", label);
    }

    @Test
    void microLabel_offer_isDecisionPending() {
        String label = ProgressCalculator.buildMicroStageLabel(
                "Offer", 5, 5, "Bar Raiser");
        assertEquals("Offer · 决定中", label);
    }

    @Test
    void microLabel_interviewingWithStagesButNoRecords_isPending() {
        String label = ProgressCalculator.buildMicroStageLabel(
                "Interviewing", 4, 0, null);
        assertEquals("Onsite · 待开始", label);
    }

    // --- daysSinceLastUpdate -----------------------------------------------

    @Test
    void daysSinceLastUpdate_usesStatusUpdatedAtWhenNoRecordActivity() {
        JobApplication app = new JobApplication();
        app.setStatusUpdatedAt(LocalDateTime.now().minusDays(9));
        assertEquals(9, ProgressCalculator.daysSinceLastUpdate(app, null));
    }

    @Test
    void daysSinceLastUpdate_fallsBackToCreatedAtWhenNull() {
        JobApplication app = new JobApplication();
        app.setStatusUpdatedAt(null);
        app.setCreatedAt(LocalDateTime.now().minusDays(4));
        assertEquals(4, ProgressCalculator.daysSinceLastUpdate(app, null));
    }

    @Test
    void daysSinceLastUpdate_usesRecordActivityWhenMoreRecent() {
        // Status updated 30d ago, but an InterviewRecord was edited 3d ago.
        // 3d should win — recent interview activity counts as progress.
        JobApplication app = new JobApplication();
        app.setStatusUpdatedAt(LocalDateTime.now().minusDays(30));
        LocalDateTime recordTs = LocalDateTime.now().minusDays(3);
        assertEquals(3, ProgressCalculator.daysSinceLastUpdate(app, recordTs));
    }

    @Test
    void daysSinceLastUpdate_usesStatusWhenStatusMoreRecentThanRecord() {
        // Status updated 2d ago, latest record was 10d ago. Status wins (most recent).
        JobApplication app = new JobApplication();
        app.setStatusUpdatedAt(LocalDateTime.now().minusDays(2));
        LocalDateTime recordTs = LocalDateTime.now().minusDays(10);
        assertEquals(2, ProgressCalculator.daysSinceLastUpdate(app, recordTs));
    }

    @Test
    void interviewing_recentRecordUpdate_isNotStalled() {
        // 面试中状态：状态本身 30 天没动，但 5 天前有面试记录更新 → 不应算 stalled
        JobApplication app = new JobApplication();
        app.setApplicationStatus("Interviewing");
        app.setStatusUpdatedAt(LocalDateTime.now().minusDays(30));
        LocalDateTime recordTs = LocalDateTime.now().minusDays(5);
        int days = ProgressCalculator.daysSinceLastUpdate(app, recordTs);
        assertFalse(ProgressCalculator.isStalled("Interviewing", days),
                "Interviewing with 5d old record activity should not be stalled");
    }

    // --- buildNextAction ---------------------------------------------------

    @Test
    void nextAction_offerWithDeadline_returnsDeadlineCountdown() {
        LocalDate deadline = LocalDate.of(2026, 5, 5);
        ProgressCalculator.NextAction action = ProgressCalculator.buildNextAction(
                PriorityLevel.OFFER_DEADLINE, deadline, null, null);
        assertEquals(NextActionType.OFFER_DEADLINE, action.type());
        assertEquals("⏰ 截止 5/5", action.label());
        assertEquals(deadline, action.date());
    }

    @Test
    void nextAction_scheduled_returnsInterviewInfo() {
        LocalDate when = LocalDate.of(2026, 5, 2);
        ProgressCalculator.NextAction action = ProgressCalculator.buildNextAction(
                PriorityLevel.SCHEDULED, null, when, "Tech Round 2");
        assertEquals(NextActionType.SCHEDULED_INTERVIEW, action.type());
        assertEquals("📅 Tech Round 2 · 5/2", action.label());
        assertEquals(when, action.date());
    }

    @Test
    void nextAction_stalled_suggestsFollowUp() {
        ProgressCalculator.NextAction action = ProgressCalculator.buildNextAction(
                PriorityLevel.STALLED, null, null, null);
        assertEquals(NextActionType.FOLLOW_UP, action.type());
        assertEquals("📩 跟进 recruiter", action.label());
        assertNull(action.date());
    }

    @Test
    void nextAction_waiting_returnsWaitingFeedback() {
        ProgressCalculator.NextAction action = ProgressCalculator.buildNextAction(
                PriorityLevel.WAITING, null, null, null);
        assertEquals(NextActionType.WAITING_FEEDBACK, action.type());
        assertEquals("⏳ 等待反馈", action.label());
        assertNull(action.date());
    }
}
