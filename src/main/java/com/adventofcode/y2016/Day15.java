package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day15 {
    private final Map<Integer, Disc> discs;


    public Day15() throws IOException {
        this.discs = Input.day15();
    }

    long part1() {
        return work(discs);
    }

    long part2() {
        HashMap<Integer, Disc> newDiscs = new HashMap<>(discs);
        int nextNumber = newDiscs.keySet().stream().max(Integer::compareTo).orElseThrow() + 1;
        newDiscs.put(nextNumber, new Disc(nextNumber, 11, 0));
        return work(newDiscs);
    }

    private int work(Map<Integer, Disc> discs) {
        int time = -1;
        while (soarAwayDiscNumber(++time, discs).isPresent()) ;
        return time;
    }

    private OptionalInt soarAwayDiscNumber(int releaseTime, Map<Integer, Disc> discs) {
        return discs.entrySet().stream()
                .filter(discEntry -> discEntry.getValue().soarAway(releaseTime))
                .sorted(Map.Entry.comparingByKey())
                .mapToInt(Map.Entry::getKey)
                .findFirst();
    }

    public record Disc(int number, int size, int startPosition) {
        public static final Pattern LINE_PATTERN = Pattern.compile("Disc #(\\d+) has (\\d+) positions; at time=0, it is at position (\\d+).");

        public static Disc parse(String line) {
            Matcher matcher = LINE_PATTERN.matcher(line);
            if (matcher.matches())
                return new Disc(Integer.parseInt(matcher.group(1)),
                        Integer.parseInt(matcher.group(2)),
                        Integer.parseInt(matcher.group(3)));
            throw new IllegalArgumentException("Invalid input line: " + line);
        }

        public boolean soarAway(int releaseTime) {
            return (startPosition + number + releaseTime) % size != 0;
        }
    }
}
