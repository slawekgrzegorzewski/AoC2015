package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.*;

import static com.adventofcode.y2018.Day22.Equipment.*;

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
        Coordinate3D start = new Coordinate3D(0, 0, Equipment.TORCH.ordinal());
        Coordinate3D goal = new Coordinate3D(cave.target.x, cave.target.y, TORCH.ordinal());

        int[][] erosionLevelMap = buildMaps(cave.depth);
        int[][][] distances = new int[cave.depth + 1][cave.depth + 1][3];
        for (int i = 0; i < cave.depth + 1; i++) {
            for (int j = 0; j < cave.depth + 1; j++) {
                Arrays.fill(distances[i][j], Integer.MAX_VALUE);
            }
        }
        start.setValue(distances, 0);
        int upperBound = estimateUpperBound(start, goal, erosionLevelMap);

        Set<Coordinate3D> visited = new HashSet<>();
        Comparator<Coordinate3D> dijkstraComparator = Comparator.comparingInt(node -> node.getValue(distances) + node.convertTo2D().manhattanDistance(goal.convertTo2D()));
        PriorityQueue<Coordinate3D> queue = new PriorityQueue<>(dijkstraComparator);
        Map<Coordinate3D, Integer> currentHashes = new HashMap<>();

        queue.add(start);
        currentHashes.put(start, System.identityHashCode(start));

        while (!queue.isEmpty()) {
            Coordinate3D current = queue.poll();
            if (currentHashes.getOrDefault(current, -1) != System.identityHashCode(current))
                continue;
            currentHashes.remove(current);
            visited.add(current);
            int currentDistance = current.getValue(distances);
            if (currentDistance > upperBound) {
                continue;
            }
            Coordinate coordinate = current.convertTo2D();
            for (Coordinate c : new Coordinate[]{coordinate, coordinate.up(), coordinate.down(), coordinate.left(), coordinate.right()}) {
                if (c.notInBounds(erosionLevelMap)) continue;
                Coordinate3D neighbor = getLegalMove(current, c, erosionLevelMap).orElse(null);
                if (neighbor == null) continue;
                if (visited.contains(neighbor)) continue;
                int neighborDistance = neighbor.getValue(distances);
                int moveCost = calculateMoveCost(current, neighbor);
                if (currentDistance + moveCost < neighborDistance) {
                    neighborDistance = currentDistance + moveCost;
                    neighbor.setValue(distances, neighborDistance);
                    if (neighbor.equals(goal) && neighborDistance < upperBound) {
                        upperBound = neighborDistance;
                    }
                }
                queue.add(neighbor);
                currentHashes.put(neighbor, System.identityHashCode(neighbor));
            }
        }
        return distances[cave.target.y()][cave.target.x()][TORCH.ordinal()];
    }

    private int estimateUpperBound(Coordinate3D start, Coordinate3D goal, int[][] erosionLevelMap) {
        int cost = 0;
        Coordinate3D current = start;
        while (current.y() != goal.y()) {
            Coordinate3D currentFinal = current;
            Coordinate3D next = getLegalMove(currentFinal, currentFinal.convertTo2D().down(), erosionLevelMap)
                    .orElseGet(() -> getLegalMove(currentFinal, currentFinal.convertTo2D(), erosionLevelMap).orElseThrow());
            cost += calculateMoveCost(current, next);
            current = next;
        }
        while (current.x() != goal.x()) {
            Coordinate3D currentFinal = current;
            Coordinate3D next = getLegalMove(currentFinal, currentFinal.convertTo2D().right(), erosionLevelMap)
                    .orElseGet(() -> getLegalMove(currentFinal, currentFinal.convertTo2D(), erosionLevelMap).orElseThrow());
            cost += calculateMoveCost(current, next);
            current = next;
        }
        return cost;
    }

    private int calculateMoveCost(Coordinate3D current, Coordinate3D neighbor) {
        if (current.equals2D(neighbor.convertTo2D())) {
            return 7;
        }
        return neighbor.z() == current.z() ? 1 : 8;
    }

    private Optional<Coordinate3D> getLegalMove(Coordinate3D current, Coordinate next, int[][] erosionLevelMap) {
        if (current.equals2D(next)) {
            return Arrays.stream(Equipment.values())
                    .filter(e -> e.ordinal() != current.z())
                    .filter(e -> isAllowed(current.convertTo2D().get(erosionLevelMap), e.ordinal()))
                    .map(e -> current.z(e.ordinal()))
                    .findFirst();
        }
        return isAllowed(next.get(erosionLevelMap), current.z()) ? Optional.of(new Coordinate3D(next.x(), next.y(), current.z())) : Optional.empty();
    }

    private int[][] buildMaps(int depth) {
        final int size = depth + 1;
        final int caveDepth = cave.depth;
        final int targetX = cave.target.x;
        final int targetY = cave.target.y;

        int[][] regionTypeMap = new int[size][size];
        int[] previousErosion = new int[size];
        int[] currentErosion = new int[size];

        for (int y = 0; y < size; y++) {
            int[] regionTypeRow = regionTypeMap[y];
            for (int x = 0; x < size; x++) {
                int geologicIndex;
                if (x == 0) {
                    geologicIndex = y * 48271;
                } else if (y == 0) {
                    geologicIndex = x * 16807;
                } else if (x == targetX && y == targetY) {
                    geologicIndex = 0;
                } else {
                    geologicIndex = currentErosion[x - 1] * previousErosion[x];
                }
                int erosionLevel = (geologicIndex + caveDepth) % 20183;
                currentErosion[x] = erosionLevel;
                regionTypeRow[x] = erosionLevel % 3;
            }
            int[] swap = previousErosion;
            previousErosion = currentErosion;
            currentErosion = swap;
        }
        return regionTypeMap;
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

        public boolean notInBounds(int[][] map) {
            return y < 0 || x < 0 || y >= map.length || x >= map[0].length;
        }

        public int get(int[][] map) {
            if (notInBounds(map))
                return -1;
            return map[y][x];
        }

        public int manhattanDistance(Coordinate coordinate) {
            return Math.abs(x - coordinate.x) + Math.abs(y - coordinate.y);
        }
    }

    public record Coordinate3D(int x, int y, int z) {

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

    private static boolean isAllowed(int regionType, int equipmentOrdinal) {
        return switch (regionType) {
            case 0 -> equipmentOrdinal != NEITHER.ordinal();
            case 1 -> equipmentOrdinal != TORCH.ordinal();
            case 2 -> equipmentOrdinal != CLIMBING_GEAR.ordinal();
            default -> throw new IllegalStateException("Unexpected region type: " + regionType);
        };
    }
}
