package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class Day1 {
    private final List<String> input;

    private static enum Direction {
        NORTH, SOUTH, EAST, WEST;

        public Direction left() {
            return switch (this) {
                case NORTH -> WEST;
                case WEST -> SOUTH;
                case SOUTH -> EAST;
                case EAST -> NORTH;
            };
        }

        public Direction right() {
            return switch (this) {
                case NORTH -> EAST;
                case EAST -> SOUTH;
                case SOUTH -> WEST;
                case WEST -> NORTH;
            };
        }
    }

    public Day1() throws IOException {
        this.input = Input.day1();
    }

    long part1() {
        return task(1);
    }

    long part2() {
        return task(2);
    }

    private long task(int part) {
        int x = 0, y = 0;
        Direction direction = Direction.NORTH;
        Set<Position> visited = new HashSet<>();
        visited.add(new Position(x, y));
        for (String command : input) {
            direction = command.startsWith("R") ? direction.right() : direction.left();
            int moves = Integer.parseInt(command.substring(1));
            int initialX = x, initialY = y;
            switch (direction) {
                case NORTH -> y += moves;
                case SOUTH -> y -= moves;
                case EAST -> x += moves;
                case WEST -> x -= moves;
            }
            Optional<Long> resultForPartTwo = addAndCheckIntermediatePoints(visited, initialX, initialY, x, y)
                    .stream()
                    .filter(value -> part == 2)
                    .boxed()
                    .findAny();
            if (resultForPartTwo.isPresent())
                return resultForPartTwo.orElseThrow();
        }
        return Math.abs(x) + Math.abs(y);
    }

    private static OptionalLong addAndCheckIntermediatePoints(Set<Position> visited, int fromX, int fromY, int toX, int toY) {
        Function<Integer, Position> newPositionSupplier;
        int from, to;
        if (fromX != toX) {
            newPositionSupplier = x -> new Position(x, fromY);
            from = fromX + (fromX < toX ? 1 : -1);
            to = toX;
        } else {
            newPositionSupplier = y -> new Position(fromX, y);
            from = fromY + (fromY < toY ? 1 : -1);
            to = toY;
        }
        boolean increment = from < to;
        for (int i = from; (increment && i <= to) || (!increment && i >= to); i += increment ? 1 : -1) {
            Position newPosition = newPositionSupplier.apply(i);
            if (!visited.contains(newPosition)) {
                visited.add(newPosition);
            } else {
                return OptionalLong.of(Math.abs(newPosition.x) + Math.abs(newPosition.y));
            }
        }
        return OptionalLong.empty();
    }

    private record Position(int x, int y) {
    }
}
