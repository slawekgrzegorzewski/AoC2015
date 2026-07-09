package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.List;

public class Day11 {
    private final long finalDistance;
    private final long furthestDistance;

    public Day11() throws IOException {
        List<Direction> path = Input.day11();
        long maxDistance = 0;
        HexCoordinate start = new HexCoordinate(0, 0, 0);
        HexCoordinate current = start;
        for (Direction direction : path) {
            current = current.goToDirection(direction);
            maxDistance = Math.max(maxDistance, current.distance(start));
        }
        finalDistance = current.distance(start);
        furthestDistance = maxDistance;
    }

    long part1() {
        return finalDistance;
    }

    long part2() {
        return furthestDistance;
    }

    public record HexCoordinate(int q, int r, int s) {

        public long distance(HexCoordinate other) {
            return (Math.abs(q - other.q) + Math.abs(r - other.r) + Math.abs(s - other.s)) / 2;
        }

        public HexCoordinate goToDirection(Direction direction) {
            return switch (direction) {
                case N -> new HexCoordinate(q + 1, r, s - 1);
                case NE -> new HexCoordinate(q + 1, r - 1, s);
                case SE -> new HexCoordinate(q, r - 1, s + 1);
                case S -> new HexCoordinate(q - 1, r, s + 1);
                case SW -> new HexCoordinate(q - 1, r + 1, s);
                case NW -> new HexCoordinate(q, r + 1, s - 1);
            };
        }
    }

    public enum Direction {
        N("n"), NE("ne"), SE("se"), S("s"), SW("sw"), NW("nw");

        private final String name;

        Direction(String name) {
            this.name = name;
        }

        public static Direction fromString(String name) {
            for (Direction direction : values()) {
                if (direction.name.equals(name)) {
                    return direction;
                }
            }
            throw new IllegalArgumentException("Invalid direction: " + name);
        }
    }
}
