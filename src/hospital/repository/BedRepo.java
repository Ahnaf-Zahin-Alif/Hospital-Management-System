package hospital.repository;

import hospital.model.Bed;
import hospital.utils.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BedRepo {

    public List<Bed> findAll() {
        List<Bed> list = new ArrayList<>();
        String sql = "SELECT * FROM beds ORDER BY id ASC";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching beds: " + e.getMessage());
        }
        return list;
    }

    public Bed findById(String id) {
        String sql = "SELECT * FROM beds WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding bed by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean save(Bed b) {
        String sql = "INSERT INTO beds (id, ward_name, bed_number, patient, status, daily_charge, admission_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, b.getId());
            ps.setString(2, b.getWardName());
            ps.setString(3, b.getBedNumber());
            ps.setString(4, b.getPatient());
            ps.setString(5, b.getStatus());
            ps.setDouble(6, b.getDailyCharge());
            ps.setString(7, b.getAdmissionDate());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving bed: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Bed b) {
        String sql = "UPDATE beds SET ward_name = ?, bed_number = ?, patient = ?, "
                + "status = ?, daily_charge = ?, admission_date = ? WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, b.getWardName());
            ps.setString(2, b.getBedNumber());
            ps.setString(3, b.getPatient());
            ps.setString(4, b.getStatus());
            ps.setDouble(5, b.getDailyCharge());
            ps.setString(6, b.getAdmissionDate());
            ps.setString(7, b.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating bed: " + e.getMessage());
            return false;
        }
    }

    public boolean dischargePatient(String id) {
        String sql = "UPDATE beds SET patient = '', status = 'Available', admission_date = '' WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error discharging patient from bed: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM beds WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting bed: " + e.getMessage());
            return false;
        }
    }

    public String generateNextId() {
        String sql = "SELECT id FROM beds WHERE id LIKE 'BED-%' ORDER BY id DESC LIMIT 1";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("id");
                String numPart = lastId.replace("BED-", "").trim();
                int nextNum = Integer.parseInt(numPart) + 1;
                return "BED-" + nextNum;
            }
        } catch (Exception ignored) {
        }
        return "BED-101";
    }

    private Bed mapRow(ResultSet rs) throws SQLException {
        return new Bed(
                rs.getString("id"),
                rs.getString("ward_name"),
                rs.getString("bed_number"),
                rs.getString("patient"),
                rs.getString("status"),
                rs.getDouble("daily_charge"),
                rs.getString("admission_date")
        );
    }
}

