package dao;

import model.FoodItem;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodItemDAO {

    // Kisi ek restaurant ka menu
    public List<FoodItem> getMenuByRestaurant(int restaurantId) {
        List<FoodItem> list = new ArrayList<>();
        String sql = "SELECT * FROM food_items WHERE restaurant_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new FoodItem(
                        rs.getInt("food_id"),
                        rs.getInt("restaurant_id"),
                        rs.getString("food_name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getBoolean("vegetarian"),
                        rs.getString("emoji")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Ek food item uski id se
    public FoodItem getFoodById(int foodId) {
        String sql = "SELECT * FROM food_items WHERE food_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, foodId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new FoodItem(
                        rs.getInt("food_id"),
                        rs.getInt("restaurant_id"),
                        rs.getString("food_name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getBoolean("vegetarian"),
                        rs.getString("emoji")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Naya food item add karo
    public boolean addFoodItem(int restaurantId, String name, String category,
                               double price, boolean vegetarian, String emoji) {
        String sql = "INSERT INTO food_items "
                   + "(restaurant_id, food_name, category, price, vegetarian, emoji) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, restaurantId);
            ps.setString(2, name);
            ps.setString(3, category);
            ps.setDouble(4, price);
            ps.setBoolean(5, vegetarian);
            ps.setString(6, emoji);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}