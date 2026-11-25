package dao;

import model.DeliveryPartner;
import util.DBConnection;

import java.sql.*;

public class DeliveryPartnerDAO {

    // Free partner dhundo aur busy mark karo. Koi free na ho toh null.
    public DeliveryPartner getFreePartnerAndLock() {
        String select = "SELECT partner_id, partner_name FROM delivery_partners WHERE available = TRUE LIMIT 1";
        String update = "UPDATE delivery_partners SET available = FALSE WHERE partner_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(select);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int id = rs.getInt("partner_id");
                String name = rs.getString("partner_name");
                try (PreparedStatement ups = con.prepareStatement(update)) {
                    ups.setInt(1, id);
                    ups.executeUpdate();
                }
                return new DeliveryPartner(id, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setAvailable(int partnerId, boolean available) {
        String sql = "UPDATE delivery_partners SET available = ? WHERE partner_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setInt(2, partnerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}