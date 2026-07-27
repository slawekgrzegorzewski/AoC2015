package com.adventofcode.y2018;

import com.adventofcode.Utils;
import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.adventofcode.y2018.Day22.Equipment.TORCH;

public class Day22 {

    private static final int REGION_KINDS = 3;
    private static final int TOOLS = 3;
    private static final int MOVE_COST = 1;
    private static final int SWITCH_COST = 7;

    private final Cave cave;
    private final byte[] regionTypes;
    private final int width;
    private final int height;

    public Day22() throws IOException {
        this.cave = Input.day22();
        int targetX = cave.target().x();
        int targetY = cave.target().y();
        byte[] probe = buildRegionTypes(targetX + 1, targetY + 1);
        int upperBound = estimateUpperBound(probe, targetX + 1);
        this.width = Math.max(targetX, (upperBound + targetX - targetY) / 2) + 1;
        this.height = Math.max(targetY, (upperBound + targetY - targetX) / 2) + 1;
        this.regionTypes = buildRegionTypes(width, height);
    }

    long part1() {
        long riskLevel = 0;
        for (int y = 0; y <= cave.target().y(); y++) {
            int rowOffset = y * width;
            for (int x = 0; x <= cave.target().x(); x++) {
                riskLevel += regionTypes[rowOffset + x];
            }
        }
        return riskLevel;
    }

    long part2() {
        final int torch = TORCH.ordinal();
        final int goal = stateOf(cave.target().x(), cave.target().y(), torch);

        int[] dist = new int[width * height * TOOLS];
        Arrays.fill(dist, Integer.MAX_VALUE);

        Utils.MinHeap heap = new Utils.MinHeap();
        int start = stateOf(0, 0, torch);
        dist[start] = 0;
        long value = ((long) 0 << 32) | (start & 0xFFFFFFFFL);
        heap.push(value);
        while (!heap.isEmpty()) {
            long entry = heap.pop();
            int distance = (int) (entry >>> 32);
            int state = (int) entry;

            if (distance != dist[state]) continue;
            if (state == goal) return distance;

            int tool = state % TOOLS;
            int cell = state / TOOLS;
            int x = cell % width;
            int y = cell / width;
            int regionType = regionTypes[cell];

            addNeighbour(dist, heap, cell * TOOLS + otherTool(regionType, tool), distance + SWITCH_COST);

            int moved = distance + MOVE_COST;
            if (x > 0) addNeighbourIfAllowed(dist, heap, cell - 1, tool, moved);
            if (x < width - 1) addNeighbourIfAllowed(dist, heap, cell + 1, tool, moved);
            if (y > 0) addNeighbourIfAllowed(dist, heap, cell - width, tool, moved);
            if (y < height - 1) addNeighbourIfAllowed(dist, heap, cell + width, tool, moved);
        }
        throw new IllegalStateException("Target is unreachable");
    }

    private void addNeighbourIfAllowed(int[] dist, Utils.MinHeap heap, int cell, int tool, int candidate) {
        if (isAllowed(regionTypes[cell], tool)) {
            addNeighbour(dist, heap, cell * TOOLS + tool, candidate);
        }
    }

    private static void addNeighbour(int[] dist, Utils.MinHeap heap, int state, int candidate) {
        if (candidate < dist[state]) {
            dist[state] = candidate;
            long value = ((long) candidate << 32) | (state & 0xFFFFFFFFL);
            heap.push(value);
        }
    }

    private int stateOf(int x, int y, int tool) {
        return (y * width + x) * TOOLS + tool;
    }

    private int estimateUpperBound(byte[] map, int probeWidth) {
        int targetX = cave.target().x();
        int targetY = cave.target().y();
        int x = 0;
        int y = 0;
        int tool = TORCH.ordinal();
        int cost = 0;

        while (y != targetY) {
            if (!isAllowed(map[(y + 1) * probeWidth + x], tool)) {
                tool = otherTool(map[y * probeWidth + x], tool);
                cost += SWITCH_COST;
            }
            y++;
            cost += MOVE_COST;
        }
        while (x != targetX) {
            if (!isAllowed(map[y * probeWidth + x + 1], tool)) {
                tool = otherTool(map[y * probeWidth + x], tool);
                cost += SWITCH_COST;
            }
            x++;
            cost += MOVE_COST;
        }
        if (tool != TORCH.ordinal()) cost += SWITCH_COST;
        return cost;
    }

    private byte[] buildRegionTypes(int width, int height) {
        byte[] types = new byte[width * height];
        int depth = cave.depth();
        int targetX = cave.target().x();
        int targetY = cave.target().y();

        int[] previousErosion = new int[width];
        int[] currentErosion = new int[width];

        for (int y = 0; y < height; y++) {
            int rowOffset = y * width;
            for (int x = 0; x < width; x++) {
                int geologicIndex;
                if (x == targetX && y == targetY) {
                    geologicIndex = 0;
                } else if (x == 0) {
                    geologicIndex = y * 48271;
                } else if (y == 0) {
                    geologicIndex = x * 16807;
                } else {
                    geologicIndex = currentErosion[x - 1] * previousErosion[x];
                }
                int erosionLevel = (geologicIndex + depth) % 20183;
                currentErosion[x] = erosionLevel;
                types[rowOffset + x] = (byte) (erosionLevel % REGION_KINDS);
            }
            int[] swap = previousErosion;
            previousErosion = currentErosion;
            currentErosion = swap;
        }
        return types;
    }

    private static int forbiddenTool(int regionType) {
        return (regionType + 2) % REGION_KINDS;
    }

    private static boolean isAllowed(int regionType, int tool) {
        return tool != forbiddenTool(regionType);
    }

    private static int otherTool(int regionType, int tool) {
        return TOOLS - tool - forbiddenTool(regionType);
    }

    public record Coordinate(int x, int y) {
    }

    public record Cave(Coordinate target, int depth) {
        public static Cave parse(List<String> value) {
            String[] parts = value.getLast().replace("target: ", "").split(",");
            return new Cave(
                    new Coordinate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])),
                    Integer.parseInt(value.getFirst().replace("depth: ", "")));
        }
    }

    public enum Equipment {
        TORCH, CLIMBING_GEAR, NEITHER
    }
}