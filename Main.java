import models.*;
import repository.BookingRepository;
import interfaces.*;

public class Main {
    public static void main(String[] args) {
        BookingRepository repo = new BookingRepository();

        System.out.println("==================================================");
        System.out.println("        ACCOMMODATION BOOKING SYSTEM DEMO         ");
        System.out.println("==================================================");

        // ------- [1] SYSTEM INITIALIZATION ------- 
        System.out.println("[1] System Initialization");

        User teddy = new User(1, "Teddy", "teddy@email.com", "012345678");
        User alice = new User(2, "Alice", "alice@email.com", "098765432");

        Hotel plaza = new Hotel(10, "Hotel Plaza", 220.0, 100, 5, true, true);
        Apartment skyview = new Apartment(20, "Skyview Apartment", 150.0, 50, 3, true);
        GuestHouse cozy = new GuestHouse(30, "Cozy Guest House", 90.0, 30, true, false);

        repo.addUser(teddy);
        repo.addUser(alice);

        repo.addAccommodation(plaza);
        repo.addAccommodation(skyview);
        repo.addAccommodation(cozy);

        System.out.println("-> System initialized with 2 users and 3 accommodations.");
        System.out.println("-".repeat(60));


        //-------  [2] LESSON 10: EXCEPTION HANDLING ------- 
        System.out.println("[2] Exception Handling Demonstration");

        // Transaction #1: Valid Booking
        System.out.println("\n>>> Transaction #1: Valid Booking");
        Booking b1 = new Booking(101, teddy, plaza,
                "2026-10-01T14:00:00", "2026-10-04T11:00:00", BookingStatus.CONFIRMED);
        try {
            repo.addBooking(b1);
            System.out.println("[SUCCESS] Booking #101 created.");
        } catch (BookingRepository.InvalidBookingException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } finally {
            System.out.println("[FINALLY] Transaction #1 completed.");
        }
        // Transaction #2: Invalid Dates
        System.out.println("\n>>> Transaction #2: Invalid Dates");
        Booking b2 = new Booking(102, teddy, plaza,
                "2026-10-10T14:00:00", "2026-10-05T11:00:00", BookingStatus.CONFIRMED);
        try {
            repo.addBooking(b2);
        } catch (BookingRepository.InvalidBookingException e) {
            System.out.println("[CAUGHT] " + e.getMessage());
            System.out.println("[RECOVERY] System continued safely.");
        } finally {
            System.out.println("[FINALLY] Transaction #2 completed.");
        }
        // Transaction #3: Null Booking
        System.out.println("\n>>> Transaction #3: Null Booking");
        try {
            repo.addBooking(null);
        } catch (BookingRepository.InvalidBookingException e) {
            System.out.println("[CAUGHT] " + e.getMessage());
        } finally {
            System.out.println("[FINALLY] Transaction #3 completed.");
        }
        System.out.println("-".repeat(60));


        // -------  [3] LESSON 11: ANONYMOUS INNER CLASS vs LAMBDA ------- 
        System.out.println("[3] Anonymous Inner Class vs Lambda Expression");

        // Demo 1: Anonymous Inner Class - shows the syntax
        System.out.println("\n>>> Anonymous Inner Class (Traditional Way):");
        Displayable anonymousDisplay = new Displayable() {
            @Override
            public void display() {
                System.out.println("  [Anonymous] System has " + repo.getAllBookings().size() +
                        " booking(s), " + repo.getAllUsers().size() + " user(s)");
            }
        };
        anonymousDisplay.display();
        // Demo 2: Lambda Expression - much shorter
        System.out.println("\n>>> Lambda Expression (Modern Way):");
        Displayable lambdaDisplay = () -> System.out.println(
                "  [Lambda] System has " + repo.getAllBookings().size() +
                        " booking(s), " + repo.getAllUsers().size() + " user(s)");
        lambdaDisplay.display();

        // Show the difference in code structure
        System.out.println("\n>>> Code Comparison:");
        System.out.println("  Anonymous: new Displayable() { @Override public void display() { ... } }");
        System.out.println("  Lambda:    () -> { ... }");
        System.out.println("  -> Lambda is shorter and more readable for simple behaviors.");
        System.out.println("-".repeat(60));


        // -------  [4] SHOW FULL SYSTEM OVERVIEW ONLY ONCE -------
        System.out.println("[4] Full System Overview:");
        repo.displayAllData(() -> System.out.println("\n[SYSTEM REPORT] Current System State"));
        System.out.println("-".repeat(60));


        // -------  [5] ADDITIONAL DEMONSTRATIONS -------
        System.out.println("[5] Additional Demonstrations");

        //  filter
        System.out.println("\n>>> Luxury Accommodations (4+ stars with pool):");
        boolean found = false;
        for (Accommodation acc : repo.getAllAccommodations()) {
            if (acc instanceof Hotel) {
                Hotel h = (Hotel) acc;
                if (h.getStars() >= 4 && h.hasPool()) {
                    System.out.println(
                            "  - " + h.getName() + " ($" + h.getPricePerNight() + "/night, " + h.getStars() + "★)");
                    found = true;
                }
            }
        }
        if (!found)
            System.out.println("  None found.");

        // Price calculation
        System.out.println("\n>>> Price Calculation (3 nights, 10% discount):");
        double base = plaza.calculatePrice(3);
        double discounted = plaza.calculatePrice(3, 66.0); // $66 discount
        System.out.printf("  %s: $%.2f/night x 3 nights = $%.2f (with $66 discount: $%.2f)\n",
                plaza.getName(), plaza.getPricePerNight(), base, discounted);

        // Booking summary
        System.out.println("\n>>> Booking Summary:");
        for (Booking b : repo.getAllBookings()) {
            System.out.printf("  #%d: %s @ %s | %s -> %s | %s | $%.2f\n",
                    b.getBookingId(), b.getUser().getName(), b.getAccommodation().getName(),
                    b.getCheckInDate(), b.getCheckOutDate(), b.getStatus(), b.calculateTotalPrice());
        }
        System.out.println("-".repeat(60));

        // -------  [6] SYSTEM CLOSURE ------- 
        System.out.println("\n==================================================");
        System.out.println("          Program finished successfully.          ");
        System.out.println("==================================================");
    }
}