package models;

import interfaces.Displayable;
import java.time.LocalDateTime;

public class Booking implements Displayable {
    // --- FIELDS ---
    private int bookingId;
    private User user;
    private Accommodation accommodation;
    private LocalDateTime checkInDateTime;
    private LocalDateTime checkOutDateTime;
    private BookingStatus status;

    // --- CONSTRUCTOR ---
    // Constructor accepts ISO date-time strings: "2026-10-01T14:00"
    public Booking(int id, User user, Accommodation acc,
            String checkIn, String checkOut, BookingStatus status) {
        this.bookingId = id;
        this.user = user;
        this.accommodation = acc;
        this.checkInDateTime = LocalDateTime.parse(checkIn);
        this.checkOutDateTime = LocalDateTime.parse(checkOut);
        this.status = status;
    }

    // --- VALIDATION METHODS ---
    public boolean isValidDates() {
        return checkInDateTime != null && checkOutDateTime != null
                && checkOutDateTime.isAfter(checkInDateTime);
    }

    // --- GETTERS ---
    public int getBookingId() {
        return bookingId;
    }

    public User getUser() {
        return user;
    }

    public Accommodation getAccommodation() {
        return accommodation;
    }

    // Returns just the date part as a String (used by availability checks)
    public String getCheckInDate() {
        return checkInDateTime.toLocalDate().toString();
    }

    public String getCheckOutDate() {
        return checkOutDateTime.toLocalDate().toString();
    }

    // Full datetime getters for price calculation
    public LocalDateTime getCheckInDateTime() {
        return checkInDateTime;
    }
    public LocalDateTime getCheckOutDateTime() {
        return checkOutDateTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    // --- SETTERS ---
    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    // --- STATUS MANAGEMENT ---
    public boolean canChangeTo(BookingStatus newStatus) {
        if (this.status == BookingStatus.CONFIRMED) {
            return newStatus == BookingStatus.CHECKED_IN
                    || newStatus == BookingStatus.CANCELLED;
        }
        if (this.status == BookingStatus.CHECKED_IN) {
            return newStatus == BookingStatus.CHECKED_OUT;
        }
        return false;
    }

    // --- BUSINESS LOGIC ---
    public double calculateTotalPrice() {
        if (accommodation == null)
            return 0.0;
        // Uses the LocalDateTime overload — most accurate for real booking flow
        return accommodation.calculatePrice(checkInDateTime, checkOutDateTime);
    }

    public boolean isActive() {
        return status == BookingStatus.CONFIRMED || status == BookingStatus.CHECKED_IN;
    }

    // --- DISPLAYABLE INTERFACE IMPLEMENTATION ---
    @Override
    public void display() {
        System.out.println("Booking #" + bookingId
                + " | " + user.getName()
                + " @ " + accommodation.getName()
                + " | " + getCheckInDate() + " -> " + getCheckOutDate()
                + " | Status: " + status
                + " | Total: $" + calculateTotalPrice());
    }
}