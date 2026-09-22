-- Preserve records and replace physical deletes with logical state changes.
ALTER TABLE roles ADD COLUMN active BIT(1) NOT NULL DEFAULT b'1';
ALTER TABLE clients ADD COLUMN active BIT(1) NOT NULL DEFAULT b'1';
ALTER TABLE sales ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE sales ADD COLUMN voided_at DATETIME NULL;
ALTER TABLE sales ADD COLUMN void_reason VARCHAR(255) NULL;

UPDATE appointments
SET status = 'PENDING'
WHERE status IS NULL OR status = 'Pending';

UPDATE sales
SET status = 'ACTIVE'
WHERE status IS NULL;
