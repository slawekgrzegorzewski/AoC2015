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
        return move(taskInput.middleX, taskInput.middleY, new HashMap<>(taskInput.infectedCells), false, 10_000);
    }

    long part2() {
        return move(taskInput.middleX, taskInput.middleY, new HashMap<>(taskInput.infectedCells), true, 10_000_000);
    }

    private static int move(int x, int y, Map<Coordinates, Character> infectedCells, boolean part2, int bursts) {
        Direction direction = Direction.UP;
        int newlyInfected = 0;
        for (int i = 0; i < bursts - 1; i++) {
            Coordinates currentNode = new Coordinates(x, y);
            Character currentNodeState = infectedCells.getOrDefault(currentNode, 'c');
            if (currentNodeState == 'i') {
                if (part2) infectedCells.put(currentNode, 'f');
                else infectedCells.remove(currentNode);
                direction = switch (direction) {
                    case UP -> Direction.RIGHT;
                    case LEFT -> Direction.UP;
                    case DOWN -> Direction.LEFT;
                    case RIGHT -> Direction.DOWN;
                };
            } else if (currentNodeState == 'f') {
                infectedCells.remove(currentNode);
                direction = switch (direction) {
                    case UP -> Direction.DOWN;
                    case LEFT -> Direction.RIGHT;
                    case DOWN -> Direction.UP;
                    case RIGHT -> Direction.LEFT;
                };
            } else if (currentNodeState == 'c') {
                if (part2) infectedCells.put(currentNode, 'w');
                else {
                    infectedCells.put(currentNode, 'i');
                    newlyInfected++;
                }
                direction = switch (direction) {
                    case UP -> Direction.LEFT;
                    case LEFT -> Direction.DOWN;
                    case DOWN -> Direction.RIGHT;
                    case RIGHT -> Direction.UP;
                };
            } else if (currentNodeState == 'w') {
                infectedCells.put(currentNode, 'i');
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

    public record TaskInput(Map<Coordinates, Character> infectedCells, int middleX, int middleY) {

    }

    public record Coordinates(int x, int y) {

    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}
