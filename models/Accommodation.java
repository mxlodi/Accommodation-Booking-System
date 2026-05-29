package models;

import interfaces.Displayable;
import interfaces.Bookable;
import java.time.LocalDate;
import java.util.Collection;

public abstract class Accommodation implements Displayable, Bookable {
    private int accId;
    private String name;
    private double pricePerNight;
    private int capacity;

    // Constructor using setters for validation safety
    public Accommodation(int accId, String name, double pricePerNight, int capacity) {
        this.accId = accId;
        setName(name);
        setPricePerNight(pricePerNight);
        setCapacity(capacity);
    }

    // Setters with validation logic restored
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            this.name = "Unknown Accommodation";
        } else {
            this.name = name;
        }
    }

    public void setPricePerNight(double pricePerNight) {
        if (pricePerNight < 0) {
            this.pricePerNight = 50.0; // Default fallback price
        } else {
            this.pricePerNight = pricePerNight;
        }
    }

    public void setCapacity(int capacity) {
        if (capacity < 1) {
            this.capacity = 1; // Minimum capacity
        } else {
            this.capacity = capacity;
        }
    }

    // Abstract method: Every specific type must identify itself
    public abstract String getType();

    @Override
    public boolean canAccommodate(int numberOfGuests) {
        return numberOfGuests > 0 && numberOfGuests <= capacity;
    }

    @Override
    public double calculatePrice(int nights) {
        return pricePerNight * nights;
    }

    // Common Getters
    public int getAccId() {
        return accId;
    }

    public String getName() {
        return name;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public int getCapacity() {
        return capacity;
    }

    // Shared business logic: Check if dates overlap with existing bookings
    public boolean isAvailable(LocalDate checkIn, LocalDate checkOut, Collection<Booking> allBookings) {
        for (Booking b : allBookings) {
            if (b.getAccommodation().getAccId() == this.accId && b.isActive()) {
                LocalDate existingIn = LocalDate.parse(b.getCheckInDate());
                LocalDate existingOut = LocalDate.parse(b.getCheckOutDate());
                if (checkIn.isBefore(existingOut) && checkOut.isAfter(existingIn)) {
                    return false;
                }
            }
        }
        return true;
    }
}
