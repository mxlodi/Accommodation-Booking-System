package models;

import java.time.LocalDateTime;
import interfaces.Displayable;

public class Payment implements Displayable{
    private int paymentId;
    private Booking booking;
    private double amount;
    private String method;
    private LocalDateTime paymentDate;
    private boolean completed;

    // Fixed: Added constructor with 2 parameters as requested by your code
    public Payment(int paymentId, Booking booking) {
        this(paymentId, booking, "CASH"); // Defaults to CASH
    }

    // Constructor with 3 parameters
    public Payment(int paymentId, Booking booking, String method) {
        this.paymentId = paymentId;
        this.booking = booking;
        this.method = (method != null) ? method : "CASH";
        this.amount = (booking != null) ? booking.calculateTotalPrice() : 0.0;
        this.paymentDate = LocalDateTime.now();
        this.completed = false;
    }

    // Fixed: Restored getPaymentId() getter
    public int getPaymentId() {
        return paymentId;
    }

    public Booking getBooking() {
        return booking;
    }

    public String getMethod() {
        return method;
    }

    public boolean isValidMethod() {
        return method.equals("CASH") || method.equals("CARD") || method.equals("ONLINE");
    }

    public void processPayment() {
        if (!completed) {
            this.completed = true;
            System.out.println("Payment #" + paymentId + " processed.");
        }
    }

    public double getAmount() { return amount; }

    public boolean isPaid() { return completed; }

    @Override
    public void display() {
        System.out.println("Payment ID: " + paymentId + " | Amount: $" + amount);
    }

    @Override
    public void displayName() {
        System.out.println("Payment #" + paymentId);
    }
}