package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;
import com.adventofcode.y2019.intcode.IntcodeComputer;
import com.adventofcode.y2019.intcode.commands.Memory;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day17 {
    private final List<Long> program;
    private final Map<Point, Character> map;


    public Day17() throws IOException {
        this.program = Input.day17();
        MapCollector mapCollector = new MapCollector();
        executeProgram(new Memory<>(program, 0L),
                () -> 0L,
                output -> mapCollector.addPoint((char) output));
        map = mapCollector.map();
    }

    long part1() {
        return map.keySet().stream()
                .filter(point -> Stream.of(point, point.up(), point.down(), point.left(), point.right())
                        .map(p -> map.getOrDefault(p, ' '))
                        .allMatch(c -> c == '#'))
                .mapToInt(point -> point.x * point.y)
                .sum();
    }

    long part2() {
        List<String> fullPathExplorationInstructions = fullPathExplorationInstructions();

        Instructions shortestInstructions = null;

        for (int a = 1; a <= fullPathExplorationInstructions.size() / 2; a++) {
            for (int b = 1; b <= fullPathExplorationInstructions.size() / 2 - a; b++) {
                Instructions instructions = findInstructions(fullPathExplorationInstructions, a, b)
                        .filter(Instructions::isInMemoryLimit)
                        .orElse(null);
                if (instructions == null) continue;
                if (shortestInstructions == null || instructions.getInput().length() < shortestInstructions.getInput().length()) {
                    shortestInstructions = instructions;
                }
            }
        }
        if (shortestInstructions == null) throw new IllegalStateException("No instructions found");
        Memory<Long> memory = new Memory<>(program, 0L);
        memory.set(0, 2L);
        List<Integer> instructionsAsChars = (shortestInstructions.getInput() + "n\n")
                .chars()
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));
        long[] output = new long[1];
        executeProgram(memory, instructionsAsChars::removeFirst, value -> output[0] = value);
        return output[0];
    }

    private List<String> fullPathExplorationInstructions() {
        List<String> instructions = new ArrayList<>();
        Point robotLocation = map.keySet().stream()
                .filter(point -> map.get(point) == '^')
                .findFirst()
                .orElseThrow();
        Orientation robotOrientation = Orientation.UP;
        RotationAndSteps rotationAndSteps;
        while ((rotationAndSteps = findMove(robotLocation, robotOrientation).orElse(null)) != null) {
            instructions.add(rotationAndSteps.rotation);
            instructions.add(rotationAndSteps.steps);
            robotOrientation = robotOrientation.applyRotation(rotationAndSteps.rotation);
            robotLocation = robotLocation.move(robotOrientation, Integer.parseInt(rotationAndSteps.steps));
        }
        return instructions;
    }

    private Optional<RotationAndSteps> findMove(Point robotLocation, Orientation robotOrientation) {
        Function<Point, Point> followFunction;
        String rotation;
        if (robotOrientation == Orientation.UP || robotOrientation == Orientation.DOWN) {
            char onRight = map.getOrDefault(robotLocation.right(), ' ');
            char onLeft = map.getOrDefault(robotLocation.left(), ' ');
            if (onRight == '#') {
                followFunction = Point::right;
                rotation = robotOrientation == Orientation.UP ? "R" : "L";
            } else if (onLeft == '#') {
                followFunction = Point::left;
                rotation = robotOrientation == Orientation.DOWN ? "R" : "L";
            } else {
                return Optional.empty();
            }
        } else {
            char above = map.getOrDefault(robotLocation.up(), ' ');
            char below = map.getOrDefault(robotLocation.down(), ' ');
            if (above == '#' && below == '#') throw new RuntimeException("Both valid vertical directions");
            if (above == '#') {
                followFunction = Point::up;
                rotation = robotOrientation == Orientation.LEFT ? "R" : "L";
            } else if (below == '#') {
                followFunction = Point::down;
                rotation = robotOrientation == Orientation.RIGHT ? "R" : "L";
            } else {
                return Optional.empty();
            }
        }
        int moves = 0;
        robotLocation = followFunction.apply(robotLocation);
        while (map.getOrDefault(robotLocation, ' ') == '#') {
            moves++;
            robotLocation = followFunction.apply(robotLocation);
        }
        return Optional.of(new RotationAndSteps(rotation, String.valueOf(moves)));
    }

    public Optional<Instructions> findInstructions(List<String> instructions, int aLength, int bLength) {
        String a = String.join(",", instructions.subList(0, 2 * aLength));
        String instruction = String.join(",", instructions);
        instruction = instruction.replaceAll(a, "A");
        String[] bParts = Arrays.copyOfRange(
                extractNextUnassignedSegment(instruction, "A").split(","),
                0,
                2 * bLength);
        if (bParts.length != 2 * bLength)
            return Optional.empty();
        String b = String.join(",", bParts);
        instruction = instruction.replaceAll(b, "B");
        String c = extractNextUnassignedSegment(instruction, "AB");
        instruction = instruction.replaceAll(c, "C");
        if (instruction.matches("[ABC](,[ABC])*")) {
            return Optional.of(new Instructions(instruction, a, b, c));
        }
        return Optional.empty();
    }

    private static @NonNull String extractNextUnassignedSegment(String instruction, String assignedFunctions) {
        return instruction.replaceFirst("^([" + assignedFunctions + "],)+", "")
                .replaceFirst("[" + assignedFunctions + "].*$", "")
                .replaceAll("^,+|,+$", "");
    }

    private void executeProgram(Memory<Long> memory, LongSupplier input, LongConsumer output) {
        try {
            new IntcodeComputer(false).execute(memory, input, output);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public record Instructions(String main, String a, String b, String c) {
        public boolean isInMemoryLimit() {
            return main.length() <= 20 && a.length() <= 20 && b.length() <= 20 && c.length() <= 20;
        }

        public String getInput() {
            return main + "\n" + a + "\n" + b + "\n" + c + "\n";
        }
    }

    public record RotationAndSteps(String rotation, String steps) {
    }

    public enum Orientation {
        UP, DOWN, LEFT, RIGHT;

        public Orientation applyRotation(String rotation) {
            return switch (rotation) {
                case "R" -> switch (this) {
                    case UP -> RIGHT;
                    case DOWN -> LEFT;
                    case RIGHT -> DOWN;
                    case LEFT -> UP;
                };
                case "L" -> switch (this) {
                    case UP -> LEFT;
                    case DOWN -> RIGHT;
                    case RIGHT -> UP;
                    case LEFT -> DOWN;
                };
                default -> throw new RuntimeException("Illegal rotation " + rotation + " on " + this);
            };
        }
    }

    public record Point(int x, int y) {
        Point up() {
            return up(1);
        }

        Point down() {
            return down(1);
        }

        Point left() {
            return left(1);
        }

        Point right() {
            return right(1);
        }

        Point up(int steps) {
            return new Point(x, y - steps);
        }

        Point down(int steps) {
            return new Point(x, y + steps);
        }

        Point left(int steps) {
            return new Point(x - steps, y);
        }

        Point right(int steps) {
            return new Point(x + steps, y);
        }

        public Point move(Orientation orientation, int steps) {
            return switch (orientation) {
                case UP -> up(steps);
                case DOWN -> down(steps);
                case RIGHT -> right(steps);
                case LEFT -> left(steps);
            };
        }
    }

    public static class MapCollector {
        private final Map<Point, Character> map = new HashMap<>();
        int currentRowIndex = 0;
        int currentColumnIndex = 0;

        public void addPoint(char character) {
            if (character == '\n') {
                currentRowIndex++;
                currentColumnIndex = 0;
            } else {
                map.put(new Point(currentColumnIndex, currentRowIndex), character);
                currentColumnIndex++;
            }
        }

        public Map<Point, Character> map() {
            return map;
        }
    }
}
