package ca.concordia.model;

public class BixiStation {
    private String name;
    private int tripCount;

    public BixiStation(String name, int tripCount) {
        this.name = name;
        this.tripCount = tripCount;
    }

    public String getName() { return name; }
    public int getTripCount() { return tripCount; }

    @Override
    public String toString() {
        return name + " — " + tripCount + " trips";
    }
}
