CREATE DATABASE IF NOT EXISTS appointmenttable;
USE appointmenttable;

CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(50)
);

CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100)
);

CREATE TABLE treatments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    treatment_type VARCHAR(100) NOT NULL,
    duration_minutes INT NOT NULL
);

CREATE TABLE appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    employee_id INT NOT NULL,
    name VARCHAR(100),
    email VARCHAR(100),
    start_time DATETIME NOT NULL,
    duration_minutes INT NOT NULL,
    cancelled BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE appointment_treatments (
    appointment_id INT,
    treatment_id INT,
    PRIMARY KEY (appointment_id, treatment_id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    FOREIGN KEY (treatment_id) REFERENCES treatments(id)
);

CREATE TABLE cleanup_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    deleted_count INT,
    error_message VARCHAR(255),
    logged_at DATETIME DEFAULT NOW()
);

-- TestData:
-- Medarbejdere
INSERT INTO employees (name, email, username, password, role)
VALUES ('Anna', 'anna@haarmonika.dk', 'anna', 'password123', 'frisør');

-- Testkunde
INSERT INTO customers (name, email)
VALUES ('Test Kunde', 'test@test.dk');

-- Behandlinger
INSERT INTO treatments (treatment_type, duration_minutes)
VALUES ('Klipning', 30),
       ('Farvning', 60),
       ('Vask', 15);

-- Test bookinger
INSERT INTO appointments (customer_id, employee_id, name, email, start_time, duration_minutes, cancelled)
VALUES (1, 1, 'Test Person', 'test@test.dk', '2026-03-05 10:00:00', 30, false),
       (1, 1, 'Jane Doe', 'jane@test.dk', '2026-03-05 11:00:00', 60, false);

INSERT INTO appointment_treatments (appointment_id, treatment_id)
VALUES (1, 1),
       (2, 2);