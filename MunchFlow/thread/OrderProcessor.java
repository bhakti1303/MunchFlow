package thread;

import dao.DeliveryPartnerDAO;
import dao.OrderDAO;
import model.DeliveryPartner;

public class OrderProcessor implements Runnable {

    // Sab orders ke liye ek hi lock: ek time pe sirf ek thread partner chunega
    private static final Object PARTNER_LOCK = new Object();

    private final int orderId;
    private final OrderDAO orderDAO = new OrderDAO();
    private final DeliveryPartnerDAO partnerDAO = new DeliveryPartnerDAO();

    public OrderProcessor(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public void run() {
        try {
            // 1. Preparing
            sleep(5);
            orderDAO.updateStatus(orderId, "PREPARING");
            System.out.println("\n[Update] Order #" + orderId + " is being prepared.");

            sleep(8);

            // 2. Partner assign (synchronized -> race condition nahi hogi)
            DeliveryPartner partner = null;
            while (partner == null) {
                synchronized (PARTNER_LOCK) {
                    partner = partnerDAO.getFreePartnerAndLock();
                }
                if (partner == null) {
                    System.out.println("\n[Update] Order #" + orderId + " is waiting for a free delivery partner...");
                    sleep(3);
                }
            }
            orderDAO.assignPartner(orderId, partner.getId());
            orderDAO.updateStatus(orderId, "OUT_FOR_DELIVERY");
            System.out.println("\n[Update] Order #" + orderId + " is out for delivery with " + partner.getName() + ".");

            // 3. Delivery
            sleep(10);
            orderDAO.updateStatus(orderId, "DELIVERED");
            partnerDAO.setAvailable(partner.getId(), true);
            System.out.println("\n[Update] Order #" + orderId + " has been delivered!");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void sleep(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000L);
    }
}