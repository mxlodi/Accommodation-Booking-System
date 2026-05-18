package models;

public class Apartment extends Accommodation {
    private int floor;
    private boolean hasElevator;

    public Apartment(int id, String name, double price, int capacity, int floor, boolean elevator) {
        super(id, name, price, capacity);
        this.floor = floor;
        this.hasElevator = elevator;
    }

    public int getFloor() { return floor; }
    public boolean hasElevator() { return hasElevator; }  // ← Fixed naming

    @Override
    public String getType() { return "APARTMENT"; }

    @Override
    public void display() {
        System.out.println("APARTMENT: " + getName() + " (Floor: " + floor + ")");
        System.out.println("Elevator: " + (hasElevator ? "Yes" : "No") + " | Price: $" + getPricePerNight());
    }

    @Override
    public void displayName() { System.out.println("Apartment: " + getName()); }
}