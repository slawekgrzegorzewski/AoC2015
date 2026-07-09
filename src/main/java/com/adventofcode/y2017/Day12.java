package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Day12 {
    private final Map<Integer, List<Integer>> programsWithConnections;


    public Day12() throws IOException {
        this.programsWithConnections = Input.day12();
    }

    long part1() {
        return discoverAllProgramsAccessibleFrom(0).size();
    }

    long part2() {
        List<Set<Integer>> groups = new ArrayList<>();
        List<Integer> allPrograms = new ArrayList<>(programsWithConnections.keySet());
        while (!allPrograms.isEmpty()) {
            Set<Integer> group = discoverAllProgramsAccessibleFrom(allPrograms.removeFirst());
            groups.add(group);
            allPrograms.removeAll(group);
        }
        return groups.size();
    }

    private @NonNull Set<Integer> discoverAllProgramsAccessibleFrom(int startProgramId) {
        Set<Integer> group = new HashSet<>();
        group.add(startProgramId);
        List<Integer> toDiscover = new ArrayList<>(programsWithConnections.get(startProgramId));
        while (!toDiscover.isEmpty()) {
            int program = toDiscover.removeFirst();
            group.add(program);
            programsWithConnections.get(program)
                    .stream()
                    .filter(Predicate.not(group::contains))
                    .collect(Collectors.toCollection(() -> toDiscover));
        }
        return group;
    }
}
