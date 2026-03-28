package ca.concordia.controller;

import ca.concordia.dsa.DynamicArray;
import ca.concordia.dsa.HashMap;
import ca.concordia.model.Arrondissement;
import ca.concordia.model.BixiStation;
import ca.concordia.model.BixiTrip;
import ca.concordia.model.RushHour;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BixiController implements IBixiController {
    private DynamicArray<BixiTrip> trips = new DynamicArray<>();

    @Override
    public void loadFile(String filePath) {
        System.out.println("Loading file from: " + filePath);
        Path path = Path.of(filePath);

        try (var lines = Files.lines(path)) {
            lines.skip(1).forEach(this::parseLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Total trips loaded: " + trips.size());
    }

    private void parseLine(String data) {
        String[] fields = data.split(",");
        if (fields.length < 10) return;
        try {
            String startName = fields[0];
            String startArron = fields[1];
            double startLat = Double.parseDouble(fields[2]);
            double startLng = Double.parseDouble(fields[3]);
            String endName = fields[4];
            String endArron = fields[5];
            double endLat = Double.parseDouble(fields[6]);
            double endLng = Double.parseDouble(fields[7]);
            long startMs = Long.parseLong(fields[8]);
            long endMs = Long.parseLong(fields[9]);
            trips.add(new BixiTrip(startName, startArron, startLat, startLng,
                    endName, endArron, endLat, endLng, startMs, endMs));
        } catch (NumberFormatException e) {
            // skip rows with wrong format
        }
    }

    @Override
    public Iterable<BixiTrip> getTripsByStation(String stationName, String mode) {
        DynamicArray<BixiTrip> result = new DynamicArray<>();
        String search = stationName.toLowerCase();
        for (BixiTrip trip : trips) {
            boolean matchStart = trip.getStartStationName().toLowerCase().contains(search);
            boolean matchEnd = trip.getEndStationName().toLowerCase().contains(search);
            boolean include;
            if (mode.equalsIgnoreCase("start")) {
                include = matchStart;
            } else if (mode.equalsIgnoreCase("end")) {
                include = matchEnd;
            } else {
                include = matchStart || matchEnd;
            }
            if (include) result.add(trip);
        }
        return result;
    }

    @Override
    public Iterable<BixiTrip> getTripsByMonth(String month) {
        String[] parts = month.split("-");
        int targetYear = Integer.parseInt(parts[0]);
        int targetMonth = Integer.parseInt(parts[1]);

        DynamicArray<BixiTrip> result = new DynamicArray<>();
        for (BixiTrip trip : trips) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTimeInMillis(trip.getStartTimeMs());
            int tripYear = cal.get(java.util.Calendar.YEAR);
            int tripMonth = cal.get(java.util.Calendar.MONTH) + 1;
            if (tripYear == targetYear && tripMonth == targetMonth) {
                result.add(trip);
            }
        }

        return mergeSortByTime(result);
    }

    @Override
    public Iterable<BixiTrip> getTripsByDuration(float minDuration) {
        DynamicArray<BixiTrip> result = new DynamicArray<>();
        for (BixiTrip trip : trips) {
            if (trip.getDurationMinutes() > minDuration) {
                result.add(trip);
            }
        }

        return mergeSortByDuration(result);
    }

    @Override
    public Iterable<BixiTrip> getTripsByStartTime(String startTime, String finalTime) {
        long startMs = parseTimestamp(startTime);
        long endMs = parseTimestamp(finalTime);

        DynamicArray<BixiTrip> result = new DynamicArray<>();
        for (BixiTrip trip : trips) {
            if (trip.getStartTimeMs() >= startMs && trip.getStartTimeMs() <= endMs) {
                result.add(trip);
            }
        }

        return mergeSortByTime(result);
    }

    private long parseTimestamp(String timestamp) {
        //format: YYYY-MM-DD HH:mm:ss
        String[] parts = timestamp.split(" ");
        String[] dateParts = parts[0].split("-");
        String[] timeParts = parts[1].split(":");

        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;
        int day = Integer.parseInt(dateParts[2]);
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        int second = Integer.parseInt(timeParts[2]);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month, day, hour, minute, second);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    // Sort ascending by start time
    private DynamicArray<BixiTrip> mergeSortByTime(DynamicArray<BixiTrip> arr){
        if(arr.size() <= 1) return arr;
        int mid = arr.size() / 2;
        DynamicArray<BixiTrip> left = new DynamicArray<>();
        DynamicArray<BixiTrip> right = new DynamicArray<>();
        for(int i = 0; i < mid; i++) left.add(arr.get(i));
        for (int i = mid; i < arr.size(); i++) right.add(arr.get(i));
        left = mergeSortByTime(left);
        right = mergeSortByTime(right);
        return mergeByTime(left, right);
    }

    private DynamicArray<BixiTrip> mergeByTime(DynamicArray<BixiTrip> left, DynamicArray<BixiTrip> right){
        DynamicArray<BixiTrip> result = new DynamicArray<>();
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            if (left.get(i).getStartTimeMs() <= right.get(j).getStartTimeMs()){
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }

        while(i < left.size()){
            result.add(left.get(i++));
        }
        while(j < right.size()){
            result.add(right.get(j++));
        }
        return result;
    }

    // Sort descending by duration
    private DynamicArray<BixiTrip> mergeSortByDuration(DynamicArray<BixiTrip> arr) {
        if(arr.size() <= 1) return arr;
        int mid = arr.size() / 2;
        DynamicArray<BixiTrip> left = new DynamicArray<>();
        DynamicArray<BixiTrip> right = new DynamicArray<>();
        for(int i = 0; i < mid; i++) left.add(arr.get(i));
        for (int i = mid; i < arr.size(); i++) right.add(arr.get(i));
        left = mergeSortByDuration(left);
        right = mergeSortByDuration(right);
        return mergeByDuration(left, right);
    }

    private DynamicArray<BixiTrip> mergeByDuration(DynamicArray<BixiTrip> left, DynamicArray<BixiTrip> right){
        DynamicArray<BixiTrip> result = new DynamicArray<>();
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            if(left.get(i).getDurationMinutes() >= right.get(j).getDurationMinutes()) {
                result.add(left.get(i++));
            } else{
                result.add(right.get(j++));
            }
        }

        while(i < left.size()){
            result.add(left.get(i++));
        }
        while(j < right.size()){
            result.add(right.get(j++));
        }
        return result;
    }

    @Override
    public Iterable<Arrondissement> getTopArrondissements(int k) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Iterable<BixiStation> getTopStations(int k, String startDate, String endDate) {
        long startMs = parseTimestamp(startDate);
        long endMs = parseTimestamp(endDate);

        HashMap<String, Integer> counts = new HashMap<>();
        for (BixiTrip trip : trips) {
            if (trip.getStartTimeMs() >= startMs && trip.getStartTimeMs() <= endMs) {
                String name = trip.getStartStationName();
                if (counts.containsKey(name)) {
                    counts.put(name, counts.get(name) + 1);
                } else {
                    counts.put(name, 1);
                }
            }
        }

        DynamicArray<String> keys = counts.getKeys();
        DynamicArray<BixiStation> stations = new DynamicArray<>();
        for (int i = 0; i < keys.size(); i++) {
            String name = keys.get(i);
            stations.add(new BixiStation(name, counts.get(name)));
        }

        // Sort by count to find top k
        stations = mergeSortByCount(stations);

        // Take top k
        DynamicArray<BixiStation> result = new DynamicArray<>();
        for (int i = 0; i < k && i < stations.size(); i++) {
            result.add(stations.get(i));
        }

        // Sort top k alphabetically
        result = mergeSortByName(result);
        return result;
    }

    // Sort descending by trip count
    private DynamicArray<BixiStation> mergeSortByCount(DynamicArray<BixiStation> arr) {
        if (arr.size() <= 1) return arr;
        int mid = arr.size() / 2;
        DynamicArray<BixiStation> left = new DynamicArray<>();
        DynamicArray<BixiStation> right = new DynamicArray<>();
        for (int i = 0; i < mid; i++) {
            left.add(arr.get(i));
        }
        for (int i = mid; i < arr.size(); i++) {
            right.add(arr.get(i));
        }
        left = mergeSortByCount(left);
        right = mergeSortByCount(right);
        return mergeByCount(left, right);
    }

    private DynamicArray<BixiStation> mergeByCount(DynamicArray<BixiStation> left, DynamicArray<BixiStation> right) {
        DynamicArray<BixiStation> result = new DynamicArray<>();
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            if (left.get(i).getTripCount() >= right.get(j).getTripCount()) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }
        while (i < left.size()) {
            result.add(left.get(i++));
        }
        while (j < right.size()) {
            result.add(right.get(j++));
        }
        return result;
    }

    private DynamicArray<BixiStation> mergeSortByName(DynamicArray<BixiStation> arr) {
        if (arr.size() <= 1) return arr;
        int mid = arr.size() / 2;
        DynamicArray<BixiStation> left = new DynamicArray<>();
        DynamicArray<BixiStation> right = new DynamicArray<>();
        for (int i = 0; i < mid; i++) {
            left.add(arr.get(i));
        }
        for (int i = mid; i < arr.size(); i++) {
            right.add(arr.get(i));
        }
        left = mergeSortByName(left);
        right = mergeSortByName(right);
        return mergeByName(left, right);
    }

    private DynamicArray<BixiStation> mergeByName(DynamicArray<BixiStation> left, DynamicArray<BixiStation> right) {
        DynamicArray<BixiStation> result = new DynamicArray<>();
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            if (left.get(i).getName().compareTo(right.get(j).getName()) <= 0) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }
        while (i < left.size()) {
            result.add(left.get(i++));
        }
        while (j < right.size()) {
            result.add(right.get(j++));
        }
        return result;
    }

    @Override
    public RushHour getRushHourOfMonth(int month) {
        HashMap<Integer, Integer> hourCounts = new HashMap<>();
        for (BixiTrip trip : trips){
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTimeInMillis(trip.getStartTimeMs());
            if (cal.get(java.util.Calendar.MONTH) + 1 == month){
                int hour = cal.get(java.util.Calendar.HOUR_OF_DAY);
                if (hourCounts.containsKey(hour)) {
                    hourCounts.put(hour, hourCounts.get(hour) + 1);
                } else{
                    hourCounts.put(hour, 1);
                }
            }
        }

        // O(n) but technically only 24 hours so O(24) -> O(1)
        int rushHour = 0;
        int maxCount = 0;
        DynamicArray<Integer> hours = hourCounts.getKeys();
        for(int i = 0; i < hours.size(); i++){
            int hour = hours.get(i);
            int count = hourCounts.get(hour);
            if (count > maxCount){
                maxCount = count;
                rushHour = hour;
            }
        }

        return new RushHour(rushHour, maxCount);
    }
}