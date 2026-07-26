package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

import static com.adventofcode.y2018.Day22.Equipment.NEITHER;
import static com.adventofcode.y2018.Day22.Equipment.TORCH;

public class Day22 {
    private final Cave cave;


    public Day22() throws IOException {
        this.cave = Input.day22();
    }

    long part1() {
        int[][] erosionLevelMap = buildMaps(cave.depth);
        long riskLevel = 0;
        for (int x = 0; x <= cave.target.x; x++) {
            for (int y = 0; y <= cave.target.y; y++) {
                riskLevel += new Coordinate(x, y).get(erosionLevelMap);
            }
        }
        return riskLevel;
    }

    long part2() {
        int[][] erosionLevelMap = buildMaps(cave.depth);
//        print(erosionLevelMap);
        System.out.println("Maps built");
        int[][][] distances = new int[cave.depth + 1][cave.depth + 1][3];
        Coordinate3D[][][] previous = new Coordinate3D[cave.depth + 1][cave.depth + 1][3];
        for (int i = 0; i < cave.depth + 1; i++) {
            for (int j = 0; j < cave.depth + 1; j++) {
                Arrays.fill(distances[i][j], Integer.MAX_VALUE);
            }
        }
        Set<Coordinate3D> visited = new HashSet<>();
        PriorityQueue<Coordinate3D> queue = new PriorityQueue<>(Comparator.comparingLong(node -> node.getValue(distances)));
        Set<Coordinate3D> uniqueQueue = new HashSet<>();
        Coordinate3D start = new Coordinate3D(0, 0, Equipment.TORCH.ordinal());
        visited.add(start);
        queue.add(start);
        uniqueQueue.add(start);
        start.setValue(distances, 0);

        int actualMin = Integer.MAX_VALUE;
        boolean targetFound = false;
//        int actualMin = 1500;
//        boolean targetFound = true;
        int skipped = 0;

        while (!queue.isEmpty()) {
            if ((skipped + visited.size()) % 100_000 == 0) {
                System.out.println((skipped + visited.size()) + " processed.");
                System.out.println("Skipped = " + skipped + ", Visited = " + visited.size());
                System.out.println("queue.size() = " + queue.size());
            }
            Coordinate3D current = queue.poll();
            uniqueQueue.remove(current);
            visited.add(current);
            int currentDistance = current.getValue(distances);
            if (currentDistance > actualMin) {
                skipped++;
                continue;
            }
            for (Coordinate3D neighbor : getNeighbors(current, erosionLevelMap, targetFound)) {
                if (!visited.contains(neighbor)) {
                    int minutesToMove = current.z == neighbor.z ? 1 : 8;
                    int neighborDistance = neighbor.getValue(distances);
                    if (currentDistance + minutesToMove < neighborDistance) {
                        neighborDistance = currentDistance + minutesToMove;
                        neighbor.setValue(distances, neighborDistance);
                        previous[neighbor.y][neighbor.x][neighbor.z] = current;
                        queue.remove(neighbor);
                        uniqueQueue.remove(neighbor);
                    }
                    int finalStep = neighbor.z == TORCH.ordinal() ? 0 : 7;
                    if (neighbor.equals2D(cave.target) && (neighborDistance + finalStep) < actualMin) {
                        actualMin = neighborDistance + finalStep;
                        System.out.println("New min = " + actualMin);
                        if (!targetFound) {
                            visited.clear();
                            queue.clear();
                            uniqueQueue.clear();
                            queue.add(new Coordinate3D(0, 0, Equipment.TORCH.ordinal()));
                            uniqueQueue.add(new Coordinate3D(0, 0, Equipment.TORCH.ordinal()));
                            for (int i = 0; i < cave.depth + 1; i++) {
                                for (int j = 0; j < cave.depth + 1; j++) {
                                    Arrays.fill(distances[i][j], Integer.MAX_VALUE);
                                }
                            }
                            distances[0][0][TORCH.ordinal()] = 0;
                        }
                        targetFound = true;
                    }
                    if (neighbor.x == cave.target.x && neighbor.y == cave.target.y) {
                        System.out.println(Equipment.values()[neighbor.z]);
                        System.out.println(neighborDistance);
                    }
                    if (!uniqueQueue.contains(neighbor)) {
                        queue.add(neighbor);
                        uniqueQueue.add(neighbor);
                    }
                }
            }
//            queue.sort(Comparator.comparingLong(node -> node.getValue(distances)));
        }
        System.out.println((skipped + visited.size()) + " processed.");
        System.out.println("Skipped = " + skipped + ", Visited = " + visited.size());
        int size = 0;
        for (int[] longs : erosionLevelMap) {
            size = (int) (size + Arrays.stream(longs).filter(l -> l != -1).count());
        }
//        System.out.println("Total size = " + size * 3);
        List<Coordinate3D> path = new ArrayList<>();
        Coordinate3D current = new Coordinate3D(cave.target.x, cave.target.y, TORCH.ordinal());
        while (!current.equals(start)) {
            path.add(current);
            current = previous[current.y][current.x][current.z];
        }
        path.add(current);
        path = path.reversed();
        int changes = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            if(path.get(i).z != path.get(i + 1).z) {
                changes++;
            }
        }
        System.out.println("changes = " + changes);
//        IntSummaryStatistics xStats = path.stream().mapToInt(c -> c.x).summaryStatistics();
//        IntSummaryStatistics yStats = path.stream().mapToInt(c -> c.y).summaryStatistics();
//        System.out.println("xStats = " + xStats);
//        System.out.println("yStats = " + yStats);
//        int cost = 0;
//        Equipment lastEquipment = TORCH;
//        for (int i = path.size() - 1; i >= 0; i--) {
//            Coordinate3D c = path.get(i);
//            if (i != path.size() - 1) {
//                int lastCost = cost;
//                cost += 1 + (lastEquipment != Equipment.values()[c.z] ? 7 : 0);
//                if (cost <= lastCost) throw new IllegalStateException("cost = " + cost + " lastCost = " + lastCost);
//                lastEquipment = Equipment.values()[c.z];
//            }
//            System.out.println("(" + c.x + ":" + c.y + ":" + Equipment.values()[c.z] + ")" + " = " + regionKind(c.convertTo2D().get(erosionLevelMap)));
//            System.out.println("cost = " + cost);
//        }
//        System.out.println();
//        print(erosionLevelMap, xStats.getMax(), yStats.getMax(), List.of());
//        System.out.println();
//        print(erosionLevelMap, xStats.getMax(), yStats.getMax(), path.reversed().stream().map(Coordinate3D::convertTo2D).toList());
//        System.out.println();
//        print(distances);
        print(erosionLevelMap, 15, 15 ,List.of());
        return distances[cave.target.y()][cave.target.x()][TORCH.ordinal()];
    }

    private List<Coordinate3D> getNeighbors(Coordinate3D current, int[][] erosionLevelMap, boolean targetFound) {
        Stream<Coordinate3D> stream = null;
        if (!targetFound) {
            if (current.x() == 0 && current.y() < cave.target.y()) {
                stream = Stream.of(current.down());
            }
            if (current.x() < cave.target.x() && current.y() == cave.target.y()) {
                stream = Stream.of(current.right());
            }
        } else {
            stream = Stream.of(current.up(), current.down(), current.left(), current.right());
        }
        return stream
                .filter(c -> new Coordinate(c.x, c.y).inBounds(erosionLevelMap))
                .flatMap(coordinate -> switch (((int) erosionLevelMap[coordinate.y][coordinate.x])) {
                    case 0 ->
                            Stream.of(coordinate.z(Equipment.CLIMBING_GEAR.ordinal()), coordinate.z(Equipment.TORCH.ordinal()));//ROCKY
                    case 1 ->
                            Stream.of((coordinate.z(NEITHER.ordinal())), coordinate.z(Equipment.CLIMBING_GEAR.ordinal()));//WET
                    case 2 ->
                            Stream.of((coordinate.z(NEITHER.ordinal())), coordinate.z(Equipment.TORCH.ordinal()));//NARROW
                    default ->
                            throw new IllegalStateException("Unexpected value: " + erosionLevelMap[coordinate.y][coordinate.x]);
                })
                .toList();
    }

    private void print(int[][][] map) {
        Path path = Path.of("distances.txt");
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("writing to a file");
        for (int[][] row : map) {
            StringBuilder sb = new StringBuilder();
            for (int[] ints : row) {
                int value = ints[TORCH.ordinal()];
                if (value == Integer.MAX_VALUE) {
                    sb.append(" X  ");
                } else {
                    sb.append(String.format("%4d", value));
                }
                sb.append(" ");
            }
//            System.out.println(sb);
            try {
                Files.write(path, sb.append("\n").toString().getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void print(int[][] map, int xMax, int yMax, List<Coordinate> visited) {
        if (xMax == -1) xMax = map[0].length - 1;
        if (yMax == -1) yMax = map.length - 1;

        for (int y = 0; y < Math.min(map.length, yMax + 1); y++) {
            int[] row = map[y];
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < Math.min(row.length, xMax + 1); x++) {
                if (row[x] == -1) continue;
                if (visited.contains(new Coordinate(x, y))) {
                    sb.append("X");
                } else {
                    if ((x != 0 || y != 0) && (x != cave.target.x || y != cave.target.y)) {
                        sb.append(regionKind(row[x]));
                    } else if (x == 0 && y == 0) {
                        sb.append("M");
                    } else {
                        sb.append("T");
                    }
                }
            }
            System.out.println(sb);
        }
    }

    private static @NonNull String regionKind(long row) {
        return switch (((int) row)) {
            case 0 -> ".";
            case 1 -> "=";
            case 2 -> "|";
            default -> throw new IllegalStateException("Unexpected value: " + row);
        };
    }

    private int[][] buildMaps(int depth) {
        int[][] geologicIndexMap = new int[cave.depth + 1][cave.depth + 1];
        int[][] erosionLevelMap = new int[cave.depth + 1][cave.depth + 1];
        geologicIndexMap[0][0] = 0;
        erosionLevelMap[0][0] = cave.depth % 20183;
        for (int y = 0; y <= depth; y++) {
            for (int x = 0; x <= depth; x++) {
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
        for (int y = 0; y <= depth; y++) {
            for (int x = 0; x <= depth; x++) {
                Coordinate c = new Coordinate(x, y);
                c.set(erosionLevelMap, c.get(erosionLevelMap) % 3);
            }
        }
        return erosionLevelMap;
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

        public boolean inBounds(int[][] map) {
            return y >= 0 && x >= 0 && y < map.length && x < map[0].length;
        }

        public int get(int[][] map) {
            if (!inBounds(map))
                return -1;
            return map[y][x];
        }
    }

    public record Coordinate3D(int x, int y, int z) {

        public Coordinate3D up() {
            return new Coordinate3D(x, y - 1, z);
        }

        public Coordinate3D down() {
            return new Coordinate3D(x, y + 1, z);
        }

        public Coordinate3D left() {
            return new Coordinate3D(x - 1, y, z);
        }

        public Coordinate3D right() {
            return new Coordinate3D(x + 1, y, z);
        }

        public Coordinate convertTo2D() {
            return new Coordinate(x, y);
        }

        public int getValue(int[][][] map) {
            return map[y][x][z];
        }

        public void setValue(int[][][] map, int value) {
            map[y][x][z] = value;
        }

        public Coordinate3D z(int z) {
            return new Coordinate3D(x, y, z);
        }

        public boolean equals2D(Coordinate other) {
            return x == other.x && y == other.y;
        }
    }

    public record Cave(Coordinate target, int depth) {
        public static Cave parse(List<String> value) {
            String[] parts = value.getLast().replace("target: ", "").split(",");
            return new Cave(new Coordinate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])), Integer.parseInt(value.getFirst().replace("depth: ", "")));
        }
    }

    public enum Equipment {
        TORCH, CLIMBING_GEAR, NEITHER
    }
}
