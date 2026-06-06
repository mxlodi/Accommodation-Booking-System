package models;

import interfaces.Displayable;
import java.time.LocalDateTime;

public class Payment implements Displayable {
    private int paymentId;
    private Booking booking;
    private double amount;
    private String method;
    private LocalDateTime paymentDate;
    private boolean completed;

    // Constructor overload 1: defaults to CASH payment method
    public Payment(int paymentId, Booking booking) {
        this(paymentId, booking, "CASH");
    }

    // Constructor overload 2: caller specifies the method
    public Payment(int paymentId, Booking booking, String method) {
        this.paymentId = paymentId;
        this.booking = booking;
        this.method = (method != null) ? method : "CASH";
        this.amount = (booking != null) ? booking.calculateTotalPrice() : 0.0;
        this.paymentDate = LocalDateTime.now();
        this.completed = false;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public Booking getBooking() {
        return booking;
    }

    public String getMethod() {
        return method;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isPaid() {
        return completed;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public boolean isValidMethod() {
        return "CASH".equals(method) || "CARD".equals(method) || "ONLINE".equals(method);
    }

    // Overload 1: process with the already-set method
    public void processPayment() {
        if (!completed) {
            this.completed = true;
            System.out.println("[PAYMENT] #" + paymentId
                    + " processed via " + method
                    + " | Amount: $" + amount);
        }
    }

    // Overload 2: process and switch to a different method
    public void processPayment(String method) {
        this.method = method;
        if (!completed) {
            this.completed = true;
            System.out.println("[PAYMENT] #" + paymentId
                    + " processed via " + method
                    + " | Amount: $" + amount);
        }
    }

    // Overload 3: process with a method and apply a discount to the amount
    public void processPayment(String method, double discount) {
        this.method = method;
        if (!completed) {
            this.amount = Math.max(0, this.amount - discount);
            this.completed = true;
            System.out.println("[PAYMENT] #" + paymentId
                    + " processed via " + method
                    + " | Discount: $" + discount
                    + " | Final Amount: $" + amount);
        }
    }

    @Override
    public void display() {
        System.out.println("Payment #" + paymentId
                + " | Method: " + method
                + " | Amount: $" + amount
                + " | Paid: " + completed
                + " | Date: " + paymentDate);
    }

    @Override
    public void displayName() {
        System.out.println("Payment #" + paymentId);
    }
}