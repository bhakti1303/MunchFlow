package model;

public class Restaurant {

    private int id;
    private String name;
    private String cuisine;
    private double rating;
    private String deliveryTime;
    private String emoji;

    public Restaurant(int id, String name, String cuisine,
                      double rating, String deliveryTime, String emoji) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.rating = rating;
        this.deliveryTime = deliveryTime;
        this.emoji = emoji;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCuisine() { return cuisine; }
    public double getRating() { return rating; }
    public String getDeliveryTime() { return deliveryTime; }
    public String getEmoji() { return emoji; }

    @Override
    public String toString() {
        return emoji + " " + name + " (" + cuisine + ") ⭐" + rating + " | " + deliveryTime;
    }
}