CREATE DATABASE glamgest_db;

#DROP DATABASE glamgest_db;

USE glamgest_db;

-- ROLES

CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(150)
);

-- USERS

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    active TINYINT DEFAULT 1,

    CONSTRAINT fk_users_roles
        FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

-- CLIENTS

CREATE TABLE clients (
    client_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- WORKERS

CREATE TABLE employees (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    active TINYINT DEFAULT 1
);

-- SERVICES

CREATE TABLE services (
    service_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price INT NOT NULL,
    duration_minutes INT,
    active TINYINT DEFAULT 1
);

-- APPOINTMENTS

CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    employee_id INT NOT NULL,
    user_id INT NOT NULL,
    service_id INT NOT NULL,
    appointment_datetime DATETIME NOT NULL,
    status VARCHAR(30),
    notes TEXT,

    CONSTRAINT fk_appointments_clients
        FOREIGN KEY (client_id) REFERENCES clients(client_id),

    CONSTRAINT fk_appointments_employees
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id),

    CONSTRAINT fk_appointments_users
        FOREIGN KEY (user_id) REFERENCES users(user_id),

    CONSTRAINT fk_appointments_services
        FOREIGN KEY (service_id) REFERENCES services(service_id)
);

-- SALES

CREATE TABLE sales (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    user_id INT NOT NULL,
    sale_datetime DATETIME DEFAULT CURRENT_TIMESTAMP,
    total INT NOT NULL,

    CONSTRAINT fk_sales_clients
        FOREIGN KEY (client_id) REFERENCES clients(client_id),

    CONSTRAINT fk_sales_users
        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- SALE DETAILS

CREATE TABLE sale_details (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NULL,
    sale_id INT NOT NULL,
    service_id INT NOT NULL,
    employee_id INT NOT NULL,
    quantity INT DEFAULT 1,
    unit_price INT NOT NULL,
    subtotal INT,

    CONSTRAINT fk_sale_details_appointments
        FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id),

    CONSTRAINT fk_sale_details_sales
        FOREIGN KEY (sale_id) REFERENCES sales(sale_id),

    CONSTRAINT fk_sale_details_services
        FOREIGN KEY (service_id) REFERENCES services(service_id),

    CONSTRAINT fk_sale_details_workers
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);
