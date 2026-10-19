-- V7__align_schema_with_entities.sql
-- Align the full schema with JPA entities.

-- ============================================================
-- user_educations
-- The entity uses: degree, school, period, icon
-- V1 created: institution, degree, field_of_study, start_date, end_date, description
-- ============================================================
ALTER TABLE user_educations
    ADD COLUMN IF NOT EXISTS school   VARCHAR(255),
    ADD COLUMN IF NOT EXISTS period   VARCHAR(100),
    ADD COLUMN IF NOT EXISTS icon     VARCHAR(100);

-- ============================================================
-- user_experiences
-- The entity uses: title, company, period, description, icon
-- V1 created: company, title, location, start_date, end_date, description
-- ============================================================
ALTER TABLE user_experiences
    ADD COLUMN IF NOT EXISTS period   VARCHAR(100),
    ADD COLUMN IF NOT EXISTS icon     VARCHAR(100);

-- ============================================================
-- user_skills
-- The entity uses: name (NOT NULL), percent (INTEGER NOT NULL)
-- V1 created: name, level — 'level' column is missing 'percent'
-- ============================================================
ALTER TABLE user_skills
    ADD COLUMN IF NOT EXISTS percent  INTEGER NOT NULL DEFAULT 0;

-- ============================================================
-- job_applications
-- The entity uses: job_id, applicant_id, status, applied_at
-- V1 created: job_id, applicant_id, applied_at (no status)
-- status was added in V5, job_offer_id renamed to job_id in entity
-- ============================================================
-- status already added by V5; nothing more needed here.

-- ============================================================
-- post_likes
-- created_at was added in V6; nothing more needed here.
-- ============================================================
