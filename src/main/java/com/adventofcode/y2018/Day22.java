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
        int[][] geologicIndexMap = new int[cave.depth][cave.depth];
        int[][] erosionLevelMap = new int[cave.depth][cave.depth];
        buildMaps(geologicIndexMap, erosionLevelMap, cave.target.x + cave.target.y);
        int riskLevel = 0;
        for (int x = 0; x <= cave.target.x; x++) {
            for (int y = 0; y <= cave.target.y; y++) {
                riskLevel += new Coordinate(x, y).get(erosionLevelMap) % 3;
            }
        }
        return riskLevel;
    }

    long part2() {
        int[][] geologicIndexMap = new int[cave.depth + 1][cave.depth + 1];
        int[][] erosionLevelMap = new int[cave.depth + 1][cave.depth + 1];
        buildMaps(geologicIndexMap, erosionLevelMap, cave.depth);
        System.out.println("Maps built");
        Set<Node> visited = new HashSet<>();
        Node start = new Node(new Coordinate(0, 0), Equipment.TORCH);
        start.setTime(0);
        List<Node> queue = new ArrayList<>();
        visited.add(start);
        queue.add(start);
//        Node targetNode = null;
        int minSeen = Integer.MAX_VALUE;
        while (!queue.isEmpty()) {
            Node current = queue.removeFirst();
            visited.add(current);
//            if (targetNode == null && current.coordinate.equals(cave.target)) {
//                targetNode = current;
//                minSeen = targetNode.time;
//                if(targetNode.equipment != Equipment.TORCH) minSeen += 7;
//                visited.clear();
//                queue.clear();
//                queue.add(start);
//                continue;
//            }
//            if (current.getTime() < (targetNode == null ? Integer.MAX_VALUE : minSeen)) {
            for (Coordinate neighbor : getNeighbors(current, geologicIndexMap, erosionLevelMap)) {
                if (!visited.contains(neighbor)) {
                    int minutesToMove = Objects.equals(current.equipment, neighbor.equipment) ? 1 : 7;
                    if (current.time + minutesToMove < neighbor.time) {
                        neighbor.setTime(current.time + minutesToMove);
                    }
                    if (!queue.contains(neighbor)) queue.add(neighbor);
                }
            }
//            }
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

    private List<Coordinate> getNeighbors(Coordinate current, int[][] geologicIndexMap, int[][] erosionLevelMap) {
        Stream<Coordinate> stream = null;
//        if (!targetFound) {
//            if (current.coordinate.x() == 0 && current.coordinate.y() < cave.target.y()) {
//                stream = Stream.of(current.coordinate.down());
//            }
//            if (current.coordinate.x() < cave.target.x() && current.coordinate.y() == cave.target.y()) {
//                stream = Stream.of(current.coordinate.right());
//            }
//        } else {
        stream = Stream.of(current.up(), current.down(), current.left(), current.right());
//        }
        return stream
                .filter(c -> new Coordinate(c.x, c.y).get(geologicIndexMap) != -1)
                .toList();
    }

    private void buildMaps(int[][] geologicIndexMap, int[][] erosionLevelMap, int diagonals) {
        for (int i = 0; i < geologicIndexMap.length; i++) {
            Arrays.fill(geologicIndexMap[i], -1);
            Arrays.fill(erosionLevelMap[i], -1);
        }
        geologicIndexMap[0][0] = 0;
        erosionLevelMap[0][0] = cave.depth % 20183;
        for (int sum = 1; sum <= diagonals; sum++) {
            for (int x = 0; x <= sum; x++) {
                int y = sum - x;
                Coordinate currentCoordinate = new Coordinate(x, y);
                if (x == 0) {
                    currentCoordinate.set(geologicIndexMap, y * 48271);
                } else if (y == 0) {
                    currentCoordinate.set(geologicIndexMap, x * 16807);
                } else if (x == cave.target.x && y == cave.target.y) {
                    currentCoordinate.set(geologicIndexMap, 0);
                } else {
                    currentCoordinate.set(geologicIndexMap,
                            erosionLevelMap[y][x - 1] * erosionLevelMap[y - 1][x]);
                }
                currentCoordinate.set(erosionLevelMap, (currentCoordinate.get(geologicIndexMap) + cave.depth) % 20183);
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

        public void set(int[][] map, int value) {
            map[y][x] = value;
        }

        public int get(int[][] map) {
            if (y < 0 || x < 0 || y >= map.length || x >= map[0].length)
                return -1;
            return map[y][x];
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
        private int torchTime;
        private int climbingGearTime;
        private int neitherTime;

        public Node() {
            torchTime = Integer.MAX_VALUE;
            climbingGearTime = Integer.MAX_VALUE;
            neitherTime = Integer.MAX_VALUE;
        }

        public void setTorchTime(int torchTime) {
            this.torchTime = torchTime;
        }

        public void setClimbingGearTime(int climbingGearTime) {
            this.climbingGearTime = climbingGearTime;
        }

        public void setNeitherTime(int neitherTime) {
            this.neitherTime = neitherTime;
        }
    }
}
