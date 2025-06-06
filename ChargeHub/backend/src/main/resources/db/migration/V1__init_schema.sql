-- === CLIENT ===
CREATE TABLE IF NOT EXISTS client (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    age INT NOT NULL,
    mail VARCHAR(255) NOT NULL UNIQUE,
    number VARCHAR(20)
);

-- === STATION ===
CREATE TABLE IF NOT EXISTS station (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    address VARCHAR(255),
    number_of_chargers INT,
    opening_hours VARCHAR(20),
    closing_hours VARCHAR(20),
    price DOUBLE
);

-- === STAFF ===
CREATE TABLE IF NOT EXISTS staff (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    age INT NOT NULL,
    mail VARCHAR(255) NOT NULL,
    number VARCHAR(20),
    address VARCHAR(255),
    is_active BOOLEAN,
    start_date DATE,
    end_date DATE,
    role VARCHAR(50),
    station_id BIGINT,
    CONSTRAINT fk_staff_station FOREIGN KEY (station_id) REFERENCES station(id)
);

-- === CHARGER ===
CREATE TABLE IF NOT EXISTS charger (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    station_id BIGINT,
    type VARCHAR(50),
    power DOUBLE,
    available BOOLEAN,
    connector_type VARCHAR(50),
    CONSTRAINT fk_charger_station FOREIGN KEY (station_id) REFERENCES station(id)
);

CREATE TABLE IF NOT EXISTS booking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255) NOT NULL,
    client_id BIGINT NOT NULL,
    charger_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    duration INT NOT NULL,
    CONSTRAINT fk_booking_client FOREIGN KEY (client_id) REFERENCES client(id),
    CONSTRAINT fk_booking_charger FOREIGN KEY (charger_id) REFERENCES charger(id),
    CONSTRAINT uk_booking_token UNIQUE (token)
);

CREATE TABLE IF NOT EXISTS charging_session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    energy_consumed FLOAT,
    price FLOAT,
    session_status VARCHAR(255) NOT NULL,
    CONSTRAINT fk_charging_session_booking FOREIGN KEY (booking)
        REFERENCES booking(id)
);
