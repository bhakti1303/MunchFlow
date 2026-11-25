package model;

public class Order {

    private int id;
    private double total;
    private String status;
    private String date;

    public Order(int id, double total, String status, String date) {
        this.id = id;
        this.total = total;
        this.status = status;
        this.date = date;
    }

    public int getId() { return id; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }
    public String getDate() { return date; }

    @Override
    public String toString() {
        return "Order #" + id + " | Rs." + total + " | " + status + " | " + date;
    }
}