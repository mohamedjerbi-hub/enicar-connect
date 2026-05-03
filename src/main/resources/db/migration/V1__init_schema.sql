-- ============================================================
-- V1__init_schema.sql
-- ENICAR Connect — Initial Schema for PostgreSQL
-- Managed by Flyway. DO NOT edit manually.
-- ============================================================

-- Users
CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL PRIMARY KEY,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    phone       VARCHAR(30),
    bio         TEXT,
    website     VARCHAR(255),
    linkedin    VARCHAR(255),
    github      VARCHAR(255),
    role        VARCHAR(30)  NOT NULL,
    department  VARCHAR(100),
    level       VARCHAR(50),
    avatar_color VARCHAR(30),
    avatar_bg    VARCHAR(60),
    created_at  TIMESTAMP DEFAULT now(),
    updated_at  TIMESTAMP DEFAULT now()
);

-- Groups
CREATE TABLE IF NOT EXISTS groups_table (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    group_type      VARCHAR(30)  NOT NULL DEFAULT 'THEMATIC',
    privacy         VARCHAR(20)  NOT NULL DEFAULT 'PUBLIC',
    icon            VARCHAR(100),
    icon_color      VARCHAR(30),
    banner_gradient VARCHAR(200),
    creator_id      BIGINT NOT NULL REFERENCES users(id),
    created_at      TIMESTAMP DEFAULT now(),
    updated_at      TIMESTAMP DEFAULT now()
);

-- Group Members
CREATE TABLE IF NOT EXISTS group_members (
    id          BIGSERIAL PRIMARY KEY,
    group_id    BIGINT NOT NULL REFERENCES groups_table(id) ON DELETE CASCADE,
    user_id     BIGINT NOT NULL REFERENCES users(id),
    member_role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at   TIMESTAMP DEFAULT now(),
    CONSTRAINT uk_group_member UNIQUE (group_id, user_id)
);

-- Posts
CREATE TABLE IF NOT EXISTS posts (
    id          BIGSERIAL PRIMARY KEY,
    author_id   BIGINT NOT NULL REFERENCES users(id),
    group_id    BIGINT REFERENCES groups_table(id),
    body        TEXT   NOT NULL,
    visibility  VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    hashtags    TEXT,
    media_urls  TEXT,
    moderated   BOOLEAN NOT NULL DEFAULT false,
    created_at  TIMESTAMP DEFAULT now(),
    updated_at  TIMESTAMP DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_posts_author ON posts(author_id);
CREATE INDEX IF NOT EXISTS idx_posts_created ON posts(created_at DESC);

-- Post Mentions (many-to-many)
CREATE TABLE IF NOT EXISTS post_mentions (
    post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    PRIMARY KEY (post_id, user_id)
);

-- Post Likes
CREATE TABLE IF NOT EXISTS post_likes (
    id      BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    CONSTRAINT uk_post_like UNIQUE (post_id, user_id)
);

-- Comments
CREATE TABLE IF NOT EXISTS comments (
    id          BIGSERIAL PRIMARY KEY,
    post_id     BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    author_id   BIGINT NOT NULL REFERENCES users(id),
    text        TEXT   NOT NULL,
    created_at  TIMESTAMP DEFAULT now()
);

-- Post Reports
CREATE TABLE IF NOT EXISTS post_reports (
    id          BIGSERIAL PRIMARY KEY,
    post_id     BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    reporter_id BIGINT NOT NULL REFERENCES users(id),
    reason      TEXT,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMP DEFAULT now()
);

-- Events
CREATE TABLE IF NOT EXISTS events (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    date         VARCHAR(20)  NOT NULL,
    time         VARCHAR(10)  NOT NULL,
    location     VARCHAR(255),
    description  TEXT,
    category     VARCHAR(100) NOT NULL,
    organizer    VARCHAR(200) NOT NULL,
    color        VARCHAR(30),
    max_capacity INTEGER,
    owner_id     BIGINT REFERENCES users(id),
    created_at   TIMESTAMP DEFAULT now()
);

-- Event Registrations (many-to-many)
CREATE TABLE IF NOT EXISTS event_registrations (
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    user_id  BIGINT NOT NULL REFERENCES users(id),
    PRIMARY KEY (event_id, user_id)
);

-- Job Offers
CREATE TABLE IF NOT EXISTS job_offers (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    company     VARCHAR(200) NOT NULL,
    location    VARCHAR(200) NOT NULL,
    type        VARCHAR(50)  NOT NULL,
    description TEXT         NOT NULL,
    tags        TEXT,
    author_id   BIGINT NOT NULL REFERENCES users(id),
    created_at  TIMESTAMP DEFAULT now(),
    updated_at  TIMESTAMP DEFAULT now()
);

-- Job Applications
CREATE TABLE IF NOT EXISTS job_applications (
    id          BIGSERIAL PRIMARY KEY,
    job_id      BIGINT NOT NULL REFERENCES job_offers(id) ON DELETE CASCADE,
    applicant_id BIGINT NOT NULL REFERENCES users(id),
    applied_at  TIMESTAMP DEFAULT now(),
    CONSTRAINT uk_job_application UNIQUE (job_id, applicant_id)
);

-- Resource Files
CREATE TABLE IF NOT EXISTS resource_files (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    author_id   BIGINT NOT NULL REFERENCES users(id),
    file_size   VARCHAR(30)  NOT NULL,
    category    VARCHAR(50)  NOT NULL,
    file_path   VARCHAR(500) NOT NULL,
    icon        VARCHAR(100) NOT NULL,
    upload_date TIMESTAMP DEFAULT now()
);

-- Chat Messages
CREATE TABLE IF NOT EXISTS chat_messages (
    id           BIGSERIAL PRIMARY KEY,
    sender_id    BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    content      TEXT   NOT NULL,
    is_read      BOOLEAN NOT NULL DEFAULT false,
    timestamp    TIMESTAMP DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_chat_participants ON chat_messages(sender_id, recipient_id);

-- Mentorship Requests
CREATE TABLE IF NOT EXISTS mentorship_requests (
    id          BIGSERIAL PRIMARY KEY,
    mentor_id   BIGINT NOT NULL REFERENCES users(id),
    mentee_id   BIGINT NOT NULL REFERENCES users(id),
    objective   TEXT   NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMP DEFAULT now()
);

-- Connection Requests
CREATE TABLE IF NOT EXISTS connection_requests (
    id          BIGSERIAL PRIMARY KEY,
    sender_id   BIGINT NOT NULL REFERENCES users(id),
    receiver_id BIGINT NOT NULL REFERENCES users(id),
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    timestamp   TIMESTAMP DEFAULT now()
);

-- User Educations
CREATE TABLE IF NOT EXISTS user_educations (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    institution     VARCHAR(255),
    degree          VARCHAR(255),
    field_of_study  VARCHAR(255),
    start_date      DATE,
    end_date        DATE,
    description     TEXT
);

-- User Experiences
CREATE TABLE IF NOT EXISTS user_experiences (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company         VARCHAR(255),
    title           VARCHAR(255),
    location        VARCHAR(255),
    start_date      DATE,
    end_date        DATE,
    description     TEXT
);

-- User Skills
CREATE TABLE IF NOT EXISTS user_skills (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name            VARCHAR(255),
    level           VARCHAR(50)
);
