package hospital.repository;

import hospital.model.Appointment;
import hospital.utils.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentRepo {

    public List<Appointment> findAll() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY id ASC";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching appointments: " + e.getMessage());
        }
        return list;
    }

    public Appointment findById(String id) {
        String sql = "SELECT * FROM appointments WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding appointment by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean save(Appointment a) {
        String sql = "INSERT INTO appointments (id, patient, doctor, date, time, department, status, notes) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, a.getId());
            ps.setString(2, a.getPatient());
            ps.setString(3, a.getDoctor());
            ps.setString(4, a.getDate());
            ps.setString(5, a.getTime());
            ps.setString(6, a.getDepartment());
            ps.setString(7, a.getStatus());
            ps.setString(8, a.getNotes());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving appointment: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Appointment a) {
        String sql = "UPDATE appointments SET patient = ?, doctor = ?, date = ?, time = ?, "
                + "department = ?, status = ?, notes = ? WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, a.getPatient());
            ps.setString(2, a.getDoctor());
            ps.setString(3, a.getDate());
            ps.setString(4, a.getTime());
            ps.setString(5, a.getDepartment());
            ps.setString(6, a.getStatus());
            ps.setString(7, a.getNotes());
            ps.setString(8, a.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating appointment: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(String id, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating appointment status: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            return false;
        }
    }

    public List<Appointment> search(String keyword, String status) {
        List<Appointment> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM appointments WHERE 1=1");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("All Statuses");

        if (hasKeyword) {
            sql.append(" AND (patient LIKE ? OR doctor LIKE ? OR id LIKE ?)");
        }
        if (hasStatus) {
            sql.append(" AND status = ?");
        }
        sql.append(" ORDER BY id ASC");

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (hasKeyword) {
                String term = "%" + keyword.trim() + "%";
                ps.setString(paramIndex++, term);
                ps.setString(paramIndex++, term);
                ps.setString(paramIndex++, term);
            }
            if (hasStatus) {
                ps.setString(paramIndex++, status.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching appointments: " + e.getMessage());
        }
        return list;
    }

    public String generateNextId() {
        String sql = "SELECT id FROM appointments WHERE id LIKE 'APT-%' ORDER BY id DESC LIMIT 1";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("id");
                String numPart = lastId.replace("APT-", "").trim();
                int nextNum = Integer.parseInt(numPart) + 1;
                return "APT-" + nextNum;
            }
        } catch (Exception e) {
            // fallback
        }
        return "APT-301";
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        return new Appointment(
                rs.getString("id"),
                rs.getString("patient"),
                rs.getString("doctor"),
                rs.getString("date"),
                rs.getString("time"),
                rs.getString("department"),
                rs.getString("status"),
                rs.getString("notes")
        );
    }
}
