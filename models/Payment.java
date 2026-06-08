package models;

import interfaces.Displayable;
import java.time.LocalDateTime;

public class Payment implements Displayable {

    // Nested enum
    // ONLINE :funds captured immediately via digital gateway
    // PAY_AT_PROPERTY :booking secured online, guest settles at check-in
    // (property handles card, KHQR, etc. directly)
    public enum PaymentMethod {
        ONLINE,
        PAY_AT_PROPERTY
    }

    private int paymentId;
    private Booking booking;
    private double amount;
    private PaymentMethod method;
    private LocalDateTime paymentDate;
    private boolean completed;

    // Constructor overload 1: defaults to ONLINE (standard for booking systems)
    public Payment(int paymentId, Booking booking) {
        this(paymentId, booking, PaymentMethod.ONLINE);
    }

    // Constructor overload 2: caller specifies the payment method
    public Payment(int paymentId, Booking booking, PaymentMethod method) {
        this.paymentId = paymentId;
        this.booking = booking;
        this.method = (method != null) ? method : PaymentMethod.ONLINE;
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

    public PaymentMethod getMethod() {
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
        return method == PaymentMethod.ONLINE || method == PaymentMethod.PAY_AT_PROPERTY;
    }

    // Overload 1: process with the already-set method
    public void processPayment() {
    if (!completed) {
        if (method == PaymentMethod.ONLINE) {
            this.completed = true; // Mark as paid
            this.paymentDate = LocalDateTime.now(); // Date of payment
            System.out.println("[PAYMENT] #" + paymentId
                    + " -> Online payment captured | Amount: $" + amount);
        } else {
            this.completed = false; // Remains false because they haven't arrived at the check-in yet
            System.out.println("[PAYMENT] #" + paymentId
                    + " -> Pay at Property | $" + amount + " pending at check-in");
            }
        }
    }
    // Overload 2: switch method then process
    // example: guest originally chose PAY_AT_PROPERTY but decides to pay online instead
    public void processPayment(PaymentMethod method) {
        this.method = method;
        processPayment();
    }

    // Overload 3: switch method, apply discount, then process
    public void processPayment(PaymentMethod method, double discount) {
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