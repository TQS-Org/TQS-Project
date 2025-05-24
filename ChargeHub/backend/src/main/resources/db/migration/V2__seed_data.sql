-- === Clients ===
INSERT INTO client (id, name, password, age, mail, number)
VALUES (
    1,
    'Driver One',
    '$2a$10$2yhQW.Xdv.x1g7DeFIcs6.ZskEQ2SfI4hsyRgp2QxcdbkzgDwgNua',
    30,
    'driver@mail.com',
    '123456789'
);

-- === Stations ===
INSERT INTO station (id, name, brand, latitude, longitude, address, number_of_chargers, opening_hours, closing_hours, price)
VALUES
(1, 'PRIO Borba (Sul)', 'PRIO', 40.19253, -8.50822, 'Coimbra', 3, '7:00', '23:00', 0.33),
(2, 'Chamauto - Sociedade Transmontana de Automóveis Lda', 'GALP', 38.8899, -9.04055, 'Lisboa', 1, '10:00', '24:00', 0.33),
(3, 'E. LECLERC BARCELOS', 'LECLERC', 41.1972, -8.51031, 'Porto', 2, '7:30', '19:00', 0.17),
(4, 'VREI Lda.', 'GALP', 39.476826, -8.339649, 'Santarém', 3, '9:00', '20:00', 0.31),
(5, 'Casimiro', 'REPSOL', 39.19317, -9.18156, 'Lisboa', 3, '9:00', '19:00', 0.25),
(6, 'ALVES BANDEIRA Olival', 'ALVES BANDEIRA', 38.21371, -7.54008, 'Évora', 3, '9:30', '20:00', 0.20),
(7, 'Prio Montalegre', 'PRIO', 38.55458, -9.08845, 'Setúbal', 3, '9:00', '23:00', 0.14),
(8, 'ALCOCHETE  -(Junto ao Freeport)', 'PRIO', 38.06603, -8.77616, 'Setúbal', 2, '9:00', '20:00', 0.19);

-- === Staff ===
INSERT INTO staff (id, name, password, age, mail, number, address, is_active, start_date, end_date, role, station_id)
VALUES
(1, 'Operator One', '$2a$10$xAqHu63a1A8FHxBhrx9bK.3GXMiAAvfS1sXyCAcS0vMizvCICHVgu', 35, 'operator1@mail.com', '111111111', 'Lisboa', true, CURRENT_DATE, null, 'OPERATOR', 2),
(2, 'Operator Two', '$2a$10$xAqHu63a1A8FHxBhrx9bK.3GXMiAAvfS1sXyCAcS0vMizvCICHVgu', 28, 'operator2@mail.com', '222222222', 'Porto', true, CURRENT_DATE, null, 'OPERATOR', 3),
(3, 'Admin One', '$2a$10$4iXbS1Ul8cT3jAXVk/Kw9e4qpE2oRURK1pVXaITXHmPhFPuuNI82i', 40, 'admin@mail.com', '999999999', 'Santarém', true, CURRENT_DATE, null, 'ADMIN', 1);

-- === Chargers ===
INSERT INTO charger (id, station_id, type, power, available, connector_type)
VALUES
-- Station 1
(1, 1, 'DC', 50.0, true, 'CCS'),
(2, 1, 'AC', 22.0, true, 'Type2'),
(3, 1, 'DC', 100.0, false, 'CHAdeMO'),

-- Station 2
(4, 2, 'AC', 22.0, true, 'Type2'),

-- Station 3
(5, 3, 'DC', 50.0, true, 'CCS'),
(6, 3, 'AC', 11.0, true, 'Type2'),

-- Station 4
(7, 4, 'DC', 150.0, true, 'CCS'),
(8, 4, 'DC', 100.0, false, 'CHAdeMO'),
(9, 4, 'AC', 22.0, true, 'Type2'),

-- Station 5
(10, 5, 'AC', 11.0, true, 'Type2'),
(11, 5, 'AC', 22.0, true, 'Type2'),
(12, 5, 'DC', 50.0, true, 'CCS'),

-- Station 6
(13, 6, 'AC', 22.0, true, 'Type2'),
(14, 6, 'DC', 100.0, true, 'CCS'),
(15, 6, 'AC', 11.0, false, 'Type2'),

-- Station 7
(16, 7, 'DC', 100.0, true, 'CHAdeMO'),
(17, 7, 'AC', 22.0, true, 'Type2'),
(18, 7, 'DC', 150.0, true, 'CCS'),

-- Station 8
(19, 8, 'AC', 11.0, true, 'Type2'),
(20, 8, 'AC', 22.0, false, 'Type2');
