package hospital.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnection {

    private static final String HOST = "localhost";
    private static final int PORT = 3306;
    private static final String DB_NAME = "hospital_db";
    private static final String USER = "root";

    // Common passwords across different XAMPP and MySQL setups
    private static final String[] PASSWORD_CANDIDATES = {"1234", "", "root", "admin"};
    private static volatile String activePassword = null;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found in classpath: " + e.getMessage());
        }
    }

    private static String buildUrl(String dbName) {
        String base = "jdbc:mysql://" + HOST + ":" + PORT + "/";
        if (dbName != null && !dbName.isEmpty()) {
            base += dbName;
        }
        return base + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    public static Connection getConnection() throws SQLException {
        if (activePassword != null) {
            try {
                return DriverManager.getConnection(buildUrl(DB_NAME), USER, activePassword);
            } catch (SQLException e) {
                // If the cached password failed, reset and re-detect
                activePassword = null;
            }
        }

        SQLException lastException = null;
        for (String candidate : PASSWORD_CANDIDATES) {
            try {
                Connection conn = DriverManager.getConnection(buildUrl(DB_NAME), USER, candidate);
                activePassword = candidate;
                return conn;
            } catch (SQLException ex) {
                lastException = ex;
            }
        }

        throw (lastException != null) ? lastException : new SQLException("Unable to connect to MySQL database.");
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static void initializeTables() {
        // Step 1: Automatically ensure database exists on MySQL server
        ensureDatabaseExists();

        // Step 2: Create all 7 tables and insert default seed records if needed
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // 1. Users Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(50) NOT NULL UNIQUE, "
                    + "password VARCHAR(100) NOT NULL, "
                    + "role VARCHAR(50) NOT NULL)");

            stmt.executeUpdate("INSERT IGNORE INTO users (username, password, role) VALUES "
                    + "('admin', 'admin123', 'Admin'), "
                    + "('staff', 'staff123', 'Receptionist'), "
                    + "('doctor', 'doc123', 'Doctor')");

            // 2. Patients Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS patients ("
                    + "id VARCHAR(20) PRIMARY KEY, "
                    + "name VARCHAR(100) NOT NULL, "
                    + "age INT NOT NULL, "
                    + "gender VARCHAR(20), "
                    + "blood_group VARCHAR(10), "
                    + "phone VARCHAR(30) NOT NULL, "
                    + "diagnosis VARCHAR(255), "
                    + "address TEXT, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.executeUpdate("INSERT IGNORE INTO patients (id, name, age, gender, blood_group, phone, diagnosis, address) VALUES "
                    + "('P-101', 'Emma Watson', 29, 'Female', 'O+', '+1 555-0192', 'Acute Bronchitis', '452 Elm Street, Suite 3'), "
                    + "('P-102', 'Robert Downey', 48, 'Male', 'A+', '+1 555-0843', 'Hypertension Stage 1', '742 Evergreen Terrace'), "
                    + "('P-103', 'Sophia Martinez', 34, 'Female', 'B+', '+1 555-0321', 'Routine Prenatal Exam', '1208 Meadow Lane'), "
                    + "('P-104', 'David Miller', 62, 'Male', 'AB-', '+1 555-0774', 'Type 2 Diabetes Checkup', '890 Pinecrest Blvd')");

            // 3. Doctors Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS doctors ("
                    + "id VARCHAR(20) PRIMARY KEY, "
                    + "name VARCHAR(100) NOT NULL, "
                    + "specialty VARCHAR(100), "
                    + "qualification VARCHAR(100), "
                    + "phone VARCHAR(30) NOT NULL, "
                    + "email VARCHAR(100), "
                    + "fee VARCHAR(20) NOT NULL, "
                    + "room VARCHAR(50) NOT NULL, "
                    + "visiting_hours VARCHAR(100), "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.executeUpdate("INSERT IGNORE INTO doctors (id, name, specialty, qualification, phone, email, fee, room, visiting_hours) VALUES "
                    + "('DOC-201', 'Dr. Alexander Wright', 'Cardiology', 'MD, FACC', '+1 555-8821', 'alex.wright@hospital.com', '150', 'Room 401', 'Mon, Wed, Fri (09:00 - 13:00)'), "
                    + "('DOC-202', 'Dr. Elena Rostova', 'Neurology', 'MD, Ph.D.', '+1 555-4490', 'elena.rostova@hospital.com', '180', 'Room 305', 'Tue, Thu (10:00 - 15:00)'), "
                    + "('DOC-203', 'Dr. Marcus Chen', 'Pediatrics', 'MBBS, DCH', '+1 555-7712', 'marcus.chen@hospital.com', '100', 'Room 102', 'Mon - Fri (08:30 - 12:30)'), "
                    + "('DOC-204', 'Dr. Sarah Mitchell', 'Orthopedics', 'MS, FRCS', '+1 555-9014', 'sarah.mitchell@hospital.com', '160', 'Room 210', 'Wed, Sat (11:00 - 16:00)')");

            // 4. Appointments Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS appointments ("
                    + "id VARCHAR(20) PRIMARY KEY, "
                    + "patient VARCHAR(150) NOT NULL, "
                    + "doctor VARCHAR(150) NOT NULL, "
                    + "date VARCHAR(30) NOT NULL, "
                    + "time VARCHAR(30) NOT NULL, "
                    + "department VARCHAR(100), "
                    + "status VARCHAR(30) DEFAULT 'Scheduled', "
                    + "notes TEXT, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.executeUpdate("INSERT IGNORE INTO appointments (id, patient, doctor, date, time, department, status, notes) VALUES "
                    + "('APT-301', 'Emma Watson', 'Dr. Alexander Wright', '2026-10-30', '10:30 AM', 'Cardiology', 'Scheduled', 'Blood pressure follow-up'), "
                    + "('APT-302', 'Robert Downey', 'Dr. Sarah Mitchell', '2026-10-30', '11:30 AM', 'Orthopedics', 'Completed', 'Knee joint pain consultation'), "
                    + "('APT-303', 'Sophia Martinez', 'Dr. Marcus Chen', '2026-10-31', '09:00 AM', 'Pediatrics', 'Scheduled', 'Immunization shot for newborn'), "
                    + "('APT-304', 'David Miller', 'Dr. Elena Rostova', '2026-10-31', '02:30 PM', 'Neurology', 'Cancelled', 'Patient rescheduled due to travel')");

            // 5. Bills Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS bills ("
                    + "id VARCHAR(20) PRIMARY KEY, "
                    + "patient VARCHAR(150) NOT NULL, "
                    + "doctor VARCHAR(150) NOT NULL, "
                    + "consultation_fee DOUBLE NOT NULL DEFAULT 0, "
                    + "treatment_fee DOUBLE NOT NULL DEFAULT 0, "
                    + "medicine_fee DOUBLE NOT NULL DEFAULT 0, "
                    + "total_amount DOUBLE NOT NULL DEFAULT 0, "
                    + "status VARCHAR(30) DEFAULT 'Pending', "
                    + "payment_method VARCHAR(50) DEFAULT 'Cash', "
                    + "bill_date VARCHAR(30) NOT NULL, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.executeUpdate("INSERT IGNORE INTO bills (id, patient, doctor, consultation_fee, treatment_fee, medicine_fee, total_amount, status, payment_method, bill_date) VALUES "
                    + "('BILL-401', 'Emma Watson', 'Dr. Alexander Wright', 150.0, 50.0, 35.0, 235.0, 'Paid', 'Card', '2026-10-30'), "
                    + "('BILL-402', 'Robert Downey', 'Dr. Sarah Mitchell', 160.0, 120.0, 45.0, 325.0, 'Paid', 'Insurance', '2026-10-30'), "
                    + "('BILL-403', 'Sophia Martinez', 'Dr. Marcus Chen', 100.0, 20.0, 15.0, 135.0, 'Pending', 'Cash', '2026-10-31'), "
                    + "('BILL-404', 'David Miller', 'Dr. Elena Rostova', 180.0, 80.0, 60.0, 320.0, 'Pending', 'Cash', '2026-10-31')");

            // 6. Beds Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS beds ("
                    + "id VARCHAR(20) PRIMARY KEY, "
                    + "ward_name VARCHAR(100) NOT NULL, "
                    + "bed_number VARCHAR(30) NOT NULL, "
                    + "patient VARCHAR(150) DEFAULT '', "
                    + "status VARCHAR(30) DEFAULT 'Available', "
                    + "daily_charge DOUBLE NOT NULL DEFAULT 50.0, "
                    + "admission_date VARCHAR(30) DEFAULT '', "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.executeUpdate("INSERT IGNORE INTO beds (id, ward_name, bed_number, patient, status, daily_charge, admission_date) VALUES "
                    + "('BED-101', 'ICU', 'ICU-01', 'David Miller', 'Occupied', 250.0, '2026-10-28'), "
                    + "('BED-102', 'ICU', 'ICU-02', '', 'Available', 250.0, ''), "
                    + "('BED-103', 'General Ward', 'GW-01', 'Emma Watson', 'Occupied', 75.0, '2026-10-29'), "
                    + "('BED-104', 'General Ward', 'GW-02', '', 'Available', 75.0, ''), "
                    + "('BED-105', 'Emergency', 'EMG-01', 'Robert Downey', 'Occupied', 150.0, '2026-10-30'), "
                    + "('BED-106', 'Emergency', 'EMG-02', '', 'Available', 150.0, ''), "
                    + "('BED-107', 'Maternity', 'MAT-01', 'Sophia Martinez', 'Occupied', 120.0, '2026-10-27'), "
                    + "('BED-108', 'Pediatric', 'PED-01', '', 'Available', 90.0, '')");

            // 7. Medicines Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS medicines ("
                    + "id VARCHAR(20) PRIMARY KEY, "
                    + "name VARCHAR(100) NOT NULL, "
                    + "category VARCHAR(100) NOT NULL, "
                    + "stock_qty INT NOT NULL DEFAULT 0, "
                    + "unit_price DOUBLE NOT NULL DEFAULT 0.0, "
                    + "expiry_date VARCHAR(30) NOT NULL, "
                    + "manufacturer VARCHAR(100) NOT NULL, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.executeUpdate("INSERT IGNORE INTO medicines (id, name, category, stock_qty, unit_price, expiry_date, manufacturer) VALUES "
                    + "('MED-501', 'Amoxicillin 500mg', 'Antibiotics', 120, 12.50, '2027-08-15', 'Pfizer Pharma'), "
                    + "('MED-502', 'Paracetamol 650mg', 'Analgesics / Antipyretic', 350, 4.00, '2028-02-20', 'GSK Healthcare'), "
                    + "('MED-503', 'Atorvastatin 20mg', 'Cardiology', 85, 18.00, '2027-11-10', 'Novartis'), "
                    + "('MED-504', 'Metformin 850mg', 'Diabetes', 140, 9.50, '2027-05-30', 'Merck Group'), "
                    + "('MED-505', 'Ibuprofen 400mg', 'Anti-inflammatory', 8, 7.20, '2026-12-31', 'Bayer Health'), "
                    + "('MED-506', 'Omeprazole 20mg', 'Gastrointestinal', 210, 8.50, '2027-09-18', 'AstraZeneca'), "
                    + "('MED-507', 'Cetirizine 10mg', 'Antihistamine', 5, 5.00, '2026-11-25', 'Sanofi')");

            System.out.println("[Database] All 7 tables in '" + DB_NAME + "' verified and initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing tables: " + e.getMessage());
        }
    }

    private static void ensureDatabaseExists() {
        String serverUrl = buildUrl("");
        for (String candidate : PASSWORD_CANDIDATES) {
            try (Connection serverConn = DriverManager.getConnection(serverUrl, USER, candidate);
                 Statement serverStmt = serverConn.createStatement()) {
                serverStmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
                activePassword = candidate;
                return;
            } catch (SQLException ignored) {
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Testing MySQL connection to " + DB_NAME + "...");
        initializeTables();
        if (testConnection()) {
            System.out.println("Connection SUCCESSFUL!");
        } else {
            System.err.println("Connection FAILED! Please ensure MySQL is running in XAMPP on port 3306.");
        }
    }
}
