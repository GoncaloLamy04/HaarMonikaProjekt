-- TestData:
-- Medarbejdere
INSERT INTO employees (name, email, username, password, role)
VALUES
    ('Monika', 'monika@haarmonika.dk', 'monika', 'password123', 'admin'),
    ('Anna', 'anna@haarmonika.dk', 'anna', 'password123', 'frisør'),
    ('Goncalo', 'goncalo@haarmonika.dk', 'goncalo', 'password123', 'frisør'),
    ('Mattias', 'mattias@haarmonika.dk', 'mattias', 'password123', 'frisør'),
    ('Nicki', 'nicki@haarmonika.dk', 'nicki', 'password123', 'frisør');

-- Kunder
INSERT INTO customers (name, email)
VALUES
    ('Test Kunde', 'test@test.dk'),
    ('Jane Doe', 'jane@test.dk'),
    ('Lars Jensen', 'lars@test.dk'),
    ('Sofie Nielsen', 'sofie@test.dk');

-- Behandlinger
INSERT INTO treatments (treatment_type, duration_minutes)
VALUES
    ('Klipning', 30),
    ('Farvning', 60),
    ('Vask', 15),
    ('Føntørring', 20),
    ('Skæg trimning', 20);

-- Test bookinger (fordelt på flere medarbejdere)
INSERT INTO appointments (customer_id, employee_id, name, email, start_time, duration_minutes, cancelled)
VALUES
    (1, 2, 'Test Kunde', 'test@test.dk', '2026-03-05 09:00:00', 30, false),
    (2, 2, 'Jane Doe', 'jane@test.dk', '2026-03-05 10:00:00', 60, false),
    (3, 3, 'Lars Jensen', 'lars@test.dk', '2026-03-05 09:00:00', 30, false),
    (4, 3, 'Sofie Nielsen', 'sofie@test.dk', '2026-03-05 10:30:00', 60, false),
    (1, 4, 'Test Kunde', 'test@test.dk', '2026-03-05 11:00:00', 20, false),
    (2, 2, 'Jane Doe', 'jane@test.dk', '2026-03-05 13:00:00', 30, true);

-- Behandlinger til bookinger
INSERT INTO appointment_treatments (appointment_id, treatment_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 1),
    (4, 2),
    (5, 4),
    (6, 1);

-- Udløbne bookinger (til test af EXPIRED status)
INSERT INTO appointments (customer_id, employee_id, name, email, start_time, duration_minutes, cancelled)
VALUES
    (1, 2, 'Test Kunde', 'test@test.dk', '2025-01-15 09:00:00', 30, false),
    (2, 3, 'Jane Doe', 'jane@test.dk', '2025-06-10 10:00:00', 60, false),
    (3, 4, 'Lars Jensen', 'lars@test.dk', '2024-11-20 11:00:00', 30, false);

-- Behandlinger til udløbne bookinger
INSERT INTO appointment_treatments (appointment_id, treatment_id)
VALUES
    (7, 1),
    (8, 2),
    (9, 3);