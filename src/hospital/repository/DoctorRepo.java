package hospital.repository;

import hospital.model.Doctor;
import hospital.utils.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorRepo {

    public List<Doctor> findAll() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY id ASC";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching doctors: " + e.getMessage());
        }
        return list;
    }

    public Doctor findById(String id) {
        String sql = "SELECT * FROM doctors WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding doctor by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean save(Doctor d) {
        String sql = "INSERT INTO doctors (id, name, specialty, qualification, phone, email, fee, room, visiting_hours) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, d.getId());
            ps.setString(2, d.getName());
            ps.setString(3, d.getSpecialty());
            ps.setString(4, d.getQualification());
            ps.setString(5, d.getPhone());
            ps.setString(6, d.getEmail());
            ps.setString(7, d.getFee());
            ps.setString(8, d.getRoom());
            ps.setString(9, d.getVisitingHours());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving doctor: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Doctor d) {
        String sql = "UPDATE doctors SET name = ?, specialty = ?, qualification = ?, "
                + "phone = ?, email = ?, fee = ?, room = ?, visiting_hours = ? WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, d.getName());
            ps.setString(2, d.getSpecialty());
            ps.setString(3, d.getQualification());
            ps.setString(4, d.getPhone());
            ps.setString(5, d.getEmail());
            ps.setString(6, d.getFee());
            ps.setString(7, d.getRoom());
            ps.setString(8, d.getVisitingHours());
            ps.setString(9, d.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating doctor: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM doctors WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting doctor: " + e.getMessage());
            return false;
        }
    }

    public List<Doctor> search(String keyword, String specialty) {
        List<Doctor> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM doctors WHERE 1=1");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasSpecialty = specialty != null && !specialty.trim().isEmpty() && !specialty.equalsIgnoreCase("All Specialties");

        if (hasKeyword) {
            sql.append(" AND (name LIKE ? OR id LIKE ? OR phone LIKE ?)");
        }
        if (hasSpecialty) {
            sql.append(" AND specialty = ?");
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
            if (hasSpecialty) {
                ps.setString(paramIndex++, specialty.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching doctors: " + e.getMessage());
        }
        return list;
    }

    public String generateNextId() {
        String sql = "SELECT id FROM doctors WHERE id LIKE 'DOC-%' ORDER BY id DESC LIMIT 1";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("id");
                String numPart = lastId.replace("DOC-", "").trim();
                int nextNum = Integer.parseInt(numPart) + 1;
                return "DOC-" + nextNum;
            }
        } catch (Exception e) {
            // fallback
        }
        return "DOC-201";
    }

    private Doctor mapRow(ResultSet rs) throws SQLException {
        return new Doctor(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("specialty"),
                rs.getString("qualification"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("fee"),
                rs.getString("room"),
                rs.getString("visiting_hours")
        );
    }
}
