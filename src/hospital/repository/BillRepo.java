package hospital.repository;

import hospital.model.Bill;
import hospital.utils.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillRepo {

    public List<Bill> findAll() {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY id ASC";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bills: " + e.getMessage());
        }
        return list;
    }

    public Bill findById(String id) {
        String sql = "SELECT * FROM bills WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding bill by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean save(Bill b) {
        String sql = "INSERT INTO bills (id, patient, doctor, consultation_fee, treatment_fee, "
                + "medicine_fee, total_amount, status, payment_method, bill_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, b.getId());
            ps.setString(2, b.getPatient());
            ps.setString(3, b.getDoctor());
            ps.setDouble(4, b.getConsultationFee());
            ps.setDouble(5, b.getTreatmentFee());
            ps.setDouble(6, b.getMedicineFee());
            ps.setDouble(7, b.getTotalAmount());
            ps.setString(8, b.getStatus());
            ps.setString(9, b.getPaymentMethod());
            ps.setString(10, b.getBillDate());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving bill: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Bill b) {
        String sql = "UPDATE bills SET patient = ?, doctor = ?, consultation_fee = ?, "
                + "treatment_fee = ?, medicine_fee = ?, total_amount = ?, status = ?, "
                + "payment_method = ?, bill_date = ? WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, b.getPatient());
            ps.setString(2, b.getDoctor());
            ps.setDouble(3, b.getConsultationFee());
            ps.setDouble(4, b.getTreatmentFee());
            ps.setDouble(5, b.getMedicineFee());
            ps.setDouble(6, b.getTotalAmount());
            ps.setString(7, b.getStatus());
            ps.setString(8, b.getPaymentMethod());
            ps.setString(9, b.getBillDate());
            ps.setString(10, b.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating bill: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM bills WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting bill: " + e.getMessage());
            return false;
        }
    }

    public String generateNextId() {
        String sql = "SELECT id FROM bills WHERE id LIKE 'BILL-%' ORDER BY id DESC LIMIT 1";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("id");
                String numPart = lastId.replace("BILL-", "").trim();
                int nextNum = Integer.parseInt(numPart) + 1;
                return "BILL-" + nextNum;
            }
        } catch (Exception ignored) {
        }
        return "BILL-401";
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        return new Bill(
                rs.getString("id"),
                rs.getString("patient"),
                rs.getString("doctor"),
                rs.getDouble("consultation_fee"),
                rs.getDouble("treatment_fee"),
                rs.getDouble("medicine_fee"),
                rs.getDouble("total_amount"),
                rs.getString("status"),
                rs.getString("payment_method"),
                rs.getString("bill_date")
        );
    }
}

