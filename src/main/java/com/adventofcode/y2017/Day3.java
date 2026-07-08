package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Day3 {
    private final int squareToRead;
    private final static List<BiFunction<Integer, Coordinate, Boolean>> PREDICATES = List.of(
            (Integer l, Coordinate coordinate) -> coordinate.y > -l,
            (Integer l, Coordinate coordinate) -> coordinate.x > -l,
            (Integer l, Coordinate coordinate) -> coordinate.y < l,
            (Integer l, Coordinate coordinate) -> coordinate.x < l
    );
    private final static List<Function<Coordinate, Coordinate>> MOVERS = List.of(
            Coordinate::up,
            Coordinate::left,
            Coordinate::down,
            Coordinate::right
    );

    public Day3() throws IOException {
        this.squareToRead = Input.day3();
    }

    long part1() {
        return findCoordinates(squareToRead).manhattanDistance();
    }

    long part2() {
        int layer = 5;
        int[][] memory = new int[layer * 2 + 1][layer * 2 + 1];
        Coordinate currentCoordinate = new Coordinate(0, 0);
        setCell(memory, currentCoordinate.translateIntoArray(layer), 1);
        int layerToFill = 1;
        while (true) {
            currentCoordinate = currentCoordinate.right();
            int value = fillValue(memory, currentCoordinate, layer);
            if (value > squareToRead) {
                return value;
            }
            for (int i = 0; i < PREDICATES.size(); i++) {
                BiFunction<Integer, Coordinate, Boolean> predicate = PREDICATES.get(i);
                Function<Coordinate, Coordinate> mover = MOVERS.get(i);
                while (predicate.apply(layerToFill, currentCoordinate)) {
                    currentCoordinate = mover.apply(currentCoordinate);
                    value = fillValue(memory, currentCoordinate, layer);
                    if (value > squareToRead) {
                        return value;
                    }
                }
            }
            layerToFill++;
        }
    }

    private int fillValue(int[][] memory, Coordinate currentCoordinate, int layer) {
        return setCell(memory, currentCoordinate.translateIntoArray(layer), sumAdjacent(memory, currentCoordinate.translateIntoArray(layer)));
    }

    private int sumAdjacent(int[][] memory, Coordinate coordinate) {
        return memory[coordinate.y + 1][coordinate.x - 1]
                + memory[coordinate.y + 1][coordinate.x]
                + memory[coordinate.y + 1][coordinate.x + 1]
                + memory[coordinate.y][coordinate.x - 1]
                + memory[coordinate.y][coordinate.x + 1]
                + memory[coordinate.y - 1][coordinate.x - 1]
                + memory[coordinate.y - 1][coordinate.x]
                + memory[coordinate.y - 1][coordinate.x + 1];
    }

    private int setCell(int[][] memory, Coordinate coordinate, int i) {
        memory[coordinate.y][coordinate.x] = i;
        return i;
    }

    private Coordinate findCoordinates(int squareAddress) {
        int layer = 1;
        while (layer * layer < squareAddress)
            layer += 2;
        layer -= 2;
        int x = layer / 2, y = -x;
        int diff = squareAddress - layer * layer;
        layer += 2;
        int maxCoordinate = layer / 2;
        x++;
        diff--;
        if (maxCoordinate - y > diff) {
            return new Coordinate(x, y + diff);
        } else {
            diff -= maxCoordinate - y;
            y = maxCoordinate;
        }
        if (2 * maxCoordinate > diff) {
            return new Coordinate(x - diff, y);
        } else {
            diff -= 2 * maxCoordinate;
            x = -maxCoordinate;
        }
        if (2 * maxCoordinate > diff) {
            return new Coordinate(x, y - diff);
        } else {
            diff -= 2 * maxCoordinate;
            y = -maxCoordinate;
        }
        return new Coordinate(x + diff, y);
    }

    record Coordinate(int x, int y) {
        public int manhattanDistance() {
            return Math.abs(x) + Math.abs(y);
        }

        public Coordinate translateIntoArray(int layer) {
            return new Coordinate(layer + x, layer - y);
        }

        public Coordinate right() {
            return new Coordinate(x + 1, y);
        }

        public Coordinate left() {
            return new Coordinate(x - 1, y);
        }

        public Coordinate up() {
            return new Coordinate(x, y - 1);
        }

        public Coordinate down() {
            return new Coordinate(x, y + 1);
        }
    }
}
