package hospital.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    private static final String BASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "hospital_db";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static Connection getServerConnection() throws SQLException {
        return DriverManager.getConnection(BASE_URL, USER, PASSWORD);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(BASE_URL + DB_NAME, USER, PASSWORD);
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}