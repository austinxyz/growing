package com.growing.app.service;

import com.growing.app.dto.ActiveProgressDTO;
import com.growing.app.dto.NextActionType;
import com.growing.app.dto.PriorityLevel;
import com.growing.app.entity.Company;
import com.growing.app.entity.InterviewRecord;
import com.growing.app.entity.InterviewStage;
import com.growing.app.entity.JobApplication;
import com.growing.app.repository.CompanyRepository;
import com.growing.app.repository.InterviewRecordRepository;
import com.growing.app.repository.InterviewStageRepository;
import com.growing.app.repository.JobApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceProgressTest {

    @Mock private JobApplicationRepository jobApplicationRepository;
    @Mock private CompanyRepository companyRepository;
    @Mock private InterviewStageRepository interviewStageRepository;
    @Mock private InterviewRecordRepository interviewRecordRepository;

    @InjectMocks
    private JobApplicationService service;

    // ---- helpers ----------------------------------------------------------

    private JobApplication app(long id, long companyId, String position, String status,
                               int daysSinceCreated, int daysSinceUpdated) {
        JobApplication a = new JobApplication();
        a.setId(id);
        a.setUserId(100L);
        a.setCompanyId(companyId);
        a.setPositionName(position);
        a.setApplicationStatus(status);
        a.setCreatedAt(LocalDateTime.now().minusDays(daysSinceCreated));
        a.setStatusUpdatedAt(LocalDateTime.now().minusDays(daysSinceUpdated));
        return a;
    }

    private Company company(long id, String name) {
        Company c = new Company();
        c.setId(id);
        c.setCompanyName(name);
        return c;
    }

    // ---- tests ------------------------------------------------------------

    @Test
    void getActiveProgress_emptyForUserWithNoActiveApplications() {
        when(jobApplicationRepository.findByUserIdAndApplicationStatusInOrderByCreatedAtDesc(
                eq(100L), anyList()))
                .thenReturn(List.of());

        List<ActiveProgressDTO> result = service.getActiveProgress(100L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getActiveProgress_returnsDtoWithAllDerivedFields() {
        JobApplication adobe = app(1L, 50L, "Sr EM, AI Platform", "Interviewing", 25, 9);
        when(jobApplicationRepository.findByUserIdAndApplicationStatusInOrderByCreatedAtDesc(
                eq(100L), anyList()))
                .thenReturn(List.of(adobe));
        when(companyRepository.findAllById(anyList()))
                .thenReturn(List.of(company(50L, "Adobe")));
        when(interviewStageRepository.findByJobApplicationIdInOrderByStageOrder(List.of(1L)))
                .thenReturn(List.of(stage(1L, "Phone", 1), stage(1L, "Tech 1", 2),
                                    stage(1L, "System Design", 3), stage(1L, "Behavioral", 4)));
        when(interviewRecordRepository.findByJobApplicationIdInOrderByInterviewDateDesc(List.of(1L)))
                .thenReturn(List.of(record(1L, "Tech 1", LocalDate.now().minusDays(12)),
                                    record(1L, "Phone", LocalDate.now().minusDays(20))));

        List<ActiveProgressDTO> result = service.getActiveProgress(100L);

        assertEquals(1, result.size());
        ActiveProgressDTO dto = result.get(0);
        assertEquals(1L, dto.id());
        assertEquals("Adobe", dto.companyName());
        assertEquals("Sr EM, AI Platform", dto.positionName());
        assertEquals("Interviewing", dto.applicationStatus());
        assertEquals(3, dto.macroStageStep());
        assertEquals(25, dto.daysSinceApplied());
        assertEquals(9, dto.daysSinceLastUpdate());
        // 9 days since update on Onsite is below 14d stalled threshold → not stalled
        assertEquals(PriorityLevel.WAITING, dto.priorityLevel());
        assertNotNull(dto.microStageLabel());
        assertNotNull(dto.nextActionType());
    }

    @Test
    void getClosedProgress_returnsRejectedAndWithdrawnApplications() {
        JobApplication rejected = app(10L, 60L, "Pos R", "Rejected", 90, 30);
        JobApplication withdrawn = app(11L, 61L, "Pos W", "Withdrawn", 60, 20);
        // Chinese variant — confirms the status filter accepts both English and Chinese
        JobApplication chineseRejected = app(12L, 62L, "Pos CR", "已拒绝", 45, 15);

        when(jobApplicationRepository.findByUserIdAndApplicationStatusInOrderByCreatedAtDesc(
                eq(100L), anyList()))
                .thenReturn(List.of(rejected, withdrawn, chineseRejected));
        when(companyRepository.findAllById(anyList()))
                .thenReturn(List.of(company(60L, "R Corp"),
                                    company(61L, "W Corp"),
                                    company(62L, "CR Corp")));
        when(interviewStageRepository.findByJobApplicationIdInOrderByStageOrder(anyList()))
                .thenReturn(List.of());
        when(interviewRecordRepository.findByJobApplicationIdInOrderByInterviewDateDesc(anyList()))
                .thenReturn(List.of());

        List<ActiveProgressDTO> result = service.getClosedProgress(100L);

        assertEquals(3, result.size());
        // All carry their applicationStatus through (no normalization to "Applied" for closed)
        assertTrue(result.stream().anyMatch(d -> "Rejected".equals(d.applicationStatus())));
        assertTrue(result.stream().anyMatch(d -> "Withdrawn".equals(d.applicationStatus())));
        assertTrue(result.stream().anyMatch(d -> "已拒绝".equals(d.applicationStatus())));
    }

    @Test
    void getClosedProgress_emptyForUserWithNoClosedApplications() {
        when(jobApplicationRepository.findByUserIdAndApplicationStatusInOrderByCreatedAtDesc(
                eq(100L), anyList()))
                .thenReturn(List.of());

        List<ActiveProgressDTO> result = service.getClosedProgress(100L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getActiveProgress_passesSubmissionTypeThrough() {
        JobApplication referredApp = app(1L, 50L, "Pos R", "Applied", 5, 5);
        referredApp.setSubmissionType("Referral");
        JobApplication directApp = app(2L, 51L, "Pos D", "Applied", 5, 5);
        directApp.setSubmissionType("Direct");

        when(jobApplicationRepository.findByUserIdAndApplicationStatusInOrderByCreatedAtDesc(
                eq(100L), anyList()))
                .thenReturn(List.of(referredApp, directApp));
        when(companyRepository.findAllById(anyList()))
                .thenReturn(List.of(company(50L, "R"), company(51L, "D")));
        when(interviewStageRepository.findByJobApplicationIdInOrderByStageOrder(anyList()))
                .thenReturn(List.of());
        when(interviewRecordRepository.findByJobApplicationIdInOrderByInterviewDateDesc(anyList()))
                .thenReturn(List.of());

        List<ActiveProgressDTO> result = service.getActiveProgress(100L);

        assertEquals(2, result.size());
        assertEquals("Referral",
                result.stream().filter(d -> d.id() == 1L).findFirst().orElseThrow().submissionType());
        assertEquals("Direct",
                result.stream().filter(d -> d.id() == 2L).findFirst().orElseThrow().submissionType());
    }

    @Test
    void getActiveProgress_doesNotIssuePerRowQueries() {
        // Regression guard for N+1: each repository must be called at most once,
        // batched. Specifically guards against companyRepository.findById per row,
        // which was the original N+1 bug fixed in this change.
        JobApplication a1 = app(1L, 50L, "Pos A", "Applied", 5, 5);
        JobApplication a2 = app(2L, 51L, "Pos B", "Interviewing", 10, 10);
        JobApplication a3 = app(3L, 50L, "Pos C", "Offer", 30, 5);
        a3.setOfferDeadline(LocalDate.now().plusDays(7));

        when(jobApplicationRepository.findByUserIdAndApplicationStatusInOrderByCreatedAtDesc(
                eq(100L), anyList()))
                .thenReturn(List.of(a1, a2, a3));
        when(companyRepository.findAllById(anyList()))
                .thenReturn(List.of(company(50L, "X"), company(51L, "Y")));
        when(interviewStageRepository.findByJobApplicationIdInOrderByStageOrder(anyList()))
                .thenReturn(List.of());
        when(interviewRecordRepository.findByJobApplicationIdInOrderByInterviewDateDesc(anyList()))
                .thenReturn(List.of());

        service.getActiveProgress(100L);

        // Exactly one batch call to each repo, regardless of how many applications
        verify(jobApplicationRepository, times(1))
                .findByUserIdAndApplicationStatusInOrderByCreatedAtDesc(eq(100L), anyList());
        verify(companyRepository, times(1)).findAllById(anyList());
        verify(interviewStageRepository, times(1))
                .findByJobApplicationIdInOrderByStageOrder(anyList());
        verify(interviewRecordRepository, times(1))
                .findByJobApplicationIdInOrderByInterviewDateDesc(anyList());

        // Crucial: per-row lookups must NEVER be invoked
        verify(companyRepository, never()).findById(anyLong());
        verify(interviewStageRepository, never()).findByJobApplicationIdOrderByStageOrder(anyLong());
        verify(interviewRecordRepository, never()).findByJobApplicationIdOrderByInterviewDateDesc(anyLong());
    }

    @Test
    void getActiveProgress_sortsByPriorityThenDaysDesc() {
        JobApplication waiting = app(1L, 50L, "Pos A", "Applied", 5, 5);
        JobApplication stalled = app(2L, 51L, "Pos B", "Applied", 12, 10);
        JobApplication offer = app(3L, 52L, "Pos C", "Offer", 30, 5);
        offer.setOfferDeadline(LocalDate.now().plusDays(7));

        when(jobApplicationRepository.findByUserIdAndApplicationStatusInOrderByCreatedAtDesc(
                eq(100L), anyList()))
                .thenReturn(List.of(waiting, stalled, offer));
        when(companyRepository.findAllById(anyList()))
                .thenReturn(List.of(company(50L, "A"), company(51L, "B"), company(52L, "C")));
        when(interviewStageRepository.findByJobApplicationIdInOrderByStageOrder(anyList()))
                .thenReturn(List.of());
        when(interviewRecordRepository.findByJobApplicationIdInOrderByInterviewDateDesc(anyList()))
                .thenReturn(List.of());

        List<ActiveProgressDTO> result = service.getActiveProgress(100L);

        assertEquals(3, result.size());
        // OFFER_DEADLINE first
        assertEquals(PriorityLevel.OFFER_DEADLINE, result.get(0).priorityLevel());
        assertEquals("C", result.get(0).companyName());
        // STALLED second
        assertEquals(PriorityLevel.STALLED, result.get(1).priorityLevel());
        assertEquals("B", result.get(1).companyName());
        // WAITING last
        assertEquals(PriorityLevel.WAITING, result.get(2).priorityLevel());
        assertEquals("A", result.get(2).companyName());
    }

    private InterviewStage stage(long appId, String name, int order) {
        InterviewStage s = new InterviewStage();
        s.setId((long) (100 + order));
        s.setJobApplicationId(appId);
        s.setStageName(name);
        s.setStageOrder(order);
        return s;
    }

    private InterviewRecord record(long appId, String stageName, LocalDate date) {
        InterviewRecord r = new InterviewRecord();
        r.setJobApplicationId(appId);
        // mimic the linked stage by id; service can resolve stageName via stages list
        r.setInterviewStageId(102L);
        r.setInterviewDate(date == null ? null : date.atStartOfDay());
        return r;
    }
}
