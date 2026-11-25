package model;

public class FoodItem {

    private int id;
    private int restaurantId;
    private String name;
    private String category;
    private double price;
    private boolean vegetarian;
    private String emoji;

    public FoodItem(int id, int restaurantId, String name, String category,
                    double price, boolean vegetarian, String emoji) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.vegetarian = vegetarian;
        this.emoji = emoji;
    }

    public int getId() { return id; }
    public int getRestaurantId() { return restaurantId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public boolean isVegetarian() { return vegetarian; }
    public String getEmoji() { return emoji; }

    @Override
    public String toString() {
        String type = vegetarian ? "🟢 Veg" : "🔴 Non-Veg";
        return id + ". " + emoji + " " + name + " - ₹" + price + " (" + type + ")";
    }
}