ALTER TABLE clients
    ADD COLUMN is_system_client BOOLEAN NOT NULL DEFAULT FALSE;

INSERT IGNORE INTO clients (name, phone, email, registration_date, active, user_id, is_system_client)
SELECT 'Consumidor final', NULL, 'consumidor.final@glamgest.local', CURRENT_TIMESTAMP, TRUE, NULL, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM clients WHERE is_system_client = TRUE
);
