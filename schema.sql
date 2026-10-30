CREATE DATABASE IF NOT EXISTS hopital_db;
USE hopital_db;

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

INSERT IGNORE INTO patients (id, name, age, gender, blood_group, phone, diagnosis, address) VALUES
('P-101', 'Emma Watson', 29, 'Female', 'O+', '+1 555-0192', 'Acute Bronchitis', '452 Elm Street, Suite 3'),
('P-102', 'Robert Downey', 48, 'Male', 'A+', '+1 555-0843', 'Hypertension Stage 1', '742 Evergreen Terrace'),
('P-103', 'Sophia Martinez', 34, 'Female', 'B+', '+1 555-0321', 'Routine Prenatal Exam', '1208 Meadow Lane'),
('P-104', 'David Miller', 62, 'Male', 'AB-', '+1 555-0774', 'Type 2 Diabetes Checkup', '890 Pinecrest Blvd');

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

INSERT IGNORE INTO doctors (id, name, specialty, qualification, phone, email, fee, room, visiting_hours) VALUES
('DOC-201', 'Dr. Alexander Wright', 'Cardiology', 'MD, FACC', '+1 555-8821', 'alex.wright@hospital.com', '150', 'Room 401', 'Mon, Wed, Fri (09:00 - 13:00)'),
('DOC-202', 'Dr. Elena Rostova', 'Neurology', 'MD, Ph.D.', '+1 555-4490', 'elena.rostova@hospital.com', '180', 'Room 305', 'Tue, Thu (10:00 - 15:00)'),
('DOC-203', 'Dr. Marcus Chen', 'Pediatrics', 'MBBS, DCH', '+1 555-7712', 'marcus.chen@hospital.com', '100', 'Room 102', 'Mon - Fri (08:30 - 12:30)'),
('DOC-204', 'Dr. Sarah Mitchell', 'Orthopedics', 'MS, FRCS', '+1 555-9014', 'sarah.mitchell@hospital.com', '160', 'Room 210', 'Wed, Sat (11:00 - 16:00)');

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

INSERT IGNORE INTO appointments (id, patient, doctor, date, time, department, status, notes) VALUES
('APT-301', 'Emma Watson', 'Dr. Alexander Wright', '2026-10-30', '10:30 AM', 'Cardiology', 'Scheduled', 'Blood pressure follow-up'),
('APT-302', 'Robert Downey', 'Dr. Sarah Mitchell', '2026-10-30', '11:30 AM', 'Orthopedics', 'Completed', 'Knee joint pain consultation'),
('APT-303', 'Sophia Martinez', 'Dr. Marcus Chen', '2026-10-31', '09:00 AM', 'Pediatrics', 'Scheduled', 'Immunization shot for newborn'),
('APT-304', 'David Miller', 'Dr. Elena Rostova', '2026-10-31', '02:30 PM', 'Neurology', 'Cancelled', 'Patient rescheduled due to travel');

CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;
CREATE TABLE IF NOT EXISTS users LIKE hopital_db.users;
INSERT IGNORE INTO users SELECT * FROM hopital_db.users;
CREATE TABLE IF NOT EXISTS patients LIKE hopital_db.patients;
INSERT IGNORE INTO patients SELECT * FROM hopital_db.patients;
CREATE TABLE IF NOT EXISTS doctors LIKE hopital_db.doctors;
INSERT IGNORE INTO doctors SELECT * FROM hopital_db.doctors;
CREATE TABLE IF NOT EXISTS appointments LIKE hopital_db.appointments;
INSERT IGNORE INTO appointments SELECT * FROM hopital_db.appointments;