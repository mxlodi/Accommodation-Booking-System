package models;

public class Hotel extends Accommodation {
    // --- FIELDS ---
    private int stars;
    private boolean hasPool;
    private boolean hasBreakfast;

    // --- CONSTRUCTOR ---
    public Hotel(int id, String name, double price, int capacity,
                 int stars, boolean pool, boolean breakfast) {
        super(id, name, price, capacity);
        this.stars = stars;
        this.hasPool = pool;
        this.hasBreakfast = breakfast;
    }

    // --- GETTERS ---
    public int getStars()        { return stars; }
    public boolean hasPool()     { return hasPool; }
    public boolean hasBreakfast(){ return hasBreakfast; }

    // --- OVERRIDDEN METHODS ---
    @Override
    public String getType() { return "HOTEL"; }

    @Override
    public void display() {
        super.display();    // prints name, id, price, capacity
        System.out.println("  Stars    : " + stars);
        System.out.println("  Pool     : " + (hasPool ? "Yes" : "No"));
        System.out.println("  Breakfast: " + (hasBreakfast ? "Yes" : "No"));
    }
}