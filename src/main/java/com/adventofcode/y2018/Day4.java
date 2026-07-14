package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day4 {

    public static final Comparator<Timestamp> TIMESTAMP_COMPARATOR = Comparator.comparing(Timestamp::year)
            .thenComparing(Timestamp::month)
            .thenComparing(Timestamp::day)
            .thenComparing(Timestamp::hour)
            .thenComparing(Timestamp::minute);
    private final Map<Timestamp, String> input;


    public Day4() throws IOException {
        this.input = Input.day4();
    }

    long part1() {
        return work(Comparator.comparing(entry -> entry.getValue().values().stream().mapToInt(i -> i).sum()));
    }

    long part2() {
        return work(Comparator.comparing(entry -> entry.getValue().values().stream().mapToInt(i -> i).max().orElseThrow()));
    }

    private long work(Comparator<Map.Entry<Integer, Map<Integer, Integer>>> comparator) {
        Map<Integer, Map<Integer, Integer>> minutesWhenGuardsAreAsleep = new HashMap<>();
        List<Timestamp> timestamps = input.keySet().stream().sorted(TIMESTAMP_COMPARATOR).toList();
        int guardId = 0;
        int minuteOfFallAsleep = -1;
        for (Timestamp timestamp : timestamps) {
            String event = input.get(timestamp);
            if (event.startsWith("Guard")) {
                guardId = Integer.parseInt(event.substring(7, event.indexOf(" begins")));
            } else if (event.equals("wakes up")) {
                for (int m = minuteOfFallAsleep; m < timestamp.minute; m++)
                    minutesWhenGuardsAreAsleep.computeIfAbsent(guardId, _ -> new HashMap<>())
                            .compute(m, (_, v) -> v == null ? 1 : v + 1);
                minuteOfFallAsleep = -1;
            } else if (event.equals("falls asleep")) {
                minuteOfFallAsleep = timestamp.minute;
            }
        }
        Map.Entry<Integer, Map<Integer, Integer>> pickedGuard = minutesWhenGuardsAreAsleep.entrySet()
                .stream()
                .max(comparator)
                .orElseThrow();
        return (long) pickedGuard.getKey() * pickedGuard.getValue().entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow().getKey();
    }

    public record Timestamp(int year, int month, int day, int hour, int minute) {
        private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("^\\[(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})]");

        public static Timestamp parse(String timestamp) {
            Matcher matcher = TIMESTAMP_PATTERN.matcher(timestamp);
            if (matcher.matches()) {
                return new Timestamp(
                        Integer.parseInt(matcher.group(1)),
                        Integer.parseInt(matcher.group(2)),
                        Integer.parseInt(matcher.group(3)),
                        Integer.parseInt(matcher.group(4)),
                        Integer.parseInt(matcher.group(5)));
            }
            throw new IllegalArgumentException("Invalid timestamp: " + timestamp);
        }
    }
}
