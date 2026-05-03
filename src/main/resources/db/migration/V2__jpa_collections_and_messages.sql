-- Alignement Hibernate / Flyway pour collections @ElementCollection et messagerie de groupe.

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS class_name VARCHAR(150);

ALTER TABLE groups_table
    ADD COLUMN IF NOT EXISTS kind VARCHAR(30) NOT NULL DEFAULT 'CUSTOM';

CREATE TABLE IF NOT EXISTS user_skill_keywords (
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    skill   VARCHAR(200) NOT NULL,
    CONSTRAINT uk_user_skill_keywords PRIMARY KEY (user_id, skill)
);

CREATE TABLE IF NOT EXISTS job_offer_required_skills (
    job_offer_id   BIGINT NOT NULL REFERENCES job_offers (id) ON DELETE CASCADE,
    required_skill VARCHAR(200) NOT NULL,
    CONSTRAINT uk_job_offer_skill PRIMARY KEY (job_offer_id, required_skill)
);

CREATE TABLE IF NOT EXISTS group_messages (
    id         BIGSERIAL PRIMARY KEY,
    content    TEXT      NOT NULL,
    timestamp  TIMESTAMP DEFAULT now(),
    sender_id  BIGINT NOT NULL REFERENCES users (id),
    group_id   BIGINT NOT NULL REFERENCES groups_table (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_group_messages_group ON group_messages (group_id);

-- Entité JPA présente dans le projet (évite l'échec de validate au démarrage).
CREATE TABLE IF NOT EXISTS user_skills (
    id        BIGSERIAL PRIMARY KEY,
    user_id   BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    name      VARCHAR(200) NOT NULL,
    percent   INTEGER NOT NULL
);
