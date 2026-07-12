package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Day22 {
    private final TaskInput taskInput;

    public Day22() throws IOException {
        this.taskInput = Input.day22();
    }

    long part1() {
        return move(taskInput.middleX, taskInput.middleY, copyInfectedCells(), false, 10_000);
    }

    long part2() {
        return move(taskInput.middleX, taskInput.middleY, copyInfectedCells(), true, 10_000_000);
    }

    private HashMap<Integer, Map<Integer, Character>> copyInfectedCells() {
        HashMap<Integer, Map<Integer, Character>> result = new HashMap<>();
        taskInput.infectedCells.forEach((y, row) -> result.put(y, new HashMap<>(row)));
        return result;
    }

    private static int move(int x, int y, Map<Integer, Map<Integer, Character>> infectedCells, boolean part2, int bursts) {
        Direction direction = Direction.UP;
        int newlyInfected = 0;
        for (int i = 0; i < bursts - 1; i++) {
            Character currentNodeState = infectedCells
                    .getOrDefault(y, new HashMap<>())
                    .getOrDefault(x, 'c');
            if (currentNodeState == 'i') {
                if (part2) infectedCells.get(y).put(x, 'f');
                else infectedCells.get(y).remove(x);
                direction = switch (direction) {
                    case UP -> Direction.RIGHT;
                    case LEFT -> Direction.UP;
                    case DOWN -> Direction.LEFT;
                    case RIGHT -> Direction.DOWN;
                };
            } else if (currentNodeState == 'f') {
                infectedCells.get(y).remove(x);
                direction = switch (direction) {
                    case UP -> Direction.DOWN;
                    case LEFT -> Direction.RIGHT;
                    case DOWN -> Direction.UP;
                    case RIGHT -> Direction.LEFT;
                };
            } else if (currentNodeState == 'c') {
                if (part2) infectedCells.computeIfAbsent(y, _ -> new HashMap<>()).put(x, 'w');
                else {
                    infectedCells.computeIfAbsent(y, _ -> new HashMap<>()).put(x, 'i');
                    newlyInfected++;
                }
                direction = switch (direction) {
                    case UP -> Direction.LEFT;
                    case LEFT -> Direction.DOWN;
                    case DOWN -> Direction.RIGHT;
                    case RIGHT -> Direction.UP;
                };
            } else if (currentNodeState == 'w') {
                infectedCells.get(y).put(x, 'i');
                newlyInfected++;
            } else {
                throw new IllegalArgumentException("Invalid node state: " + currentNodeState);
            }
            switch (direction) {
                case UP -> y--;
                case LEFT -> x--;
                case DOWN -> y++;
                case RIGHT -> x++;
            }
        }
        return newlyInfected;
    }

    public record TaskInput(Map<Integer, Map<Integer, Character>> infectedCells, int middleX, int middleY) {

    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}
