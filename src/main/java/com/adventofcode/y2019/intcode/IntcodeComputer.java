package com.adventofcode.y2019.intcode;

import com.adventofcode.y2019.intcode.commands.Command;
import com.adventofcode.y2019.intcode.commands.CommandFactory;
import com.adventofcode.y2019.intcode.commands.CommandInput;
import com.adventofcode.y2019.intcode.commands.CommandResult;

import java.util.concurrent.BlockingQueue;

public class IntcodeComputer {

    public ProgramResult execute(int[] memory, BlockingQueue<Integer> input, BlockingQueue<Integer> output) throws InterruptedException {
        int instructionPointer = 0;
        while (true) {
            Command command = CommandFactory.getCommand(memory[instructionPointer]);
            CommandResult commandResult = command.execute(new CommandInput(memory, instructionPointer, input, output));
            instructionPointer += commandResult.pointerMove();
            if (command.isEnd()) {
                break;
            }
        }
        return new ProgramResult(memory);
    }
}
