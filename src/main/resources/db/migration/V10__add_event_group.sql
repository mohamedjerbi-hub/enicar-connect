-- Add optional group ownership required by AppEvent.
ALTER TABLE events
    ADD COLUMN IF NOT EXISTS group_id BIGINT REFERENCES groups_table(id);

ALTER TABLE resource_files
    ADD COLUMN IF NOT EXISTS group_id BIGINT REFERENCES groups_table(id);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS photo_url VARCHAR(500);
