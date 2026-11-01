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

INSERT IGNORE INTO bills (id, patient, doctor, consultation_fee, treatment_fee, medicine_fee, total_amount, status, payment_method, bill_date) VALUES
('BILL-401', 'Emma Watson', 'Dr. Alexander Wright', 150.0, 50.0, 35.0, 235.0, 'Paid', 'Card', '2026-10-30'),
('BILL-402', 'Robert Downey', 'Dr. Sarah Mitchell', 160.0, 120.0, 45.0, 325.0, 'Paid', 'Insurance', '2026-10-30'),
('BILL-403', 'Sophia Martinez', 'Dr. Marcus Chen', 100.0, 20.0, 15.0, 135.0, 'Pending', 'Cash', '2026-10-31'),
('BILL-404', 'David Miller', 'Dr. Elena Rostova', 180.0, 80.0, 60.0, 320.0, 'Pending', 'Cash', '2026-10-31');

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

INSERT IGNORE INTO beds (id, ward_name, bed_number, patient, status, daily_charge, admission_date) VALUES
('BED-101', 'ICU', 'ICU-01', 'David Miller', 'Occupied', 250.0, '2026-10-28'),
('BED-102', 'ICU', 'ICU-02', '', 'Available', 250.0, ''),
('BED-103', 'General Ward', 'GW-01', 'Emma Watson', 'Occupied', 75.0, '2026-10-29'),
('BED-104', 'General Ward', 'GW-02', '', 'Available', 75.0, ''),
('BED-105', 'Emergency', 'EMG-01', 'Robert Downey', 'Occupied', 150.0, '2026-10-30'),
('BED-106', 'Emergency', 'EMG-02', '', 'Available', 150.0, ''),
('BED-107', 'Maternity', 'MAT-01', 'Sophia Martinez', 'Occupied', 120.0, '2026-10-27'),
('BED-108', 'Pediatric', 'PED-01', '', 'Available', 90.0, '');

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

INSERT IGNORE INTO medicines (id, name, category, stock_qty, unit_price, expiry_date, manufacturer) VALUES
('MED-501', 'Amoxicillin 500mg', 'Antibiotics', 120, 12.50, '2027-08-15', 'Pfizer Pharma'),
('MED-502', 'Paracetamol 650mg', 'Analgesics / Antipyretic', 350, 4.00, '2028-02-20', 'GSK Healthcare'),
('MED-503', 'Atorvastatin 20mg', 'Cardiology', 85, 18.00, '2027-11-10', 'Novartis'),
('MED-504', 'Metformin 850mg', 'Diabetes', 140, 9.50, '2027-05-30', 'Merck Group'),
('MED-505', 'Ibuprofen 400mg', 'Anti-inflammatory', 8, 7.20, '2026-12-31', 'Bayer Health'),
('MED-506', 'Omeprazole 20mg', 'Gastrointestinal', 210, 8.50, '2027-09-18', 'AstraZeneca'),
('MED-507', 'Cetirizine 10mg', 'Antihistamine', 5, 5.00, '2026-11-25', 'Sanofi');

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
CREATE TABLE IF NOT EXISTS bills LIKE hopital_db.bills;
INSERT IGNORE INTO bills SELECT * FROM hopital_db.bills;
CREATE TABLE IF NOT EXISTS beds LIKE hopital_db.beds;
INSERT IGNORE INTO beds SELECT * FROM hopital_db.beds;
CREATE TABLE IF NOT EXISTS medicines LIKE hopital_db.medicines;
INSERT IGNORE INTO medicines SELECT * FROM hopital_db.medicines;
