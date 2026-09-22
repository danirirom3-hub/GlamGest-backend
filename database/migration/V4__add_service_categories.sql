CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    active BIT(1) NOT NULL DEFAULT b'1',
    CONSTRAINT uq_categories_name UNIQUE (name)
);

ALTER TABLE services ADD COLUMN category_id INT NULL;
ALTER TABLE services ADD CONSTRAINT fk_services_category
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT;

CREATE INDEX idx_services_category ON services(category_id);
