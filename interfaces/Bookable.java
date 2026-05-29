
package interfaces;

public interface Bookable {
    boolean canAccommodate(int numberOfGuests);
    double calculatePrice(int nights);
    int getCapacity();
    double getPricePerNight();
}
