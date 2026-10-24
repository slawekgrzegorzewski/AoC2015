package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;
import com.adventofcode.y2019.intcode.IntcodeComputer;
import com.adventofcode.y2019.intcode.commands.Memory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

public class Day13 {
    private final List<Long> program;
    private static final int BLOCK_TILE = 2;
    private static final int HORIZONTAL_PADDLE_TILE = 3;
    private static final int BALL_TILE = 4;

    public Day13() throws IOException {
        this.program = Input.day13();
    }

    long part1() {
        List<Long> output = new ArrayList<>();
        executeProgram(new Memory<>(program, 0L), null, output::add);
        Set<Coordinate> tiles = new HashSet<>();
        for (int i = 0; i < output.size(); i += 3) {
            Coordinate coordinate = new Coordinate(output.get(i).intValue(), output.get(i + 1).intValue());
            tiles.remove(coordinate);
            if (output.get(i + 2) == BLOCK_TILE) {
                tiles.add(coordinate);
            }
        }
        return tiles.size();
    }

    long part2() throws InterruptedException {
        GameState gameState = new GameState();
        Memory<Long> memory = new Memory<>(program, 0L);
        memory.set(0, 2L);
        executeProgram(
                memory,
                gameState::input,
                gameState::output);
        return gameState.score;
    }

    private void executeProgram(Memory<Long> memory, LongSupplier input, LongConsumer output) {
        try {
            new IntcodeComputer(false).execute(memory, input, output);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private record Coordinate(int x, int y) {
    }

    private static final class GameState {
        private final int[] command = new int[3];
        int commandIndex = 0;
        int score;
        int ballX;
        int paddleX;

        private void output(long value) {
            command[commandIndex++] = (int) value;
            if (commandIndex < 3) return;
            processCommand(command[0], command[1], command[2]);
            commandIndex = 0;
        }

        private void processCommand(int x, int y, int tile) {
            if (x == -1 && y == 0) {
                this.score = tile;
            } else {
                if (tile == BALL_TILE) {
                    ballX = x;
                } else if (tile == HORIZONTAL_PADDLE_TILE) {
                    paddleX = x;
                }
            }
        }

        private long input() {
            return Integer.signum(ballX - paddleX);
        }
    }
}
