package model;

public class DeliveryPartner {

    private int id;
    private String name;

    public DeliveryPartner(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
}