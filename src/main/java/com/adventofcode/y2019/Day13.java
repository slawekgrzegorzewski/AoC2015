package com.adventofcode.y2019;

import com.adventofcode.utils.BlockingDequeWithMemory;
import com.adventofcode.y2019.input.Input;
import com.adventofcode.y2019.intcode.IntcodeComputer;
import com.adventofcode.y2019.intcode.commands.Memory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class Day13 {
    private final List<Long> program;
    private static final int BLOCK_TILE = 2;
    private static final int HORIZONTAL_PADDLE_TILE = 3;
    private static final int BALL_TILE = 4;

    public Day13() throws IOException {
        this.program = Input.day13();
    }

    long part1() {
        BlockingDeque<Long> input = new LinkedBlockingDeque<>();
        BlockingDequeWithMemory output = new BlockingDequeWithMemory(Integer.MAX_VALUE);
        executeProgram(program, input, output, () -> {
        }, () -> {
        });
        List<Long> commands = new ArrayList<>();
        output.drainTo(commands);
        Set<Coordinate> tiles = new HashSet<>();
        for (int i = 0; i < commands.size(); i += 3) {
            if (commands.get(i + 2) == BLOCK_TILE)
                tiles.add(new Coordinate(commands.get(i).intValue(), commands.get(i + 1).intValue()));
        }
        return tiles.size();
    }

    long part2() throws InterruptedException {
        List<Long> programCopy = new ArrayList<>(program);
        programCopy.set(0, 2L);
        BlockingDequeWithMemory input = new BlockingDequeWithMemory(Integer.MAX_VALUE);
        BlockingDequeWithMemory output = new BlockingDequeWithMemory(Integer.MAX_VALUE);
        BlockingQueue<GameEvent> events = new LinkedBlockingQueue<>();

        GameState gameState = new GameState();
        Thread gameThread = new Thread(() -> {
            try {
                boolean shouldContinue = true;
                while (!Thread.currentThread().isInterrupted() && shouldContinue) {
                    switch (events.take()) {
                        case GameEvent.Output _ -> gameState.output(output.take().intValue());
                        case GameEvent.InputRequested _ -> gameState.inputNeeded(input);
                        case GameEvent.Finished _ -> shouldContinue = false;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "game");
        gameThread.start();
        try {
            executeProgram(programCopy, input, output, () -> events.add(new GameEvent.InputRequested()), () -> events.add(new GameEvent.Output()));
        } finally {
            events.add(new GameEvent.Finished());
            gameThread.join();
        }
        return gameState.score;
    }

    private void executeProgram(List<Long> programToExecute, BlockingQueue<Long> input, BlockingQueue<Long> output, Runnable onInputNeeded, Runnable onOutput) {
        Memory<Long> memory = new Memory<>(programToExecute, 0L);
        try {
            new IntcodeComputer(false).execute(memory, input, output, onInputNeeded, onOutput);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private record Coordinate(int x, int y) {
    }

    private static final class GameState {
        int score;
        private final List<Integer> command = new ArrayList<>(3);
        Coordinate previousBallLocation = null;
        Coordinate ballLocation = null;
        Coordinate paddleLocation = null;

        private void output(int value) {
            command.add(value);
            if (command.size() < 3) return;
            int x = command.get(0);
            int y = command.get(1);
            int tile = command.get(2);
            command.clear();
            if (x == -1 && y == 0) {
                this.score = tile;
            } else {
                Coordinate coordinate = new Coordinate(x, y);
                if (tile == BALL_TILE) {
                    previousBallLocation = ballLocation;
                    ballLocation = coordinate;
                } else if (tile == HORIZONTAL_PADDLE_TILE) {
                    paddleLocation = coordinate;
                }
            }
        }

        private void inputNeeded(BlockingDequeWithMemory input) {
            input.addLast((long) Integer.signum(ballLocation.x() - paddleLocation.x()));
        }
    }

    sealed interface GameEvent {
        record Output() implements GameEvent {
        }

        record InputRequested() implements GameEvent {
        }

        record Finished() implements GameEvent {
        }
    }
}
