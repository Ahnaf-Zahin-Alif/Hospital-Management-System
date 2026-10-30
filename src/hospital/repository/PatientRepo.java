package hospital.repository;

import hospital.model.Patient;
import hospital.utils.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientRepo {

    public List<Patient> findAll() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY id ASC";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Patient p = mapRow(rs);
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching patients: " + e.getMessage());
        }
        return list;
    }

    public Patient findById(String id) {
        String sql = "SELECT * FROM patients WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding patient by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean save(Patient p) {
        String sql = "INSERT INTO patients (id, name, age, gender, blood_group, phone, diagnosis, address) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getId());
            ps.setString(2, p.getName());
            ps.setInt(3, p.getAge());
            ps.setString(4, p.getGender());
            ps.setString(5, p.getBloodGroup());
            ps.setString(6, p.getPhone());
            ps.setString(7, p.getDiagnosis());
            ps.setString(8, p.getAddress());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving patient: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Patient p) {
        String sql = "UPDATE patients SET name = ?, age = ?, gender = ?, blood_group = ?, "
                + "phone = ?, diagnosis = ?, address = ? WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setInt(2, p.getAge());
            ps.setString(3, p.getGender());
            ps.setString(4, p.getBloodGroup());
            ps.setString(5, p.getPhone());
            ps.setString(6, p.getDiagnosis());
            ps.setString(7, p.getAddress());
            ps.setString(8, p.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating patient: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM patients WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting patient: " + e.getMessage());
            return false;
        }
    }

    public List<Patient> search(String keyword) {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE name LIKE ? OR id LIKE ? OR phone LIKE ? ORDER BY id ASC";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String term = "%" + keyword + "%";
            ps.setString(1, term);
            ps.setString(2, term);
            ps.setString(3, term);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching patients: " + e.getMessage());
        }
        return list;
    }

    public String generateNextId() {
        String sql = "SELECT id FROM patients WHERE id LIKE 'P-%' ORDER BY id DESC LIMIT 1";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("id");
                String numPart = lastId.replace("P-", "").trim();
                int nextNum = Integer.parseInt(numPart) + 1;
                return "P-" + nextNum;
            }
        } catch (Exception e) {
            // fallback
        }
        return "P-101";
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        return new Patient(
                rs.getString("id"),
                rs.getString("name"),
                rs.getInt("age"),
                rs.getString("gender"),
                rs.getString("blood_group"),
                rs.getString("phone"),
                rs.getString("diagnosis"),
                rs.getString("address")
        );
    }
}
