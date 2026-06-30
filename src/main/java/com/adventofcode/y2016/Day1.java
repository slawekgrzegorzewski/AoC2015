package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class Day1 {
    private final List<String> input;

    private enum Direction {
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
        return task(false);
    }

    long part2() {
        return task(true);
    }

    private long task(boolean part2) {
        Position position = new Position(0, 0, Direction.NORTH);
        Set<Position> visited = null;
        if (part2) {
            visited = new HashSet<>();
            visited.add(position);
        }
        for (String command : input) {
            int moves = Integer.parseInt(command.substring(1));
            List<Position> path = position.moveAndGetPath(command.substring(0, 1), moves);
            position = path.getLast();
            if (part2) {
                for (Position pathPoint : path) {
                    if (!visited.contains(pathPoint)) {
                        visited.add(pathPoint);
                    } else {
                        return pathPoint.manhattanDistance();
                    }
                }
            }
        }
        return position.manhattanDistance();
    }

    private record Position(int x, int y, Direction direction) {
        public List<Position> moveAndGetPath(String direction, int distance) {
            int newX = x, newY = y;
            Direction newDirection = direction.equals("R")
                    ? this.direction.right()
                    : this.direction.left();
            Function<Integer, Position> newPositionSupplier = null;
            int iterateFrom = 0, iterateTo = 0;
            switch (newDirection) {
                case NORTH -> {
                    newY += distance;
                    newPositionSupplier = y -> new Position(this.x(), y, newDirection);
                    iterateFrom = this.y() + 1;
                    iterateTo = newY;
                }
                case SOUTH -> {
                    newY -= distance;
                    newPositionSupplier = y -> new Position(this.x(), y, newDirection);
                    iterateFrom = this.y() - 1;
                    iterateTo = newY;
                }
                case EAST -> {
                    newX += distance;
                    newPositionSupplier = x -> new Position(x, this.y(), newDirection);
                    iterateFrom = this.x() + 1;
                    iterateTo = newX;
                }
                case WEST -> {
                    newX -= distance;
                    newPositionSupplier = x -> new Position(x, this.y(), newDirection);
                    iterateFrom = this.x() - 1;
                    iterateTo = newX;
                }
            }
            List<Position> path = new ArrayList<>();
            boolean increment = iterateFrom < iterateTo;
            for (int i = iterateFrom; (increment && i <= iterateTo) || (!increment && i >= iterateTo); i += increment ? 1 : -1) {
                path.add(newPositionSupplier.apply(i));
            }
            return path;
        }

        public long manhattanDistance() {
            return Math.abs(x) + Math.abs(y);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return x == position.x && y == position.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
