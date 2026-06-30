package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;

public class Day6 {
    private final List<String> input;


    public Day6() throws IOException {
        this.input = Input.day6();
    }

    String part1() {
        return getMessage(false);
    }

    String part2() {
        return getMessage(true);
    }

    private @NonNull String getMessage(boolean part2) {
        List<Map<Character, Integer>> charFrequencyInColumns = new ArrayList<>();
        for (int i = 0; i < input.getFirst().length(); i++) {
            charFrequencyInColumns.add(new HashMap<>());
        }
        for (String line : input) {
            char[] lineCharArray = line.toCharArray();
            for (int i = 0; i < lineCharArray.length; i++) {
                charFrequencyInColumns.get(i).compute(lineCharArray[i], (_, v) -> v == null ? 1 : v + 1);
            }
        }
        StringBuilder message = new StringBuilder();
        for (Map<Character, Integer> charFrequency : charFrequencyInColumns) {
            Comparator<Map.Entry<Character, Integer>> comparator = Map.Entry.comparingByValue();
            comparator = part2 ? comparator : comparator.reversed();
            charFrequency
                    .entrySet()
                    .stream()
                    .min(comparator)
                    .ifPresent(e -> message.append(e.getKey()));
        }
        return message.toString();
    }
}
