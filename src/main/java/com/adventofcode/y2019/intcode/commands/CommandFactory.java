package com.adventofcode.y2019.intcode.commands;

public class CommandFactory {
    public static Command getCommand(int opcode) {
        return switch (opcode % 100) {
            case 1 -> new AddCommand();
            case 2 -> new MultiplyCommand();
            case 3 -> new ReadInputCommand();
            case 4 -> new WriteOutputCommand();
            case 5 -> new JumpIfTrueCommand();
            case 6 -> new JumpIfFalseCommand();
            case 7 -> new LessThanCommand();
            case 8 -> new EqualsCommand();
            case 99 -> new EndCommand();
            default -> throw new IllegalArgumentException("Invalid opcode: " + opcode);
        };
    }
}