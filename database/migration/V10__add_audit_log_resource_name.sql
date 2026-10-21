ALTER TABLE logs
    ADD COLUMN resource_name VARCHAR(255) NULL AFTER resource_id;
