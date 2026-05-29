package repository;

import models.*;
import interfaces.Searchable;
import java.time.LocalDate;
import java.util.*;

public class BookingRepository implements Searchable {
    private Map<Integer, Booking> allBookings = new HashMap<>();
    private List<User> users = new ArrayList<>();
    private List<Accommodation> accommodations = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();

    // USER MANAGEMENT
    public void addUser(User user) {
        users.add(user);
        System.out.println("User added: " + user.getName());
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public User findUserById(int userId) {
        for (User u : users) {
            if (u.getUserId() == userId)
                return u;
        }
        return null;
    }

    // ACCOMMODATION MANAGEMENT
    public void addHotel(Hotel hotel) {
        accommodations.add(hotel);
        hotel.displayName();
    }

    public void addGuestHouse(GuestHouse gh) {
        accommodations.add(gh);
        gh.displayName();
    }

    public void addApartment(Apartment apt) {
        accommodations.add(apt);
        apt.displayName();
    }

    // OVERLOADED ACCOMMODATION METHODS
    public void addAccommodation(Hotel hotel) {
        addHotel(hotel);
    }

    public void addAccommodation(Apartment apartment) {
        addApartment(apartment);
    }

    public void addAccommodation(GuestHouse guestHouse) {
        addGuestHouse(guestHouse);
    }

    public List<Accommodation> getAllAccommodations() {
        return new ArrayList<>(accommodations);
    }

    public Accommodation findAccommodationById(int accId) {
        for (Accommodation a : accommodations) {
            if (a.getAccId() == accId)
                return a;
        }
        return null;
    }

    // BOOKING MANAGEMENT
    public void addBooking(Booking booking) {
        if (booking == null || booking.getUser() == null || booking.getAccommodation() == null) {
            System.out.println("[ERROR] Invalid booking data!");
            return;
        }

        // Validate dates using Booking's own method
        if (!booking.isValidDates()) {
            System.out.println("[ERROR] Check-out date must be after check-in date!");
            return;
        }

        if (!isAvailable(booking.getAccommodation().getAccId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate())) {
            System.out.println("[ERROR] Booking #" + booking.getBookingId()
                    + " rejected - accommodation not available!");
            return;
        }

        allBookings.put(booking.getBookingId(), booking);
        System.out.println("[OK] Booking #" + booking.getBookingId() + " created");
    }

    public void updateBookingStatus(int bookingId, BookingStatus newStatus) {
        Booking booking = allBookings.get(bookingId);
        if (booking == null) {
            System.out.println("[ERROR] Booking #" + bookingId + " not found!");
            return;
        }
        BookingStatus oldStatus = booking.getStatus();

        // Delegate status validation to Booking class
        if (!booking.canChangeTo(newStatus)) {
            System.out.println("[ERROR] Cannot change booking #" + bookingId +
                    " from " + oldStatus + " to " + newStatus);
            return;
        }
        booking.setStatus(newStatus);
        System.out.println("[OK] Booking #" + bookingId + " status: " + oldStatus + " -> " + newStatus);
    }

    public void hardDeleteBooking(int bookingId) {
        Booking removed = allBookings.remove(bookingId);
        if (removed == null) {
            System.out.println("[ERROR] Booking #" + bookingId + " not found!");
            return;
        }
        System.out.println("[OK] Booking #" + bookingId + " hard deleted");
    }

    // BOOKING QUERIES
    public Collection<Booking> getAllBookings() {
        return allBookings.values();
    }

    public Collection<Booking> getBookingsByStatus(BookingStatus status) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : allBookings.values()) {
            if (b.getStatus() == status)
                result.add(b);
        }
        return result;
    }

    public List<Booking> getUserHistory(int userId) {
        List<Booking> history = new ArrayList<>();
        for (Booking b : allBookings.values()) {
            if (b.getUser().getUserId() == userId)
                history.add(b);
        }
        history.sort((b1, b2) -> b2.getCheckInDate().compareTo(b1.getCheckInDate()));
        return history;
    }

    // AVAILABILITY
    public boolean isAvailable(int accommodationId, String checkIn, String checkOut) {
        Accommodation acc = findAccommodationById(accommodationId);
        if (acc == null) {
            System.out.println("[ERROR] Accommodation ID " + accommodationId + " not found!");
            return false;
        }

        LocalDate newIn = LocalDate.parse(checkIn);
        LocalDate newOut = LocalDate.parse(checkOut);

        // Business logic is in Accommodation class
        return acc.isAvailable(newIn, newOut, allBookings.values());
    }

    public List<Accommodation> getAvailableRooms(String checkIn, String checkOut) {
        List<Accommodation> availableResults = new ArrayList<>();

        for (Accommodation acc : accommodations) {
            if (isAvailable(acc.getAccId(), checkIn, checkOut)) {
                availableResults.add(acc);
            }
        }
        return availableResults;
    }

    // PAYMENT MANAGEMENT
    public void addPayment(Payment payment) {
        if (payment == null || payment.getBooking() == null) {
            System.out.println("[ERROR] Payment must be linked to a valid booking!");
            return;
        }

        if (!payment.isValidMethod()) {
            System.out.println("[ERROR] Invalid payment method: " + payment.getMethod());
            return;
        }

        payment.processPayment();

        payments.add(payment);
        System.out.println("[OK] Payment #" + payment.getPaymentId() + " recorded");
    }

    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments);
    }

    // COUNTS
    public int getTotalBookingsCount() {
        return allBookings.size();
    }

    public int getHotelCount() {
        int count = 0;
        for (Accommodation a : accommodations) {
            if (a instanceof Hotel)
                count++;
        }
        return count;
    }

    public int getGuestHouseCount() {
        int count = 0;
        for (Accommodation a : accommodations) {
            if (a instanceof GuestHouse)
                count++;
        }
        return count;
    }

    public int getApartmentCount() {
        int count = 0;
        for (Accommodation a : accommodations) {
            if (a instanceof Apartment)
                count++;
        }
        return count;
    }

    // SEARCHABLE INTERFACE IMPLEMENTATION
    @Override
    public List<Booking> searchBookingsByUserName(String name) {
        List<Booking> results = new ArrayList<>();
        for (Booking b : allBookings.values()) {
            if (b.getUser().getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(b);
            }
        }
        return results;
    }

    @Override
    public List<Accommodation> searchAccommodationsByName(String name) {
        List<Accommodation> results = new ArrayList<>();
        for (Accommodation a : accommodations) {
            if (a.getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(a);
            }
        }
        return results;
    }

    @Override
    public List<User> searchUsersByName(String name) {
        List<User> results = new ArrayList<>();
        for (User u : users) {
            if (u.getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(u);
            }
        }
        return results;
    }

    @Override
    public List<Booking> searchBookingsByDateRange(String startDate, String endDate, BookingStatus status) {
        List<Booking> results = new ArrayList<>();
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        for (Booking b : allBookings.values()) {
            LocalDate checkIn = LocalDate.parse(b.getCheckInDate());
            LocalDate checkOut = LocalDate.parse(b.getCheckOutDate());
            if (!(checkIn.isAfter(end) || checkOut.isBefore(start)) && b.getStatus() == status) {
                results.add(b);
            }
        }
        return results;
    }

    // DISPLAY METHODS
    public void displayAllData() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SYSTEM DATA OVERVIEW");
        System.out.println("=".repeat(80));

        System.out.println("\n[USERS]");
        for (User u : users) {
            u.displayName();
        }

        System.out.println("\n[ACCOMMODATIONS]");
        for (Accommodation a : accommodations) {
            a.displayName();
        }

        System.out.println("\n[BOOKINGS]");
        for (Booking b : allBookings.values()) {
            b.displayName();
        }

        System.out.println("\n[PAYMENTS]");
        for (Payment p : payments) {
            p.displayName();
        }

        System.out.println("\n[TOTAL BOOKINGS: " + allBookings.size() + "]");
    }
}