-- === CLIENT ===
CREATE TABLE client (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    age INT NOT NULL,
    mail VARCHAR(255) NOT NULL UNIQUE,
    number VARCHAR(20)
);

-- === STATION ===
CREATE TABLE station (
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
CREATE TABLE staff (
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
CREATE TABLE charger (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    station_id BIGINT,
    type VARCHAR(50),
    power DOUBLE,
    available BOOLEAN,
    connector_type VARCHAR(50),
    CONSTRAINT fk_charger_station FOREIGN KEY (station_id) REFERENCES station(id)
);
