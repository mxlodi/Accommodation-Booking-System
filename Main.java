import models.*;
import repository.BookingRepository;

public class Main {
    public static void main(String[] args) {
        BookingRepository repo = new BookingRepository();

        // 1. Creating specific child subclasses to demonstrate Inheritance
        User guest = new User(1, "Teddy", "teddy@email.com", "12345");
        Hotel plaza = new Hotel(10, "Hotel Plaza", 200.0, 100, 5, true, true);
        Apartment loft = new Apartment(20, "City Loft", 120.0, 4, 10, true);

        repo.addUser(guest);
        repo.addHotel(plaza);
        repo.addApartment(loft);

        // EXTRA ADDITION: Actually display the child classes to prove Inheritance works!
        System.out.println("--- Inheritance & Polymorphism Demo ---");
        plaza.display();
        System.out.println();
        loft.display();

        System.out.println("\n--- Booking Demo ---");
        Booking b1 = new Booking(101, guest, plaza, "2026-10-01", "2026-10-05", BookingStatus.CONFIRMED);
        repo.addBooking(b1);

        // 2. Demonstrating Payment Processing 
        Payment p1 = new Payment(501, b1);
        p1.processPayment();
        p1.display();

        // 3. Demonstrating Business Logic for Status Transitions
        System.out.println("\n--- Status Transition Check ---");
        boolean canCancel = b1.canChangeTo(BookingStatus.CANCELLED);
        System.out.println("Can cancel confirmed booking? " + canCancel);
    }
}