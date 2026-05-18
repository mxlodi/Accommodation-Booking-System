package models;

public class GuestHouse extends Accommodation {
    private boolean hasBreakfast;
    private boolean hasElevator;

    public GuestHouse(int guestHouseId, String name, double pricePerNight, int capacity,
            boolean hasBreakfast, boolean hasElevator) {
        super(guestHouseId, name, pricePerNight, capacity);
        this.hasBreakfast = hasBreakfast;
        this.hasElevator = hasElevator;
    }

    @Override
    public String getType() {
        return "GUEST_HOUSE";
    }

    @Override
    public void display() {
        System.out.println("- GUESTHOUSE DETAILS -");
        System.out.println("ID: " + getAccId());
        System.out.println("Name: " + getName());
        System.out.println("Price: $" + getPricePerNight() + "/night");
        System.out.println("Breakfast: " + (hasBreakfast ? "Yes" : "No"));
        System.out.println("Elevator: " + (hasElevator ? "Yes" : "No"));
    }

    // Fixed: Added the missing implementation of displayName()
    @Override
    public void displayName() {
        System.out.println("GuestHouse: " + getName());
    }
}