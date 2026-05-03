-- V2__add_document_requests.sql
CREATE TABLE IF NOT EXISTS document_requests (
    id            BIGSERIAL PRIMARY KEY,
    request_type  VARCHAR(50) NOT NULL,
    status        VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    requested_by  BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at    TIMESTAMP DEFAULT now(),
    processed_at  TIMESTAMP,
    notes         TEXT
);
