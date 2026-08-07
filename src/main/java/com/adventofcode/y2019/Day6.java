package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Day6 {
    private final Map<String, List<String>> orbits;


    public Day6() throws IOException {
        this.orbits = Input.day6();
    }

    long part1() {
        return sum(orbits, "COM", 0);
    }

    public long sum(Map<String, List<String>> orbits, String object, long depth) {
        return depth + orbits.getOrDefault(object, List.of())
                .stream()
                .mapToLong(s -> sum(orbits, s, depth + 1))
                .sum();
    }

    long part2() {
        List<String> you = findPath(orbits, "COM", "YOU");
        List<String> santa = findPath(orbits, "COM", "SAN");
        long divergeDepth = 0;
        for (int i = 0; i < you.size(); i++) {
            if (!you.get(i).equals(santa.get(i))) {
                divergeDepth = i;
                break;
            }
        }
        return you.size() + santa.size() - divergeDepth * 2 - 2;
    }

    private List<String> findPath(Map<String, List<String>> orbits, String object, String target) {
        if (object.equals(target)) {
            List<String> path = new ArrayList<>();
            path.add(object);
            return path;
        }
        for (String child : orbits.getOrDefault(object, List.of())) {
            List<String> path = findPath(orbits, child, target);
            if (!path.isEmpty()) {
                path.addFirst(object);
                return path;
            }
        }
        return List.of();
    }
}
