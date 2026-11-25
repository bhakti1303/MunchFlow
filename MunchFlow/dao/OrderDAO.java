package dao;

import model.Cart;
import model.CartItem;
import model.Order;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // Order place karo. Naya order id return karta hai, fail hua toh -1
    public int placeOrder(int userId, Cart cart) {
        String orderSql = "INSERT INTO orders (user_id, total_amount, status) VALUES (?, ?, 'PLACED')";
        String itemSql = "INSERT INTO order_items (order_id, food_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setDouble(2, cart.getTotal());
                ps.executeUpdate();

                int orderId;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) { con.rollback(); return -1; }
                    orderId = keys.getInt(1);
                }

                try (PreparedStatement ips = con.prepareStatement(itemSql)) {
                    for (CartItem ci : cart.getItems()) {
                        ips.setInt(1, orderId);
                        ips.setInt(2, ci.getFood().getId());
                        ips.setInt(3, ci.getQuantity());
                        ips.setDouble(4, ci.getFood().getPrice());
                        ips.addBatch();
                    }
                    ips.executeBatch();
                }

                con.commit();
                return orderId;

            } catch (SQLException e) {
                con.rollback();
                e.printStackTrace();
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Ek user ke saare orders, newest pehle
    public List<Order> getOrdersByUser(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT order_id, total_amount, status, order_date "
                   + "FROM orders WHERE user_id = ? ORDER BY order_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Order(
                        rs.getInt("order_id"),
                        rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getString("order_date")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Order ka status update karo (multithreading mein use hoga)
    public void updateStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Order ko delivery partner assign karo
    public void assignPartner(int orderId, int partnerId) {
        String sql = "UPDATE orders SET partner_id = ? WHERE order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, partnerId);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}