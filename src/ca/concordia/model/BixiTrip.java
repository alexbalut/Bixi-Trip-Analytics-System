package ca.concordia.model;

public class BixiTrip {
    private String startStationName;
    private String startArrondissement;
    private double startLatitude;
    private double startLongitude;
    private String endStationName;
    private String endArrondissement;
    private double endLatitude;
    private double endLongitude;
    private long startTimeMs;
    private long endTimeMs;

    public BixiTrip(String startStationName, String startArrondissement, double startLatitude,
                    double startLongitude, String endStationName, String endArrondissement,
                    double endLatitude, double endLongitude, long startTimeMs, long endTimeMs){

        this.startStationName = startStationName;
        this.startArrondissement = startArrondissement;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endStationName = endStationName;
        this.endArrondissement = endArrondissement;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.startTimeMs = startTimeMs;
        this.endTimeMs = endTimeMs;
    }

    public String getStartStationName() { return startStationName; }
    public String getStartArrondissement() { return startArrondissement; }
    public double getStartLatitude(){ return startLatitude; }
    public double getStartLongitude() { return startLongitude; }
    public String getEndStationName() { return endStationName; }
    public String getEndArrondissement() { return endArrondissement; }
    public double getEndLatitude() { return endLatitude; }
    public double getEndLongitude() { return endLongitude; }
    public long getStartTimeMs() { return startTimeMs; }
    public long getEndTimeMs() { return endTimeMs; }

    public double getDurationMinutes() {
        return (endTimeMs - startTimeMs) / (1000.0 * 60.0);
    }

    @Override
    public String toString() {
        return "From: " + startStationName +
                "\nTo:   " + endStationName +
                "\nStart time (ms): " + startTimeMs +
                "\nEnd time (ms):   " + endTimeMs +
                "\nDuration: " + getDurationMinutes() + " min";
    }
}

