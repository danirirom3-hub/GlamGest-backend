CREATE TABLE business_hours (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    day_of_week TINYINT NOT NULL,
    opens_at TIME NULL,
    closes_at TIME NULL,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_business_hours_day UNIQUE (day_of_week),
    CONSTRAINT chk_business_hours_day CHECK (day_of_week BETWEEN 1 AND 7)
);

INSERT INTO business_hours (day_of_week, opens_at, closes_at, is_closed) VALUES
    (1, '09:00:00', '19:00:00', FALSE),
    (2, '09:00:00', '19:00:00', FALSE),
    (3, '09:00:00', '19:00:00', FALSE),
    (4, '09:00:00', '19:00:00', FALSE),
    (5, '09:00:00', '19:00:00', FALSE),
    (6, '09:00:00', '19:00:00', FALSE),
    (7, NULL, NULL, TRUE);

CREATE TABLE business_schedule_exceptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exception_date DATE NOT NULL,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    opens_at TIME NULL,
    closes_at TIME NULL,
    reason VARCHAR(200) NULL,
    CONSTRAINT uq_business_schedule_exception_date UNIQUE (exception_date)
);

CREATE TABLE employee_unavailability (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    starts_at DATETIME NOT NULL,
    ends_at DATETIME NOT NULL,
    reason VARCHAR(200) NULL,
    CONSTRAINT fk_employee_unavailability_employee
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    INDEX idx_employee_unavailability_range (employee_id, starts_at, ends_at)
);
