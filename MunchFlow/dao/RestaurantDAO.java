package dao;

import model.Restaurant;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDAO {

    // Saare restaurants nikalo
    public List<Restaurant> getAllRestaurants() {
        List<Restaurant> list = new ArrayList<>();
        String sql = "SELECT * FROM restaurants";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Restaurant(
                    rs.getInt("restaurant_id"),
                    rs.getString("restaurant_name"),
                    rs.getString("cuisine"),
                    rs.getDouble("rating"),
                    rs.getString("delivery_time"),
                    rs.getString("emoji")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Naya restaurant add karo (user khud add kar paye)
    public boolean addRestaurant(String name, String cuisine, double rating,
                                 String deliveryTime, String emoji) {
        String sql = "INSERT INTO restaurants "
                   + "(restaurant_name, cuisine, rating, delivery_time, emoji) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, cuisine);
            ps.setDouble(3, rating);
            ps.setString(4, deliveryTime);
            ps.setString(5, emoji);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}