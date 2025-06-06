-- === Clients ===
INSERT INTO client (id, name, password, age, mail, number)
SELECT 1, 'Driver One', '$2a$10$2yhQW.Xdv.x1g7DeFIcs6.ZskEQ2SfI4hsyRgp2QxcdbkzgDwgNua', 30, 'driver@mail.com', '123456789'
WHERE NOT EXISTS (SELECT 1 FROM client WHERE mail = 'driver@mail.com');

-- === Stations ===
INSERT INTO station (id, name, brand, latitude, longitude, address, number_of_chargers, opening_hours, closing_hours, price)
SELECT 1, 'PRIO Borba (Sul)', 'PRIO', 40.19253, -8.50822, 'Coimbra', 3, '0:00', '23:55', 0.33
WHERE NOT EXISTS (SELECT 1 FROM station WHERE latitude = 40.19253 AND longitude = -8.50822);

INSERT INTO station (id, name, brand, latitude, longitude, address, number_of_chargers, opening_hours, closing_hours, price)
SELECT 2, 'Chamauto - Sociedade Transmontana de Automóveis Lda', 'GALP', 38.8899, -9.04055, 'Lisboa', 1, '10:00', '23:59', 0.33
WHERE NOT EXISTS (SELECT 1 FROM station WHERE latitude = 38.8899 AND longitude = -9.04055);

INSERT INTO station (id, name, brand, latitude, longitude, address, number_of_chargers, opening_hours, closing_hours, price)
SELECT 3, 'E. LECLERC BARCELOS', 'LECLERC', 41.1972, -8.51031, 'Porto', 2, '7:30', '19:00', 0.17
WHERE NOT EXISTS (SELECT 1 FROM station WHERE latitude = 41.1972 AND longitude = -8.51031);

INSERT INTO station (id, name, brand, latitude, longitude, address, number_of_chargers, opening_hours, closing_hours, price)
SELECT 4, 'VREI Lda.', 'GALP', 39.476826, -8.339649, 'Santarém', 3, '9:00', '20:00', 0.31
WHERE NOT EXISTS (SELECT 1 FROM station WHERE latitude = 39.476826 AND longitude = -8.339649);

INSERT INTO station (id, name, brand, latitude, longitude, address, number_of_chargers, opening_hours, closing_hours, price)
SELECT 5, 'Casimiro', 'REPSOL', 39.19317, -9.18156, 'Lisboa', 3, '9:00', '19:00', 0.25
WHERE NOT EXISTS (SELECT 1 FROM station WHERE latitude = 39.19317 AND longitude = -9.18156);

INSERT INTO station (id, name, brand, latitude, longitude, address, number_of_chargers, opening_hours, closing_hours, price)
SELECT 6, 'ALVES BANDEIRA Olival', 'ALVES BANDEIRA', 38.21371, -7.54008, 'Évora', 3, '9:30', '20:00', 0.20
WHERE NOT EXISTS (SELECT 1 FROM station WHERE latitude = 38.21371 AND longitude = -7.54008);

INSERT INTO station (id, name, brand, latitude, longitude, address, number_of_chargers, opening_hours, closing_hours, price)
SELECT 7, 'Prio Montalegre', 'PRIO', 38.55458, -9.08845, 'Setúbal', 3, '0:00', '23:00', 0.14
WHERE NOT EXISTS (SELECT 1 FROM station WHERE latitude = 38.55458 AND longitude = -9.08845);

INSERT INTO station (id, name, brand, latitude, longitude, address, number_of_chargers, opening_hours, closing_hours, price)
SELECT 8, 'ALCOCHETE  -(Junto ao Freeport)', 'PRIO', 38.06603, -8.77616, 'Setúbal', 2, '9:00', '20:00', 0.19
WHERE NOT EXISTS (SELECT 1 FROM station WHERE latitude = 38.06603 AND longitude = -8.77616);

-- === Staff ===
INSERT INTO staff (id, name, password, age, mail, number, address, is_active, start_date, end_date, role, station_id)
SELECT 1, 'Operator One', '$2a$10$xAqHu63a1A8FHxBhrx9bK.3GXMiAAvfS1sXyCAcS0vMizvCICHVgu', 35, 'operator1@mail.com', '111111111', 'Lisboa', true, CURRENT_DATE, null, 'OPERATOR', 2
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE mail = 'operator1@mail.com');

INSERT INTO staff (id, name, password, age, mail, number, address, is_active, start_date, end_date, role, station_id)
SELECT 2, 'Operator Two', '$2a$10$xAqHu63a1A8FHxBhrx9bK.3GXMiAAvfS1sXyCAcS0vMizvCICHVgu', 28, 'operator2@mail.com', '222222222', 'Porto', true, CURRENT_DATE, null, 'OPERATOR', 3
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE mail = 'operator2@mail.com');

INSERT INTO staff (id, name, password, age, mail, number, address, is_active, start_date, end_date, role, station_id)
SELECT 3, 'Admin One', '$2a$10$4iXbS1Ul8cT3jAXVk/Kw9e4qpE2oRURK1pVXaITXHmPhFPuuNI82i', 40, 'admin@mail.com', '999999999', 'Santarém', true, CURRENT_DATE, null, 'ADMIN', 1
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE mail = 'admin@mail.com');

-- === Chargers ===
INSERT INTO charger (id, station_id, type, power, available, connector_type)
SELECT 1, 1, 'DC', 50.0, true, 'CCS'
WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 1);

INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 2, 1, 'AC', 22.0, true, 'Type2' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 2);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 3, 1, 'DC', 100.0, false, 'CHAdeMO' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 3);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 4, 2, 'AC', 22.0, true, 'Type2' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 4);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 5, 3, 'DC', 50.0, true, 'CCS' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 5);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 6, 3, 'AC', 11.0, true, 'Type2' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 6);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 7, 4, 'DC', 150.0, true, 'CCS' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 7);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 8, 4, 'DC', 100.0, false, 'CHAdeMO' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 8);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 9, 4, 'AC', 22.0, true, 'Type2' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 9);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 10, 5, 'AC', 11.0, true, 'Type2' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 10);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 11, 5, 'AC', 22.0, true, 'Type2' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 11);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 12, 5, 'DC', 50.0, true, 'CCS' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 12);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 13, 6, 'AC', 22.0, true, 'Type2' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 13);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 14, 6, 'DC', 100.0, true, 'CCS' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 14);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 15, 6, 'AC', 11.0, false, 'Type2' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 15);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 16, 7, 'DC', 100.0, true, 'CHAdeMO' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 16);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 17, 7, 'AC', 22.0, true, 'Type2' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 17);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 18, 7, 'DC', 150.0, true, 'CCS' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 18);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 19, 8, 'AC', 11.0, true, 'Type2' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 19);
INSERT INTO charger (id, station_id, type, power, available, connector_type) SELECT 20, 8, 'AC', 22.0, false, 'Type2' WHERE NOT EXISTS (SELECT 1 FROM charger WHERE id = 20);