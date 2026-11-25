package model;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private int restaurantId = -1;
    private final List<CartItem> items = new ArrayList<>();

    public int getRestaurantId() { return restaurantId; }
    public List<CartItem> getItems() { return items; }
    public boolean isEmpty() { return items.isEmpty(); }

    public boolean addItem(FoodItem food, int quantity) {
        if (!items.isEmpty() && restaurantId != food.getRestaurantId()) {
            return false;
        }
        restaurantId = food.getRestaurantId();

        for (CartItem ci : items) {
            if (ci.getFood().getId() == food.getId()) {
                ci.addQuantity(quantity);
                return true;
            }
        }
        items.add(new CartItem(food, quantity));
        return true;
    }

    public double getTotal() {
        double total = 0;
        for (CartItem ci : items) total += ci.getSubtotal();
        return total;
    }

    public void clear() {
        items.clear();
        restaurantId = -1;
    }
}