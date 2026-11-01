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
    private static final String PASSWORD = "1234";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME 
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found in classpath: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }

    public static void initializeTables() {
        // Automatically ensure the database exists on MySQL server before connecting to it
        String serverUrl = "jdbc:mysql://" + HOST + ":" + PORT + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try (Connection serverConn = DriverManager.getConnection(serverUrl, USER, PASSWORD);
             Statement serverStmt = serverConn.createStatement()) {
            serverStmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        } catch (SQLException ignored) {
            // Non-fatal if user lacks global CREATE privilege; will attempt direct connection next
        }

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(50) NOT NULL UNIQUE, "
                    + "password VARCHAR(100) NOT NULL, "
                    + "role VARCHAR(50) NOT NULL)");

            stmt.executeUpdate("INSERT IGNORE INTO users (username, password, role) VALUES "
                    + "('admin', 'admin123', 'Admin'), "
                    + "('staff', 'staff123', 'Receptionist'), "
                    + "('doctor', 'doc123', 'Doctor')");

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

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS beds ("
                    + "id VARCHAR(20) PRIMARY KEY, "
                    + "ward_name VARCHAR(100) NOT NULL, "
                    + "bed_number VARCHAR(30) NOT NULL, "
                    + "patient VARCHAR(150) DEFAULT '', "
                    + "status VARCHAR(30) DEFAULT 'Available', "
                    + "daily_charge DOUBLE NOT NULL DEFAULT 50.0, "
                    + "admission_date VARCHAR(30) DEFAULT '', "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS medicines ("
                    + "id VARCHAR(20) PRIMARY KEY, "
                    + "name VARCHAR(100) NOT NULL, "
                    + "category VARCHAR(100) NOT NULL, "
                    + "stock_qty INT NOT NULL DEFAULT 0, "
                    + "unit_price DOUBLE NOT NULL DEFAULT 0.0, "
                    + "expiry_date VARCHAR(30) NOT NULL, "
                    + "manufacturer VARCHAR(100) NOT NULL, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        } catch (SQLException e) {
            System.err.println("Error initializing tables: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Testing MySQL connection to " + DB_NAME + "...");
        if (testConnection()) {
            System.out.println("Connection SUCCESSFUL!");
            initializeTables();
            System.out.println("All 7 tables initialized.");
        } else {
            System.out.println("Connection FAILED!");
        }
    }
}
