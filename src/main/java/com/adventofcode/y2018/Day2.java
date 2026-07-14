package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.*;

public class Day2 {
    private final IdsStats idsStats;


    public Day2() throws IOException {
        idsStats = idsStats(Input.day2());
    }

    long part1() {
        return (long) idsStats.countOfTwo() * idsStats.countOfThree();
    }

    String part2() {
        return findFabricBoxId(idsStats)
                .orElseThrow(() -> new IllegalStateException("No matching IDs found"));
    }

    private static Optional<String> findFabricBoxId(IdsStats idsStats) {
        for (int i = 0; i < idsStats.validIds().size(); i++) {
            second:
            for (int j = i + 1; j < idsStats.validIds().size(); j++) {
                char[] id1 = idsStats.validIds().get(i).toCharArray();
                char[] id2 = idsStats.validIds().get(j).toCharArray();
                int diffIndex = -1;
                for (int k = 0; k < id1.length; k++) {
                    if (id1[k] != id2[k]) {
                        if (diffIndex != -1) continue second;
                        diffIndex = k;
                    }
                }
                if (diffIndex > -1)
                    return Optional.of(idsStats.validIds().get(i).substring(0, diffIndex) + idsStats.validIds().get(j).substring(diffIndex + 1));
            }
        }
        return Optional.empty();
    }

    private static IdsStats idsStats(List<String> input) {
        List<String> validIds = new ArrayList<>();
        int countOfTwo = 0;
        int countOfThree = 0;
        for (String string : input) {
            Map<Integer, List<Character>> charsStats = stringCharsStats(string);
            if (!charsStats.getOrDefault(2, List.of()).isEmpty()) {
                countOfTwo++;
                validIds.add(string);
            }
            if (!charsStats.getOrDefault(3, List.of()).isEmpty()) {
                countOfThree++;
                validIds.add(string);
            }
        }
        return new IdsStats(validIds, countOfTwo, countOfThree);
    }

    private static Map<Integer, List<Character>> stringCharsStats(String string) {
        Map<Character, Integer> stats = new HashMap<>();
        for (char c : string.toCharArray()) {
            stats.compute(c, (_, v) -> v == null ? 1 : v + 1);
        }
        Map<Integer, List<Character>> result = new HashMap<>();
        stats.forEach((k, v) -> result.computeIfAbsent(v, _ -> new ArrayList<>()).add(k));
        return result;
    }

    private record IdsStats(List<String> validIds, int countOfTwo, int countOfThree) {
    }
}
