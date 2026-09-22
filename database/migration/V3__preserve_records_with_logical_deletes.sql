-- Preserve records and replace physical deletes with logical state changes.
ALTER TABLE roles ADD COLUMN active BIT(1) NOT NULL DEFAULT b'1';
ALTER TABLE clients ADD COLUMN active BIT(1) NOT NULL DEFAULT b'1';
ALTER TABLE sales ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE sales ADD COLUMN voided_at DATETIME NULL;
ALTER TABLE sales ADD COLUMN void_reason VARCHAR(255) NULL;

CREATE INDEX idx_sales_status_datetime ON sales(status, sale_datetime);
CREATE INDEX idx_appointments_datetime ON appointments(appointment_datetime);
CREATE INDEX idx_appointments_status_datetime ON appointments(status, appointment_datetime);
CREATE INDEX idx_sale_details_service ON sale_details(service_id);
CREATE INDEX idx_sale_details_employee ON sale_details(employee_id);

UPDATE appointments
SET status = 'PENDING'
WHERE appointment_id > 0
  AND (status IS NULL OR status = 'Pending');

UPDATE sales
SET status = 'ACTIVE'
WHERE sale_id > 0
  AND status IS NULL;
