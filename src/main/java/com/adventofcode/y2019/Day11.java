package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;
import com.adventofcode.y2019.intcode.IntcodeComputer;
import com.adventofcode.y2019.intcode.commands.Memory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Day11 {
    public static final long HALT_SIGNAL = Long.MIN_VALUE;
    public static final long EXCEPTION_SIGNAL = Long.MAX_VALUE;
    public static final int UP = 0;
    public static final int RIGHT = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    public static final long BLACK = 0L;
    public static final long WHITE = 1L;
    private final List<Long> program;


    public Day11() throws IOException {
        this.program = Input.day11();
    }

    long part1() throws InterruptedException {
        return makeRobotMoves(BLACK).painted().size();
    }

    String part2() throws InterruptedException {
        Map<Coordinate, Long> painting = makeRobotMoves(WHITE).painting();
        printPainting(painting);
        return "GLBEPJZP";
    }

    private RobotMovesResult makeRobotMoves(long initialColor) throws InterruptedException {
        BlockingDeque<Long> input = new LinkedBlockingDeque<>();
        BlockingDeque<Long> output = new LinkedBlockingDeque<>();
        Thread thread = new Thread(() -> {
            try {
                executeProgram(input, output);
                output.add(HALT_SIGNAL);
            } catch (InterruptedException e) {
                output.add(EXCEPTION_SIGNAL);
                throw new RuntimeException(e);
            }
        });
        thread.start();

        Coordinate robotPosition = new Coordinate(0, 0);
        Set<Coordinate> painted = new HashSet<>();
        Map<Coordinate, Long> positionsColor = new HashMap<>();
        positionsColor.put(robotPosition, initialColor);
        int robotDirection = UP;
        while (true) {
            long currentColor = positionsColor.getOrDefault(robotPosition, BLACK);
            input.add(currentColor);
            long color = output.take();
            if (color == HALT_SIGNAL) {
                break;
            }
            if (color == EXCEPTION_SIGNAL) {
                throw new RuntimeException("Robot crashed");
            }
            long direction = output.take();
            painted.add(robotPosition);
            positionsColor.put(robotPosition, color);
            robotDirection += (direction > 0 ? 1 : -1);
            if (robotDirection > 3) {
                robotDirection = 0;
            } else if (robotDirection < 0) {
                robotDirection = 3;
            }
            robotPosition = switch (robotDirection) {
                case UP -> new Coordinate(robotPosition.x(), robotPosition.y() - 1);
                case RIGHT -> new Coordinate(robotPosition.x() + 1, robotPosition.y());
                case DOWN -> new Coordinate(robotPosition.x(), robotPosition.y() + 1);
                case LEFT -> new Coordinate(robotPosition.x() - 1, robotPosition.y());
                default -> throw new IllegalStateException("Unexpected value: " + robotDirection);
            };
        }
        thread.join();
        return new RobotMovesResult(positionsColor, painted);
    }

    private void printPainting(Map<Coordinate, Long> painting) {
        IntSummaryStatistics xStats = painting.keySet().stream().mapToInt(coordinate -> coordinate.x).summaryStatistics();
        IntSummaryStatistics yStats = painting.keySet().stream().mapToInt(coordinate -> coordinate.y).summaryStatistics();
        for (int y = yStats.getMin(); y <= yStats.getMax(); y++) {
            for (int x = xStats.getMin(); x <= xStats.getMax(); x++) {
                Long color = painting.getOrDefault(new Coordinate(x, y), BLACK);
                System.out.print(color == WHITE ? "#" : ".");
            }
            System.out.println();
        }
    }


    private void executeProgram(BlockingDeque<Long> input, BlockingDeque<Long> output) throws InterruptedException {
        Memory<Long> memory = new Memory<>(program, 0L);
        new IntcodeComputer(false).execute(memory, input, output);
    }

    public record Coordinate(int x, int y) {
    }

    public record RobotMovesResult(Map<Coordinate, Long> painting, Set<Coordinate> painted) {
    }
}
