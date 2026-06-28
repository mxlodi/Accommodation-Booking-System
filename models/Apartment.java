package models;

public class Apartment extends Accommodation {
    // --- FIELDS ---
    private int floor;
    private boolean hasElevator;

    // --- CONSTRUCTOR ---
    public Apartment(int id, String name, double price, int capacity,
                     int floor, boolean elevator) {
        super(id, name, price, capacity);
        this.floor = floor;
        this.hasElevator = elevator;
    }

    // --- GETTERS ---
    public int getFloor() { return floor; }
    public boolean hasElevator()  { return hasElevator; }

    // --- OVERRIDDEN METHODS ---
    @Override
    public String getType() { return "APARTMENT"; }

    @Override
    public void display() {
        super.display();   // prints name, id, price, capacity
        System.out.println("  Floor    : " + floor);
        System.out.println("  Elevator : " + (hasElevator ? "Yes" : "No"));
    }
}