import models.*;
import repository.BookingRepository;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        BookingRepository repo = new BookingRepository();

        // Setup
        User teddy = new User(1, "Teddy", "teddy@email.com", "12345");
        User alice = new User(2, "Alice", "alice@email.com", "67890");

        Hotel plaza = new Hotel(10, "Hotel Plaza", 200.0, 100, 5, true, true);
        Apartment loft = new Apartment(20, "City Loft", 120.0, 4, 10, true);
        GuestHouse family = new GuestHouse(30, "Family Guest House", 80.0, 3, true, false);

        repo.addUser(teddy);
        repo.addUser(alice);
        repo.addAccommodation(plaza);
        repo.addAccommodation(loft);
        repo.addAccommodation(family);

        System.out.println("\n=== DEMO ===\n");
        ArrayList<Accommodation> accommodations = new ArrayList<>();
        accommodations.add(plaza);
        accommodations.add(loft);
        accommodations.add(family);

        for (Accommodation acc : accommodations) {
            acc.display(); // calls Hotel / Apartment / GuestHouse version
            System.out.println("  Type: " + acc.getType());
            System.out.println();
        }

        // Polymorphic price calculation — same method call, different prices
        System.out.println("=== CalculatePrice (3 nights) ===");
        for (Accommodation acc : accommodations) {
            System.out.printf("  %-22s $%.2f%n", acc.getName(), acc.calculatePrice(3));
        }

        // Bookings through the polymorphic list
        System.out.println("\n=== Bookings ===");
        repo.addBooking(teddy, plaza, "2026-10-01T14:00", "2026-10-04T11:00");
        repo.addBooking(alice, loft, "2026-10-10T15:00", "2026-10-13T11:00");

        // === Payments ===
        Booking teddyBooking = repo.getAllBookings().iterator().next();

        // 1. Teddy pays online immediately (Becomes Paid: true)
        Payment p1 = new Payment(501, teddyBooking, Payment.PaymentMethod.ONLINE);
        p1.processPayment();
        repo.addPayment(p1);

        // 2. Alice reserves via Pay At Property (Stays Paid: false)
        Booking aliceBooking = (Booking) repo.getAllBookings().toArray()[1];
        Payment p2 = new Payment(502, aliceBooking, Payment.PaymentMethod.PAY_AT_PROPERTY);
        p2.processPayment(); //  pending at check-in
        repo.addPayment(p2);

        // --- SIMULATING THE FUTURE: ALICE ARRIVES IN OCTOBER 2026 ---
        System.out.println("\n>>> [SYSTEM SIMULATION]: Alice arrives at City Loft for Check-In...");
        // The property clerk swipes her card or scans her KHQR code
        p2.processPayment(Payment.PaymentMethod.ONLINE); // Using your Overload 2 method to switch method then process

        repo.displayAllData();

    }
}