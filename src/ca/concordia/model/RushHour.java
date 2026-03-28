package ca.concordia.model;

public class RushHour {
    private int hour;
    private int tripCount;

    public RushHour(int hour, int tripCount) {
        this.hour = hour;
        this.tripCount = tripCount;
    }

    public int getHour() { return hour; }
    public int getTripCount() { return tripCount; }

    @Override
    public String toString() {
        return "Rush hour: " + hour + ":00 with " + tripCount + " trips";
    }
}
