-- =====================================================
-- V18: Add submission_type to job_applications + result to interview_records
--   - submission_type: Direct / Referral / RecruiterInbound / Other (default Direct)
--   - result: Pending / Passed / Failed (default Pending)
-- Auto-backfill submission_type='Referral' for applications already
-- linked to a Referrer in job_application_referrals.
-- =====================================================

ALTER TABLE job_applications
    ADD COLUMN submission_type VARCHAR(50) NOT NULL DEFAULT 'Direct';

ALTER TABLE interview_records
    ADD COLUMN result VARCHAR(20) NOT NULL DEFAULT 'Pending';

-- Backfill: applications already associated to a network contact via
-- role_type='Referrer' should be classified as Referral
UPDATE job_applications ja
SET submission_type = 'Referral'
WHERE ja.id IN (
    SELECT DISTINCT job_application_id
    FROM job_application_referrals
    WHERE role_type = 'Referrer'
);
