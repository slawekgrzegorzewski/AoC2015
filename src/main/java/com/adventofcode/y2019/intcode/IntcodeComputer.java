package com.adventofcode.y2019.intcode;

import com.adventofcode.y2019.intcode.commands.*;

import java.util.concurrent.BlockingQueue;

public class IntcodeComputer {

    private final boolean debug;

    public IntcodeComputer(boolean debug) {
        this.debug = debug;
    }

    public ProgramResult execute(Memory<Long> memory, BlockingQueue<Long> input, BlockingQueue<Long> output) throws InterruptedException {
        long instructionPointer = 0;
        long relativeBase = 0;
        while (true) {
            Command command = CommandFactory.getCommand(Math.toIntExact(memory.get(instructionPointer)));
            CommandResult commandResult = command.execute(new CommandInput(memory, instructionPointer, relativeBase, input, output, debug));
            instructionPointer += commandResult.pointerMove();
            relativeBase = commandResult.relativeBase();
            if (command.isEnd()) {
                break;
            }
        }
        return new ProgramResult(memory);
    }
}
