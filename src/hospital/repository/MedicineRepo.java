package hospital.repository;

import hospital.model.Medicine;
import hospital.utils.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicineRepo {

    public List<Medicine> findAll() {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines ORDER BY id ASC";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching medicines: " + e.getMessage());
        }
        return list;
    }

    public Medicine findById(String id) {
        String sql = "SELECT * FROM medicines WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding medicine by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean save(Medicine m) {
        String sql = "INSERT INTO medicines (id, name, category, stock_qty, unit_price, expiry_date, manufacturer) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getId());
            ps.setString(2, m.getName());
            ps.setString(3, m.getCategory());
            ps.setInt(4, m.getStockQty());
            ps.setDouble(5, m.getUnitPrice());
            ps.setString(6, m.getExpiryDate());
            ps.setString(7, m.getManufacturer());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving medicine: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Medicine m) {
        String sql = "UPDATE medicines SET name = ?, category = ?, stock_qty = ?, "
                + "unit_price = ?, expiry_date = ?, manufacturer = ? WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getName());
            ps.setString(2, m.getCategory());
            ps.setInt(3, m.getStockQty());
            ps.setDouble(4, m.getUnitPrice());
            ps.setString(5, m.getExpiryDate());
            ps.setString(6, m.getManufacturer());
            ps.setString(7, m.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating medicine: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStock(String id, int quantityChange) {
        String sql = "UPDATE medicines SET stock_qty = stock_qty + ? WHERE id = ? AND stock_qty + ? >= 0";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantityChange);
            ps.setString(2, id);
            ps.setInt(3, quantityChange);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating stock quantity: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM medicines WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting medicine: " + e.getMessage());
            return false;
        }
    }

    public String generateNextId() {
        String sql = "SELECT id FROM medicines WHERE id LIKE 'MED-%' ORDER BY id DESC LIMIT 1";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("id");
                String numPart = lastId.replace("MED-", "").trim();
                int nextNum = Integer.parseInt(numPart) + 1;
                return "MED-" + nextNum;
            }
        } catch (Exception ignored) {
        }
        return "MED-501";
    }

    private Medicine mapRow(ResultSet rs) throws SQLException {
        return new Medicine(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getInt("stock_qty"),
                rs.getDouble("unit_price"),
                rs.getString("expiry_date"),
                rs.getString("manufacturer")
        );
    }
}

