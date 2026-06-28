package repository;

import models.*;
import interfaces.Searchable;
import interfaces.Displayable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class BookingRepository implements Searchable {
    // --- DATA STORAGE FIELDS ---
    private Map<Integer, Booking> allBookings = new HashMap<>();
    private List<User> users = new ArrayList<>();
    private List<Accommodation> accommodations = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();

    // --- NESTED EXCEPTION CLASS ---
    // Nested Exception
    public static class InvalidBookingException extends Exception {
        public InvalidBookingException(String message) {
            super(message);
        }
    }

    // ============================================================
    // USER MANAGEMENT - CRUD operations 
    // ============================================================
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

    // ============================================================
    // ACCOMMODATION MANAGEMENT - CRUD operations for Accommodation entities
    // ============================================================
    // ACCOMMODATION MANAGEMENT
    public void addAccommodation(Hotel hotel) {
        accommodations.add(hotel);
        System.out.print("[HOTEL ADDED] ");
        hotel.display();
    }

    public void addAccommodation(Apartment apartment) {
        accommodations.add(apartment);
        System.out.print("[APARTMENT ADDED] ");
        apartment.display();
    }

    public void addAccommodation(GuestHouse guestHouse) {
        accommodations.add(guestHouse);
        System.out.print("[GUESTHOUSE ADDED] ");
        guestHouse.display();
    }

    public void addHotel(Hotel hotel) {
        addAccommodation(hotel);
    }
    public void addApartment(Apartment apt) {
        addAccommodation(apt);
    }
    public void addGuestHouse(GuestHouse gh) {
        addAccommodation(gh);
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

    // ============================================================
    // BOOKING MANAGEMENT - Core booking operations with validation
    // ============================================================
    // Overload 1 — core method, handles all validation
    public void addBooking(Booking booking) throws InvalidBookingException {
        if (booking == null || booking.getUser() == null || booking.getAccommodation() == null) {
            throw new InvalidBookingException("Invalid booking data!");
        }
        if (!booking.isValidDates()) {
            throw new InvalidBookingException("Check-out must be after check-in!");
        }
        if (!isAvailable(booking.getAccommodation().getAccId(),
                booking.getCheckInDate(), booking.getCheckOutDate())) {
            throw new InvalidBookingException("Booking #" + booking.getBookingId()
                    + " rejected — accommodation not available for those dates!");
        }
        allBookings.put(booking.getBookingId(), booking);
        System.out.println("[OK] Booking #" + booking.getBookingId() + " created for "
                + booking.getUser().getName()
                + " @ " + booking.getAccommodation().getName());
    }

    // Overload 2 — convenience for logged-in users. if user is null, the system
    // should navigate to account creation before calling this method
    public void addBooking(User user, Accommodation accommodation,
            String checkIn, String checkOut) throws InvalidBookingException {
        if (user == null) {
            System.out.println("No account found. Please create an account before booking.");
            return;
        }
        int newId = allBookings.size() + 101;
        Booking booking = new Booking(newId, user, accommodation,
                checkIn, checkOut, BookingStatus.CONFIRMED);
        addBooking(booking); //pass the validation 
    }
    
    // ============================================================
    // STATUS UPDATES - Modify booking status with validation
    // ============================================================
    public void updateBookingStatus(int bookingId, BookingStatus newStatus) {
        Booking booking = allBookings.get(bookingId);
        if (booking == null) {
            System.out.println("[ERROR] Booking #" + bookingId + " not found!");
            return;
        }
        BookingStatus old = booking.getStatus();
        if (!booking.canChangeTo(newStatus)) {
            System.out.println("[ERROR] Cannot change Booking #" + bookingId
                    + " from " + old + " to " + newStatus);
            return;
        }
        booking.setStatus(newStatus);
        System.out.println("[OK] Booking #" + bookingId + ": " + old + " -> " + newStatus);
    }

    // For TESTING purposes ONLY
    public void hardDeleteBooking(int bookingId) {
        Booking removed = allBookings.remove(bookingId);
        if (removed == null) {
            System.out.println("[ERROR] Booking #" + bookingId + " not found!");
        } else {
            System.out.println("[OK] Booking #" + bookingId + " deleted.");
        }
    }

    // ============================================================
    // BOOKING QUERIES - get booking data and history for users and accommodations
    // ============================================================
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

    // ============================================================
    // AVAILABILITY CHECKS - Verify accommodation availability for dates
    // ============================================================
    public boolean isAvailable(int accommodationId, String checkIn, String checkOut) {
        Accommodation acc = findAccommodationById(accommodationId);
        if (acc == null) {
            System.out.println("[ERROR] Accommodation ID " + accommodationId + " not found!");
            return false;
        }

        // If the check-in or check-out strings are in "YYYY-MM-DD" format, append "T00:00:00" to make them valid LocalDateTime strings
        if (checkIn.length() == 10) {
            checkIn += "T00:00:00";
        }
        if (checkOut.length() == 10) {
            checkOut += "T00:00:00";
        }
        // Parse the check-in and check-out strings into LocalDateTime objects first cause main method is using LocalDateTime format
        LocalDateTime newInTime = LocalDateTime.parse(checkIn);
        LocalDateTime newOutTime = LocalDateTime.parse(checkOut);
        // then convert to LocalDate for comparison with existing bookings
        LocalDate newIn = newInTime.toLocalDate();
        LocalDate newOut = newOutTime.toLocalDate();
        // Business logic is in Accommodation class
        return acc.isAvailable(newIn, newOut, allBookings.values());
    }

    // checks all accommodations and returns a list of those available for the given date range
    public List<Accommodation> getAvailableRooms(String checkIn, String checkOut) {
        List<Accommodation> availableResults = new ArrayList<>();

        for (Accommodation acc : accommodations) {
            if (isAvailable(acc.getAccId(), checkIn, checkOut)) {
                availableResults.add(acc);
            }
        }
        return availableResults;
    }

    // ============================================================
    // PAYMENT MANAGEMENT - Record and retrieve payment transactions
    // ============================================================
    public void addPayment(Payment payment) {
        if (payment == null || payment.getBooking() == null) {
            System.out.println("[ERROR] Payment must be linked to a valid booking!");
            return;
        }
        if (!payment.isValidMethod()) {
            System.out.println("[ERROR] Invalid payment method: " + payment.getMethod());
            return;
        }
        payments.add(payment);
        System.out.println("[OK] Payment #" + payment.getPaymentId() + " recorded.");
    }

    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments);
    }

    // ============================================================
    // STATISTICS - Count various entities in the system
    // ============================================================
    // COUNTS the number of bookings, hotels, guest houses, and apartments in the system
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

    // ============================================================
    // SEARCHABLE INTERFACE IMPLEMENTATION - Search across all entities
    // ============================================================
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
    public List<Booking> searchBookingsByDateRange(String startDate, String endDate,
            BookingStatus status) {
        List<Booking> results = new ArrayList<>();
        // Handle potential time stamps in parameters
        if (startDate.length() == 10) startDate += "T00:00:00";
        if (endDate.length() == 10) endDate += "T00:00:00";

        LocalDate start = LocalDateTime.parse(startDate).toLocalDate();
        LocalDate end = LocalDateTime.parse(endDate).toLocalDate();
        for (Booking b : allBookings.values()) {
            String InStr = b.getCheckInDate();
            String OutStr = b.getCheckOutDate();
            
            if (InStr.length() == 10) InStr += "T00:00:00";
            if (OutStr.length() == 10) OutStr += "T00:00:00";
            
            LocalDate in = LocalDateTime.parse(InStr).toLocalDate();
            LocalDate out = LocalDateTime.parse(OutStr).toLocalDate();
            if (!in.isAfter(end) && !out.isBefore(start) && b.getStatus() == status)
                results.add(b);
        }
        return results;
    }

    // ============================================================
    // DISPLAYABLE INTERFACE IMPLEMENTATION - Display all system data in a report format
    // ============================================================
    public void displayAllData(Displayable reportStyle) {
    // 1. Run the custom title style passed from Main (polymorphic behavior)
    reportStyle.display();
    
    // 2. Print statistics summary
    System.out.println("Users: " + users.size() + " | Accommodations: " + accommodations.size() + " | Bookings: " + allBookings.size());
    System.out.println("\n" + "=".repeat(60));
    System.out.println("SYSTEM DATA OVERVIEW");
    System.out.println("=".repeat(60));

    // 3. Display all users
    System.out.println("\n[USERS (" + users.size() + ")]");
    for (User u : users) {
        u.display();
    }

    // 4. Display all accommodations
    System.out.println("\n[ACCOMMODATIONS (" + accommodations.size() + ")]");
    for (Accommodation a : accommodations) {
        a.display();
    }

    // 5. Display all bookings
    System.out.println("\n[BOOKINGS (" + allBookings.size() + ")]");
    for (Booking b : allBookings.values()) {
        b.display();
    }

    // 6. Display all payments
    System.out.println("\n[PAYMENTS (" + payments.size() + ")]");
    for (Payment p : payments) {
        p.display();
    }
    System.out.println("=".repeat(60));
}
}