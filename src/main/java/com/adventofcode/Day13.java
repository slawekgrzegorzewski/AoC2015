package com.adventofcode;

import com.adventofcode.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class Day13 {

    private final Map<Input.StringPair, Integer> input;
    private final List<String> persons;

    public Day13() throws IOException {
        input = Input.day13();
        persons = input.keySet()
                .stream()
                .flatMap(stringPair -> Stream.of(stringPair.first(), stringPair.second()))
                .distinct()
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    long part1() {
        return findBestHappinessArrangement();
    }

    long part2() {
        modifyInput();
        return findBestHappinessArrangement();
    }

    private long findBestHappinessArrangement() {
        MinMax optimumArrangements = findOptimumArrangements();
        System.out.println("optimumArrangements.min() = " + optimumArrangements.min());
        print(optimumArrangements.minArrangement());
        System.out.println("optimumArrangements.max() = " + optimumArrangements.max());
        print(optimumArrangements.maxArrangement());
        return optimumArrangements.max();
    }

    private void modifyInput() {
        persons.forEach(person -> {
            input.put(new Input.StringPair(person, "me"), 0);
            input.put(new Input.StringPair("me", person), 0);
        });
        persons.add("me");
    }

    private MinMax findOptimumArrangements() {
        int i = 0;
        int size = persons.size();
        int[] indexes = new int[size];
        long minHappinessSeen = Long.MAX_VALUE;
        List<String> minArrangement = Collections.emptyList();
        long maxHappinessSeen = Long.MIN_VALUE;
        List<String> maxArrangement = Collections.emptyList();
        while (i < size) {
            if (indexes[i] < i) {
                swap(persons, i % 2 == 0 ? 0 : indexes[i], i);
                indexes[i]++;
                i = 0;
                OptionalLong happiness = calculateHappiness(persons, input);
                if (happiness.isPresent() && happiness.getAsLong() < minHappinessSeen) {
                    minHappinessSeen = happiness.getAsLong();
                    minArrangement = new ArrayList<>(persons);
                }
                if (happiness.isPresent() && happiness.getAsLong() > maxHappinessSeen) {
                    maxHappinessSeen = happiness.getAsLong();
                    maxArrangement = new ArrayList<>(persons);
                }
            } else {
                indexes[i] = 0;
                i++;
            }
        }
        return new MinMax(minHappinessSeen, minArrangement, maxHappinessSeen, maxArrangement);
    }

    private OptionalLong calculateHappiness(List<String> persons, Map<Input.StringPair, Integer> input) {
        long happiness = 0L;
        for (int i = 0; i < persons.size(); i++) {
            Input.StringPair personPair = new Input.StringPair(
                    persons.get(i),
                    persons.get((i + 1) % persons.size()));
            if (!input.containsKey(personPair)) return OptionalLong.empty();
            happiness += input.get(personPair);
        }
        for (int i = persons.size() - 1; i >= 0; i--) {
            Input.StringPair personPair = new Input.StringPair(
                    persons.get(i),
                    i == 0 ? persons.getLast() : persons.get(i - 1));
            if (!input.containsKey(personPair)) return OptionalLong.empty();
            happiness += input.get(personPair);
        }
        return OptionalLong.of(happiness);
    }

    private void swap(List<String> nodes, int i, int j) {
        String city = nodes.get(i);
        nodes.set(i, nodes.get(j));
        nodes.set(j, city);
    }

    private void print(List<String> nodes) {
        System.out.println(String.join(" -> ", nodes));
    }

    private record MinMax(long min, List<String> minArrangement, long max, List<String> maxArrangement) {
    }
}