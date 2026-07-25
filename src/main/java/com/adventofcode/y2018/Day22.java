package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class Day22 {
    private final Cave cave;


    public Day22() throws IOException {
        this.cave = Input.day22();
    }

    long part1() {
        Map<Coordinate, Integer> geologicIndexMap = new HashMap<>();
        Map<Coordinate, Integer> erosionLevelMap = new HashMap<>();
        buildMaps(geologicIndexMap, erosionLevelMap, cave.target.x + cave.target.y);
        int riskLevel = 0;
        for (int x = 0; x <= cave.target.x; x++) {
            for (int y = 0; y <= cave.target.y; y++) {
                riskLevel += erosionLevelMap.get(new Coordinate(x, y)) % 3;
            }
        }
        return riskLevel;
    }

    long part2() {
        Map<Coordinate, Integer> geologicIndexMap = new HashMap<>();
        Map<Coordinate, Integer> erosionLevelMap = new HashMap<>();
        buildMaps(geologicIndexMap, erosionLevelMap, cave.depth());
        Set<Node> visited = new HashSet<>();
        Node start = new Node(new Coordinate(0, 0), Equipment.TORCH);
        start.setTime(0);
        List<Node> queue = new ArrayList<>();
        visited.add(start);
        queue.add(start);
        while (!queue.isEmpty()) {
            Node current = queue.removeFirst();
            visited.add(current);
            if (current.getTime() < 7 * (cave.target.x + cave.target.y)) {
                for (Node neighbor : getNeighbors(current, geologicIndexMap, erosionLevelMap)) {
                    if (!visited.contains(neighbor)) {
                        int minutesToMove = Objects.equals(current.equipment, neighbor.equipment) ? 1 : 7;
                        if (current.time + minutesToMove < neighbor.time) {
                            neighbor.time = current.time + minutesToMove;
                        }
                        if (!queue.contains(neighbor)) queue.add(neighbor);
                    }
                }
            }
            queue.sort(Comparator.comparingLong(node -> node.time));
        }
        return visited.stream()
                .filter(node -> node.coordinate.equals(cave.target))
                .mapToLong(node -> node.time)
                .min()
                .orElse(0L);
        //1017 too high
        //1021 too high
    }

    private List<Node> getNeighbors(Node current, Map<Coordinate, Integer> geologicIndexMap, Map<Coordinate, Integer> erosionLevelMap) {
        return Stream.of(current.coordinate.up(), current.coordinate.down(), current.coordinate.left(), current.coordinate.right())
                .filter(geologicIndexMap::containsKey)
                .flatMap(coordinate -> switch (erosionLevelMap.get(coordinate) % 3) {
                    case 0 ->
                            Stream.of(new Node(coordinate, Equipment.CLIMBING_GEAR), new Node(coordinate, Equipment.TORCH));//ROCKY
                    case 1 -> Stream.of(new Node(coordinate, null), new Node(coordinate, Equipment.CLIMBING_GEAR));//WET
                    case 2 -> Stream.of(new Node(coordinate, null), new Node(coordinate, Equipment.TORCH));//NARROW
                    default ->
                            throw new IllegalStateException("Unexpected value: " + erosionLevelMap.get(coordinate) % 3);
                })
                .toList();
    }

    private void buildMaps(Map<Coordinate, Integer> geologicIndexMap, Map<Coordinate, Integer> erosionLevelMap, int diagonals) {
        geologicIndexMap.put(new Coordinate(0, 0), 0);
        erosionLevelMap.put(new Coordinate(0, 0), cave.depth % 20183);
        for (int sum = 1; sum <= diagonals; sum++) {
            for (int x = 0; x <= sum; x++) {
                int y = sum - x;
                Coordinate currentCoordinate = new Coordinate(x, y);
                if (x == 0) {
                    geologicIndexMap.put(currentCoordinate, y * 48271);
                } else if (y == 0) {
                    geologicIndexMap.put(currentCoordinate, x * 16807);
                } else if (x == cave.target.x && y == cave.target.y) {
                    geologicIndexMap.put(currentCoordinate, 0);
                } else {
                    geologicIndexMap.put(currentCoordinate,
                            erosionLevelMap.get(new Coordinate(x - 1, y)) * erosionLevelMap.get(new Coordinate(x, y - 1)));
                }
                erosionLevelMap.put(currentCoordinate, (geologicIndexMap.get(currentCoordinate) + cave.depth) % 20183);
            }
        }
    }

    public record Coordinate(int x, int y) {
        public Coordinate up() {
            return new Coordinate(x, y - 1);
        }

        public Coordinate down() {
            return new Coordinate(x, y + 1);
        }

        public Coordinate left() {
            return new Coordinate(x - 1, y);
        }

        public Coordinate right() {
            return new Coordinate(x + 1, y);
        }
    }

    public record Cave(Coordinate target, int depth) {
        public static Cave parse(List<String> value) {
            String[] parts = value.getLast().replace("target: ", "").split(",");
            return new Cave(new Coordinate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])), Integer.parseInt(value.getFirst().replace("depth: ", "")));
        }
    }

    public enum Equipment {
        TORCH, CLIMBING_GEAR
    }

    public static class Node {
        private final Coordinate coordinate;
        private final Equipment equipment;
        private int time;

        public Node(Coordinate coordinate, Equipment equipment) {
            this.coordinate = coordinate;
            this.equipment = equipment;
            time = Integer.MAX_VALUE;
        }

        public int getTime() {
            return time;
        }

        public Node setTime(int time) {
            this.time = time;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(coordinate, node.coordinate) && equipment == node.equipment;
        }

        @Override
        public int hashCode() {
            return Objects.hash(coordinate, equipment);
        }
    }
}
