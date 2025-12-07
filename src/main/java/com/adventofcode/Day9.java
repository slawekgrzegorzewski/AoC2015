package com.adventofcode;

import com.adventofcode.input.Input;

import java.io.IOException;
import java.util.*;

public class Day9 {

    private final MinMax minAndMaxDistances;

    public Day9() throws IOException {
        minAndMaxDistances = findMinAndMaxDistances(Input.day9());
    }

    long part1() {
        print(minAndMaxDistances.minRoute());
        return minAndMaxDistances.min();
    }

    long part2() {
        print(minAndMaxDistances.maxRoute());
        return minAndMaxDistances.max();
    }

    private MinMax findMinAndMaxDistances(Map<Input.StringPair, Integer> graph) {
        List<String> nodes = new ArrayList<>();
        graph.keySet().forEach(key -> {
            if (!nodes.contains(key.first())) nodes.add(key.first());
            if (!nodes.contains(key.second())) nodes.add(key.second());
        });
        int[] indexes = new int[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            indexes[i] = 0;
        }
        long minDistanceSeen = Long.MAX_VALUE;
        List<String> minRoute = Collections.emptyList();
        long maxDistanceSeen = Long.MIN_VALUE;
        List<String> maxRoute = Collections.emptyList();
        int i = 0;
        while (i < nodes.size()) {
            if (indexes[i] < i) {
                swap(nodes, i % 2 == 0 ? 0 : indexes[i], i);
                indexes[i]++;
                i = 0;
                OptionalLong distance = calculateDistance(nodes, graph);
                if (distance.isPresent() && distance.getAsLong() < minDistanceSeen) {
                    minDistanceSeen = distance.getAsLong();
                    minRoute = new ArrayList<>(nodes);
                }
                if (distance.isPresent() && distance.getAsLong() > maxDistanceSeen) {
                    maxDistanceSeen = distance.getAsLong();
                    maxRoute = new ArrayList<>(nodes);
                }
            } else {
                indexes[i] = 0;
                i++;
            }
        }
        return new MinMax(minDistanceSeen, minRoute, maxDistanceSeen, maxRoute);
    }

    private OptionalLong calculateDistance(List<String> nodes, Map<Input.StringPair, Integer> graph) {
        long distance = 0;
        for (int i = 0; i < nodes.size() - 1; i++) {
            String from = nodes.get(i);
            String to = nodes.get(i + 1);
            Input.StringPair keyOne = new Input.StringPair(from, to);
            Input.StringPair keyTwo = new Input.StringPair(to, from);
            if (graph.containsKey(keyOne))
                distance += graph.get(keyOne);
            else if (graph.containsKey(keyTwo))
                distance += graph.get(keyTwo);
            else return OptionalLong.empty();
        }
        return OptionalLong.of(distance);
    }

    private void swap(List<String> nodes, int i, int j) {
        String city = nodes.get(i);
        nodes.set(i, nodes.get(j));
        nodes.set(j, city);
    }

    private void print(List<String> nodes) {
        System.out.println(String.join(" -> ", nodes));
    }


    private record MinMax(long min, List<String> minRoute, long max, List<String> maxRoute) {
    }

}