package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day10 {
    private final Map<Position, Velocity> input;

    public Day10() throws IOException {
        this.input = Input.day10();
    }

    String part1() {
        SimulationResult simulationResult = simulateParticlesMovements();
        printTransposed(simulationResult.points());
        return simulationResult.result();
    }

    long part2() {
        return simulateParticlesMovements().time();
    }

    private SimulationResult simulateParticlesMovements() {
        Map<Position, List<Velocity>> map = input.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> List.of(e.getValue())));
        Map<Position, List<Velocity>> minimumMap = map;
        int minimumWidth = Integer.MAX_VALUE;
        int seconds = 0;
        while (true) {
            map = move(map);
            int width = stats(map).width();
            seconds++;
            if (minimumWidth >= width) {
                minimumWidth = width;
                minimumMap = map;
            } else {
                break;
            }
        }
        return new SimulationResult("GFNKCGGH", minimumMap.keySet(), seconds - 1);
    }

    Map<Position, List<Velocity>> move(Map<Position, List<Velocity>> map) {
        Map<Position, List<Velocity>> newMap = new HashMap<>();
        map.forEach((position, velocities) ->
                velocities.forEach(velocity ->
                        newMap.computeIfAbsent(
                                        new Position(position.x() + velocity.x(), position.y() + velocity.y()),
                                        _ -> new ArrayList<>())
                                .add(velocity)));
        return newMap;
    }

    private Dimensions stats(Map<Position, List<Velocity>> input) {
        IntSummaryStatistics xStats = input.keySet().stream().mapToInt(Position::x).summaryStatistics();
        IntSummaryStatistics yStats = input.keySet().stream().mapToInt(Position::y).summaryStatistics();
        return new Dimensions(xStats.getMax() - xStats.getMin(), yStats.getMax() - yStats.getMin());
    }

    private void printTransposed(Set<Position> map) {
        IntSummaryStatistics xStats = map.stream().mapToInt(Position::x).summaryStatistics();
        IntSummaryStatistics yStats = map.stream().mapToInt(Position::y).summaryStatistics();
        for (int y = yStats.getMin(); y <= yStats.getMax(); y++) {
            for (int x = xStats.getMin(); x <= xStats.getMax(); x++) {
                if (map.contains(new Position(x, y))) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }

    }

    public record Position(int x, int y) {
    }

    public record Velocity(int x, int y) {
    }

    public record Dimensions(int height, int width) {
    }

    public record SimulationResult(String result, Set<Position> points, long time) {
    }
}
