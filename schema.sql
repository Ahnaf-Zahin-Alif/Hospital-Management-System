CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL
);

INSERT IGNORE INTO users (username, password, role) VALUES
('admin', 'admin123', 'Admin'),
('staff', 'staff123', 'Receptionist'),
('doctor', 'doc123', 'Doctor');

CREATE TABLE IF NOT EXISTS patients (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(20),
    blood_group VARCHAR(10),
    phone VARCHAR(30) NOT NULL,
    diagnosis VARCHAR(255),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS doctors (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialty VARCHAR(100),
    qualification VARCHAR(100),
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(100),
    fee VARCHAR(20) NOT NULL,
    room VARCHAR(50) NOT NULL,
    visiting_hours VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS appointments (
    id VARCHAR(20) PRIMARY KEY,
    patient VARCHAR(150) NOT NULL,
    doctor VARCHAR(150) NOT NULL,
    date VARCHAR(30) NOT NULL,
    time VARCHAR(30) NOT NULL,
    department VARCHAR(100),
    status VARCHAR(30) DEFAULT 'Scheduled',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bills (
    id VARCHAR(20) PRIMARY KEY,
    patient VARCHAR(150) NOT NULL,
    doctor VARCHAR(150) NOT NULL,
    consultation_fee DOUBLE NOT NULL DEFAULT 0,
    treatment_fee DOUBLE NOT NULL DEFAULT 0,
    medicine_fee DOUBLE NOT NULL DEFAULT 0,
    total_amount DOUBLE NOT NULL DEFAULT 0,
    status VARCHAR(30) DEFAULT 'Pending',
    payment_method VARCHAR(50) DEFAULT 'Cash',
    bill_date VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS beds (
    id VARCHAR(20) PRIMARY KEY,
    ward_name VARCHAR(100) NOT NULL,
    bed_number VARCHAR(30) NOT NULL,
    patient VARCHAR(150) DEFAULT '',
    status VARCHAR(30) DEFAULT 'Available',
    daily_charge DOUBLE NOT NULL DEFAULT 50.0,
    admission_date VARCHAR(30) DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS medicines (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    stock_qty INT NOT NULL DEFAULT 0,
    unit_price DOUBLE NOT NULL DEFAULT 0.0,
    expiry_date VARCHAR(30) NOT NULL,
    manufacturer VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
