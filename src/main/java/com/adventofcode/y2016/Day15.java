package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
        int modulus = discs.values().stream().mapToInt(Disc::size).reduce(1, (a, b) -> a * b);
        return discs.values()
                .stream()
                .map(disc -> calculateCRTPartial(disc, modulus))
                .reduce(0, Integer::sum) % modulus;
    }

    public static int calculateCRTPartial(Disc disc, int modulus) {
        return (disc.size - disc.startPosition - disc.number) //a
                * (modulus / disc.size) //M
                * findModularInverse(modulus / disc.size, disc.size); //M^-1
    }

    private static int findModularInverse(int modular, int modulo) {
        int modularInverse = -1;
        while ((modular * (++modularInverse)) % modulo != 1) ;
        return modularInverse;
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
    }

}
