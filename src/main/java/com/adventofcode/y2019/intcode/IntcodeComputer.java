package com.adventofcode.y2019.intcode;

import com.adventofcode.y2019.intcode.commands.Command;
import com.adventofcode.y2019.intcode.commands.CommandFactory;
import com.adventofcode.y2019.intcode.commands.CommandInput;
import com.adventofcode.y2019.intcode.commands.CommandResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class IntcodeComputer {

    public ProgramResult execute(int[] memory, Queue<Integer> input) {
        int instructionPointer = 0;
        List<Integer> output = new ArrayList<>();
        while (true) {
            Command command = CommandFactory.getCommand(memory[instructionPointer]);
            CommandResult commandResult = command.execute(new CommandInput(memory, instructionPointer, input));
            instructionPointer += commandResult.pointerMove();
            if (commandResult.output() != null) {
                output.add(commandResult.output());
            }
            if (command.isEnd()) {
                break;
            }
        }
        return new ProgramResult(memory, output);
    }
}
