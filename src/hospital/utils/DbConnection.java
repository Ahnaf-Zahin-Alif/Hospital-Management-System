package hospital.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnection {

    // Database credentials
    private static final String HOST = "localhost";
    private static final int PORT = 3306;
    private static final String DB_NAME = "hopital_db"; // As created in phpMyAdmin
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

    /**
     * Initializes database tables if they do not exist yet.
     */
    public static void initializeTables() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Users table for authentication
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(50) NOT NULL UNIQUE, "
                    + "password VARCHAR(100) NOT NULL, "
                    + "role VARCHAR(50) NOT NULL)");

            // Seed default users if empty
            stmt.executeUpdate("INSERT IGNORE INTO users (username, password, role) VALUES "
                    + "('admin', 'admin123', 'Admin'), "
                    + "('staff', 'staff123', 'Receptionist'), "
                    + "('doctor', 'doc123', 'Doctor')");

            // Patients table
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

            // Doctors table
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

            // Appointments table
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

        } catch (SQLException e) {
            System.err.println("Error initializing tables: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Testing MySQL connection to " + DB_NAME + "...");
        boolean success = testConnection();
        if (success) {
            System.out.println("Connection SUCCESSFUL!");
            initializeTables();
            System.out.println("Tables verified / initialized.");
        } else {
            System.out.println("Connection FAILED!");
        }
    }
}
