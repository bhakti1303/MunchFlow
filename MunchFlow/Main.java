import dao.FoodItemDAO;
import dao.OrderDAO;
import dao.RestaurantDAO;
import dao.UserDAO;
import model.Cart;
import model.CartItem;
import model.FoodItem;
import model.Order;
import model.Restaurant;
import model.User;
import thread.OrderProcessor;
import util.InputUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final UserDAO userDAO = new UserDAO();
    private static final RestaurantDAO restaurantDAO = new RestaurantDAO();
    private static final FoodItemDAO foodDAO = new FoodItemDAO();
    private static final OrderDAO orderDAO = new OrderDAO();

    // Thread pool: ek saath 5 orders process ho sakte hain
    private static final ExecutorService executor = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("   Welcome to MunchFlow");
        System.out.println("=================================");

        while (true) {
            System.out.println("\n1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            int choice = InputUtil.readInt("Choice: ");

            switch (choice) {
                case 1 -> register();
                case 2 -> login();
                case 3 -> {
                    System.out.println("Goodbye! See you soon.");
                    executor.shutdown();
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // ---------- REGISTER ----------
    private static void register() {
        System.out.println("\n--- Register ---");
        String name = InputUtil.readString("Name: ");
        String email = InputUtil.readString("Email: ");
        String password = InputUtil.readString("Password: ");

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("No field can be empty!");
            return;
        }

        if (userDAO.register(name, email, password, "CUSTOMER")) {
            System.out.println("Registration successful! Please login now.");
        } else {
            System.out.println("Registration failed.");
        }
    }

    // ---------- LOGIN ----------
    private static void login() {
        System.out.println("\n--- Login ---");
        String email = InputUtil.readString("Email: ");
        String password = InputUtil.readString("Password: ");

        User user = userDAO.login(email, password);

        if (user == null) {
            System.out.println("Incorrect email or password!");
            return;
        }

        System.out.println("\nWelcome, " + user.getName() + "!");
        customerMenu(user);
    }

    // ---------- CUSTOMER MENU ----------
    private static void customerMenu(User user) {
        Cart cart = new Cart();

        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. View restaurants");
            System.out.println("2. View a restaurant's menu");
            System.out.println("3. Add item to cart");
            System.out.println("4. View cart");
            System.out.println("5. Place order");
            System.out.println("6. My orders");
            System.out.println("7. Logout");

            int choice = InputUtil.readInt("Choice: ");

            switch (choice) {
                case 1 -> showRestaurants();
                case 2 -> showMenu();
                case 3 -> addToCart(cart);
                case 4 -> viewCart(cart);
                case 5 -> placeOrder(user, cart);
                case 6 -> myOrders(user);
                case 7 -> {
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private static void showRestaurants() {
        System.out.println("\n===== RESTAURANTS =====");
        List<Restaurant> list = restaurantDAO.getAllRestaurants();
        for (Restaurant r : list) {
            System.out.println(r.getId() + ". " + r);
        }
    }

    private static void showMenu() {
        showRestaurants();
        int id = InputUtil.readInt("\nEnter restaurant ID: ");

        List<FoodItem> menu = foodDAO.getMenuByRestaurant(id);
        if (menu.isEmpty()) {
            System.out.println("No menu found for this ID.");
            return;
        }

        System.out.println("\n===== MENU =====");
        for (FoodItem f : menu) {
            System.out.println(f);
        }
    }

    // ---------- CART ----------
    private static void addToCart(Cart cart) {
        showRestaurants();
        int rid = InputUtil.readInt("\nEnter restaurant ID: ");

        List<FoodItem> menu = foodDAO.getMenuByRestaurant(rid);
        if (menu.isEmpty()) {
            System.out.println("No menu found for this ID.");
            return;
        }

        System.out.println("\n===== MENU =====");
        for (FoodItem f : menu) {
            System.out.println(f);
        }

        int foodId = InputUtil.readInt("\nEnter food ID to add: ");
        FoodItem food = foodDAO.getFoodById(foodId);

        if (food == null || food.getRestaurantId() != rid) {
            System.out.println("Invalid food ID for this restaurant.");
            return;
        }

        int qty = InputUtil.readInt("Quantity: ");
        if (qty <= 0) {
            System.out.println("Quantity must be at least 1.");
            return;
        }

        if (cart.addItem(food, qty)) {
            System.out.println(food.getName() + " x" + qty + " added to cart.");
        } else {
            System.out.println("Your cart has items from another restaurant.");
            String ans = InputUtil.readString("Clear cart and add this item? (y/n): ");
            if (ans.equalsIgnoreCase("y")) {
                cart.clear();
                cart.addItem(food, qty);
                System.out.println("Cart cleared. " + food.getName() + " x" + qty + " added.");
            }
        }
    }

    private static void viewCart(Cart cart) {
        if (cart.isEmpty()) {
            System.out.println("\nYour cart is empty.");
            return;
        }
        System.out.println("\n===== YOUR CART =====");
        for (CartItem ci : cart.getItems()) {
            System.out.println(ci);
        }
        System.out.println("---------------------");
        System.out.println("TOTAL: Rs." + cart.getTotal());
    }

    // ---------- ORDER ----------
    private static void placeOrder(User user, Cart cart) {
        if (cart.isEmpty()) {
            System.out.println("\nYour cart is empty. Add items first.");
            return;
        }

        viewCart(cart);
        String ans = InputUtil.readString("\nConfirm order? (y/n): ");
        if (!ans.equalsIgnoreCase("y")) {
            System.out.println("Order cancelled.");
            return;
        }

        int orderId = orderDAO.placeOrder(user.getId(), cart);
        if (orderId > 0) {
            System.out.println("\nOrder placed successfully! Your order ID is #" + orderId);
            cart.clear();
            // Background thread: status badalna + delivery partner assign karna
            executor.submit(new OrderProcessor(orderId));
        } else {
            System.out.println("Could not place the order. Please try again.");
        }
    }

    private static void myOrders(User user) {
        List<Order> orders = orderDAO.getOrdersByUser(user.getId());
        if (orders.isEmpty()) {
            System.out.println("\nYou have no orders yet.");
            return;
        }
        System.out.println("\n===== MY ORDERS =====");
        for (Order o : orders) {
            System.out.println(o);
        }
    }
}