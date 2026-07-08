package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day8 {
    private final List<Day8.Instruction> instructions;


    public Day8() throws IOException {
        this.instructions = Input.day8();
    }

    long part1() {
        return executeInstructions()[0];
    }

    long part2() {
        return executeInstructions()[1];
    }

    private long[] executeInstructions() {
        int maxValue = Integer.MIN_VALUE;
        Map<String, Integer> registers = new HashMap<>();
        for (Instruction instruction : instructions) {
            Condition condition = instruction.condition();
            if (condition.check(registers)) {
                instruction.execute(registers);
                maxValue = Math.max(maxValue, (int) findMaxValue(registers));
            }
        }
        return new long[]{findMaxValue(registers), maxValue};
    }

    private static long findMaxValue(Map<String, Integer> registers) {
        return registers.values().stream().mapToLong(Long::valueOf).max().orElse(0L);
    }

    public enum Operation {
        INC("inc"),
        DEC("dec");

        private final String command;

        Operation(String command) {
            this.command = command;
        }

        public static Operation fromString(String command) {
            return switch (command) {
                case "inc" -> INC;
                case "dec" -> DEC;
                default -> throw new IllegalArgumentException(command);
            };
        }

        public String getCommand() {
            return command;
        }

        public static String toRegexpExpression() {
            return Arrays.stream(Operation.values()).map(Operation::getCommand).collect(Collectors.joining("|"));
        }

    }

    public enum Comparator {
        GT(">"),
        GE(">="),
        EQ("=="),
        NE("!="),
        LT("<"),
        LE("<=");

        private final String stringValue;

        Comparator(String command) {
            this.stringValue = command;
        }

        public static Comparator fromString(String stringValue) {
            return switch (stringValue) {
                case ">" -> GT;
                case ">=" -> GE;
                case "==" -> EQ;
                case "!=" -> NE;
                case "<" -> LT;
                case "<=" -> LE;
                default -> throw new IllegalArgumentException(stringValue);
            };
        }

        public String getStringValue() {
            return stringValue;
        }

        public static String toRegexpExpression() {
            return Arrays.stream(Comparator.values()).map(Comparator::getStringValue).collect(Collectors.joining("|"));
        }

        public boolean compare(int leftValue, int rightValue) {
            return switch (this) {
                case GT -> leftValue > rightValue;
                case GE -> leftValue >= rightValue;
                case EQ -> leftValue == rightValue;
                case NE -> leftValue != rightValue;
                case LT -> leftValue < rightValue;
                case LE -> leftValue <= rightValue;
            };
        }

    }

    public record Condition(String register, Comparator comparator, int value) {
        public boolean check(Map<String, Integer> registers) {
            return comparator.compare(
                    registers.computeIfAbsent(register, _ -> 0),
                    value);
        }
    }

    public record Instruction(String register, Operation operation, int value, Condition condition) {
        private final static Pattern pattern;

        static {
            String patternString = "([a-z]+)\\s+(%s)\\s+([+|-]*[0-9]+)\\s+if\\s+([a-z]+)\\s+(%s)\\s+([+|-]*[0-9]+)";
            pattern = Pattern.compile(patternString.formatted(Operation.toRegexpExpression(), Comparator.toRegexpExpression()));
        }

        public static Instruction parse(String line) {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.find()) throw new RuntimeException(line);
            return new Instruction(
                    matcher.group(1),
                    Operation.fromString(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)),
                    new Condition(
                            matcher.group(4),
                            Comparator.fromString(matcher.group(5)),
                            Integer.parseInt(matcher.group(6))));
        }

        public void execute(Map<String, Integer> registers) {
            switch (this.operation) {
                case INC -> registers.compute(register, (_, v) -> (v == null ? 0 : v) + value);
                case DEC -> registers.compute(register, (_, v) -> (v == null ? 0 : v) - value);
            }
        }
    }
}
