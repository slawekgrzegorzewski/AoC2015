package com.adventofcode.y2019;

import com.adventofcode.utils.BlockingDequeWithMemory;
import com.adventofcode.y2019.input.Input;
import com.adventofcode.y2019.intcode.IntcodeComputer;
import com.adventofcode.y2019.intcode.commands.Memory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        try {
            executeProgram(program, input, output);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<Long> commands = new ArrayList<>();
        output.drainTo(commands);
        Map<Coordinate, Long> tiles = new HashMap<>();
        for (int i = 0; i < commands.size(); i += 3) {
            tiles.put(new Coordinate(commands.get(i).intValue(), commands.get(i + 1).intValue()), commands.get(i + 2));
        }
        return tiles.values().stream().filter(tile -> tile == BLOCK_TILE).count();
    }

    long part2() throws InterruptedException {
        ArrayList<Long> programCopy = new ArrayList<>(program);
        programCopy.set(0, 2L);
        BlockingDequeWithMemory input = new BlockingDequeWithMemory(Integer.MAX_VALUE);
        BlockingDequeWithMemory output = new BlockingDequeWithMemory(Integer.MAX_VALUE);
        BlockingQueue<GameEvent> events = new LinkedBlockingQueue<>();

        GameState gameState = null;
        List<Integer> paddleNextPositions = new ArrayList<>();

        do {
            output.clear();
            input.clear();
            events.clear();

            if (gameState != null) {
                if (gameState.contactsAfterLastPosition() > 0) {
                    int lastPosition = paddleNextPositions.isEmpty() ? gameState.paddleInitialPosition : paddleNextPositions.getLast();
                    for (int i = 0; i < gameState.contactsAfterLastPosition(); i++) {
                        paddleNextPositions.add(lastPosition);
                    }
                }
                paddleNextPositions.add(gameState.ballDropPosition());
            }

            List<Integer> paddleNextPositionsCopy = new ArrayList<>(paddleNextPositions);
            gameState = new GameState();
            GameState finalGameState = gameState;

            Thread gameThread = new Thread(() -> {
                try {
                    boolean shouldContinue = true;
                    while (!Thread.currentThread().isInterrupted() && shouldContinue) {
                        switch (events.take()) {
                            case GameEvent.Output _ -> finalGameState.output(output.take());
                            case GameEvent.InputRequested _ ->
                                    finalGameState.inputNeeded(input, paddleNextPositionsCopy);
                            case GameEvent.Finished _ -> shouldContinue = false;
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, "game");

            gameThread.start();
            try {
                executeProgram(programCopy, input, output, events);
            } finally {
                events.add(new GameEvent.Finished());
                gameThread.join();
            }
        } while (!gameState.isWin());

        return gameState.score;
    }

    private void executeProgram(List<Long> programToExecute, BlockingQueue<Long> input, BlockingQueue<Long> output) throws InterruptedException {
        Memory<Long> memory = new Memory<>(programToExecute, 0L);
        new IntcodeComputer(false).execute(memory, input, output, () -> {
        }, () -> {
        });
    }

    private void executeProgram(List<Long> programToExecute, BlockingQueue<Long> input, BlockingQueue<Long> output, BlockingQueue<GameEvent> events) throws InterruptedException {
        Memory<Long> memory = new Memory<>(programToExecute, 0L);
        new IntcodeComputer(false).execute(memory, input, output, () -> events.add(new GameEvent.InputRequested()), () -> events.add(new GameEvent.Output()));
    }

    private record Coordinate(int x, int y) {
    }

    private static final class GameState {
        int score;
        private final Map<Coordinate, Integer> tiles = new HashMap<>();
        int contactsAfterLastPosition = 0;
        int paddleInitialPosition = -1;
        int ballDropPosition = -1;
        private final List<Long> command = new ArrayList<>(3);
        Coordinate previousBallLocation = null;
        Coordinate ballLocation = null;
        Coordinate paddleLocation = null;
        Integer paddleTarget = null;

        public int contactsAfterLastPosition() {
            return contactsAfterLastPosition;
        }

        public int ballDropPosition() {
            return ballDropPosition;
        }

        public boolean isWin() {
            return !tiles.containsValue(BLOCK_TILE);
        }

        private void output(Long value) {
            command.add(value);
            if (command.size() < 3) return;
            Long x = command.get(0);
            Long y = command.get(1);
            int tile = command.get(2).intValue();
            command.clear();
            if (x == -1 && y == 0) {
                this.score = tile;
            } else {
                Coordinate coordinate = new Coordinate(x.intValue(), y.intValue());
                tiles.put(coordinate, tile);
                if (tile == BALL_TILE) {
                    previousBallLocation = ballLocation;
                    ballLocation = coordinate;
                } else if (tile == HORIZONTAL_PADDLE_TILE) {
                    paddleLocation = coordinate;
                }
            }
        }

        private void inputNeeded(BlockingDequeWithMemory input, List<Integer> paddleNextPositions) {
            if (ballLocation.y() == paddleLocation.y()) {
                ballDropPosition = ballLocation.x() + (isBallMovingRight() ? -1 : 1);
            }
            if (paddleInitialPosition == -1) paddleInitialPosition = paddleLocation.x();
            if (previousBallLocation != null && previousBallLocation.y() == paddleLocation.y() - 1 && ballLocation.y() == paddleLocation.y() - 2) {
                if (paddleTarget == null) contactsAfterLastPosition++;
                paddleTarget = null;
            }
            if (paddleTarget == null) {
                paddleTarget = paddleNextPositions.isEmpty() ? null : paddleNextPositions.removeFirst();
            }
            input.addLast(paddleTarget == null
                    ? 0L
                    : Integer.signum(paddleTarget - paddleLocation.x()));
        }

        private boolean isBallMovingRight() {
            return ballLocation.x() > previousBallLocation.x();
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
