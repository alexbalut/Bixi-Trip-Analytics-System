package ca.concordia.view;

import ca.concordia.controller.BixiController;
import ca.concordia.controller.IBixiController;
import ca.concordia.model.BixiTrip;
import ca.concordia.model.BixiStation;
import ca.concordia.model.RushHour;

import java.util.Scanner;

public class BixiView {

    private IBixiController controller;
    private Scanner scanner;

    /**
     * Constructor for BixiView.
     * Initializes the controller
     */
    public BixiView(){
        controller = new BixiController();
        scanner = new Scanner(System.in);
    }

    /**
     * Starts the Bixi data viewer application.
     */
    public void start() {
        String message = "Welcome to the Bixi Data Viewer!";
        System.out.println(message);
        System.out.print("Please enter the path to the Bixi data file: ");
        String filePath = scanner.nextLine();
        //CHANGE THIS LATER
        controller.loadFile(filePath);
        //TODO - Complete
        boolean running = true;
        while (running) {
            System.out.println("\n--- Menu ---");
            System.out.println("[0] Exit");
            System.out.println("[1] REQ 1 - List trips by station");
            System.out.println("[2] REQ 2 - List trips by month");
            System.out.println("[3] REQ 3 - List trips by duration");
            System.out.println("[4] REQ 4 - List trips within a time interval");
            System.out.println("[6] REQ 6 - Top K start stations in a period");
            System.out.println("[7] REQ 7 - Rush hour for a given month");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();
            double duration = System.nanoTime();
            switch (choice) {

                case "1":
                    handleReq1();
                    System.out.println(duration);
                    break;
                case "0":
                    running = false;
                    System.out.println("Goodbye!");

                    break;
                case "2":
                    handleReq2();
                    System.out.println(duration);
                    break;
                case "3":
                    handleReq3();
                    System.out.println(duration);
                    break;
                case "4":
                    handleReq4();
                    System.out.println(duration);
                    break;
                case "6":
                    handleReq6();
                    System.out.println(duration);
                    break;
                case "7":
                    handleReq7();
                    System.out.println(duration);
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    private void handleReq1() {
        System.out.print("Enter station name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Filter by [start / end / both]: ");
        String mode = scanner.nextLine().trim();

        Iterable<BixiTrip> results = controller.getTripsByStation(name, mode);

        int count = 0;
        for (BixiTrip trip : results) {
            System.out.println("\n--- Trip " + (++count) + " ---");
            System.out.println(trip);
        }

        if (count == 0) {
            System.out.println("No trips found for station: " + name);
        } else {
            System.out.println("\nTotal matching trips: " + count);
        }
    }

    private void handleReq2(){
        System.out.println("Enter a month in the format YYYY-MM (e.g., 2025-06)");
        String YEAR_MONTH = scanner.nextLine();
        String month = YEAR_MONTH;
        Iterable<BixiTrip> results = controller.getTripsByMonth(month);

        int count = 0;
        for (BixiTrip trip : results){
            System.out.println("\n--- Trip " + (++count) + " ---");
            System.out.println(trip);
        }
        if (count == 0) {
            System.out.println("No trips found for month: " + month);
        } else {
            System.out.println("\nTotal matching trips: " + count);
        }
    }

    private void handleReq3() {
        System.out.print("Enter minimum duration in minutes: ");
        float minDuration = Float.parseFloat(scanner.nextLine().trim());
        Iterable<BixiTrip> results = controller.getTripsByDuration(minDuration);
        int count = 0;
        for (BixiTrip trip : results) {
            System.out.println("\n--- Trip " + (++count) + " ---");
            System.out.println("From:     " + trip.getStartStationName());
            System.out.println("To:       " + trip.getEndStationName());
            System.out.printf( "Duration: %.2f min%n", trip.getDurationMinutes());
            System.out.println("Start ms: " + trip.getStartTimeMs());
            System.out.println("End ms:   " + trip.getEndTimeMs());
        }
        if (count == 0) {
            System.out.println("No trips found exceeding " + minDuration + " minutes.");
        } else {
            System.out.println("\nTotal matching trips: " + count);
        }
    }

    private void handleReq4() {
        System.out.print("Enter start time (YYYY-MM-DD HH:mm:ss): ");
        String startTime = scanner.nextLine().trim();

        System.out.print("Enter end time (YYYY-MM-DD HH:mm:ss): ");
        String finalTime = scanner.nextLine().trim();

        Iterable<BixiTrip> results = controller.getTripsByStartTime(startTime, finalTime);

        int count = 0;
        for (BixiTrip trip : results) {
            System.out.println("\n--- Trip " + (++count) + " ---");
            System.out.println(trip);
        }

        if (count == 0) {
            System.out.println("No trips found in that interval.");
        } else {
            System.out.println("\nTotal matching trips: " + count);
        }
    }

    private void handleReq6() {
        System.out.print("Enter K (number of top stations): ");
        int k = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter start time (YYYY-MM-DD HH:mm:ss): ");
        String startDate = scanner.nextLine().trim();
        System.out.print("Enter end time (YYYY-MM-DD HH:mm:ss): ");
        String endDate = scanner.nextLine().trim();

        Iterable<BixiStation> results = controller.getTopStations(k, startDate, endDate);

        int count = 0;
        for (BixiStation station : results) {
            System.out.println((++count) + ". " + station);
        }

        if (count == 0){
            System.out.println("No stations found in that period.");
        }
    }

    private void handleReq7() {
        System.out.print("Enter month number (1-12): ");
        int month = Integer.parseInt(scanner.nextLine().trim());

        RushHour result = controller.getRushHourOfMonth(month);
        System.out.println(result);
    }
}