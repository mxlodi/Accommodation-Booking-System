package models;

public class Hotel extends Accommodation {
    private int stars;
    private boolean hasPool;
    private boolean hasBreakfast;

    public Hotel(int id, String name, double price, int capacity, int stars, boolean pool, boolean breakfast) {
        super(id, name, price, capacity);
        this.stars = stars;
        this.hasPool = pool;
        this.hasBreakfast = breakfast;
    }

    public int getStars() { return stars; }
    public boolean hasPool() { return hasPool; }
    public boolean hasBreakfast() { return hasBreakfast; }

    @Override
    public String getType() { return "HOTEL"; }

    @Override
    public void display() {
        System.out.println("HOTEL: " + getName() + " (" + stars + " Stars)");
        System.out.println("Price: $" + getPricePerNight() + "/night");
        System.out.println("Pool: " + (hasPool ? "Yes" : "No"));
        System.out.println("Breakfast: " + (hasBreakfast ? "Yes" : "No"));
    }

    @Override
    public void displayName() { System.out.println("Hotel: " + getName()); }
}