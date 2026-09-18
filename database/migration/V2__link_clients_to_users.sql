-- Apply this migration only to an existing GlamGest database.
-- Clean duplicate client emails before adding the unique constraint.

INSERT INTO roles (name, description)
SELECT 'CLIENT', 'Customer with access to own profile and appointments'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'CLIENT');

ALTER TABLE clients ADD COLUMN user_id INT NULL;
ALTER TABLE clients ADD CONSTRAINT uq_clients_email UNIQUE (email);
ALTER TABLE clients ADD CONSTRAINT uq_clients_user UNIQUE (user_id);
ALTER TABLE clients ADD CONSTRAINT fk_clients_user
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL;

ALTER TABLE roles ADD CONSTRAINT uq_roles_name UNIQUE (name);
ALTER TABLE employees ADD CONSTRAINT uq_employees_phone UNIQUE (phone);
ALTER TABLE services ADD CONSTRAINT uq_services_name UNIQUE (name);
