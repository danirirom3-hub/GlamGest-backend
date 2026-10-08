ALTER TABLE users
    ADD COLUMN active_session_id VARCHAR(64) NULL;

CREATE INDEX idx_users_active_session_id ON users (active_session_id);
