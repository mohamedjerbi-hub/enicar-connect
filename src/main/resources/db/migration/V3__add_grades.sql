-- V3__add_grades.sql
CREATE TABLE IF NOT EXISTS grades (
    id            BIGSERIAL PRIMARY KEY,
    student_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    subject       VARCHAR(255) NOT NULL,
    grade_value   DOUBLE PRECISION NOT NULL,
    semester      INTEGER NOT NULL,
    academic_year VARCHAR(20) NOT NULL
);
