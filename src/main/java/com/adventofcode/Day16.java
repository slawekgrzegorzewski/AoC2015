package com.adventofcode;

import com.adventofcode.input.Input;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class Day16 {

    private static final Map<String, Integer> DETECTED_PROPERTIES = Map.of(
            "children", 3,
            "cats", 7,
            "samoyeds", 2,
            "pomeranians", 3,
            "akitas", 0,
            "vizslas", 0,
            "goldfish", 5,
            "trees", 3,
            "cars", 2,
            "perfumes", 1);

    private final List<Input.AuntSue> aunties;

    public Day16() throws IOException {
        aunties = Input.day16();
    }

    long part1() {
        return findAunt(1);
    }

    long part2() {
        return findAunt(2);
    }

    private int findAunt(int part) {
        List<Input.AuntSue> matchingAunties = aunties.stream()
                .filter(auntSue -> auntieMatches(auntSue, part))
                .toList();
        if (matchingAunties.size() != 1)
            throw new IllegalStateException("More than one matching auntie found");
        return matchingAunties.getFirst().id();
    }

    private boolean auntieMatches(Input.AuntSue auntSue, int part) {
        return auntSue.properties().entrySet()
                .stream()
                .allMatch(entry -> getPropertyComparator(entry.getKey(), entry.getValue(), part)
                        .apply(DETECTED_PROPERTIES.get(entry.getKey())));
    }

    Function<Integer, Boolean> getPropertyComparator(String propertyName, int propertyValue, int part) {
        if (part == 2 && ("cats".equals(propertyName) || "trees".equals(propertyName))) {
            return value -> value < propertyValue;
        }
        if (part == 2 && ("pomeranians".equals(propertyName) || "goldfish".equals(propertyName))) {
            return value -> value > propertyValue;
        } else return value -> Objects.equals(value, propertyValue);
    }
}