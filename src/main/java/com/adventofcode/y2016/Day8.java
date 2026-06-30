package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Day8 {
    private final List<String> input;


    public Day8() throws IOException {
        this.input = Input.day8();
    }

    long part1() {
        char[][] display = new char[6][50];
        return runInstructions(display);
    }

    String part2() {
        char[][] display = new char[6][50];
        runInstructions(display);
        print(display);
        return "EOARGPHYAO";
    }

    private long runInstructions(char[][] display) {
        for (int i = 0; i < display.length; i++) {
            for (int j = 0; j < display[0].length; j++) {
                display[i][j] = '.';
            }
        }
        input
                .stream()
                .map(s -> {
                    if (s.startsWith("rect ")) return Rect.parse(s.replace("rect ", ""));
                    if (s.startsWith("rotate column ")) return RotateColumn.parse(s.replace("rotate column ", ""));
                    if (s.startsWith("rotate row ")) return RotateRow.parse(s.replace("rotate row ", ""));
                    throw new IllegalStateException("Unknown instruction: " + s);
                })
                .forEach(instruction -> instruction.execute(display));
        return Arrays.stream(display)
                .mapToLong(l -> {
                    long count = 0;
                    for (char c : l) {
                        if (c == '#') count++;
                    }
                    return count;
                })
                .sum();
    }

    private static void print(char[][] display) {
        System.out.println(getString(display));
    }

    private static String getString(char[][] display) {
        StringBuilder sb = new StringBuilder();
        for (char[] chars : display) {
            sb.append(chars).append("\n");
        }
        return sb.toString();
    }

    static abstract class Instruction {
        public abstract void execute(char[][] display);
    }

    static final class Rect extends Instruction {
        private final int width;
        private final int height;

        Rect(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public static Rect parse(String value) {
            String[] parts = value.split("x");
            return new Rect(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
        }

        @Override
        public void execute(char[][] display) {
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++)
                    display[y][x] = '#';
        }
    }

    static final class RotateColumn extends Instruction {
        private final int x;
        private final int amount;

        RotateColumn(int x, int amount) {
            this.x = x;
            this.amount = amount;
        }

        public static RotateColumn parse(String value) {
            String[] parts = value.replace("x=", "")
                    .split(" by ");
            return new RotateColumn(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
        }

        @Override
        public void execute(char[][] display) {
            char[] column = new char[display.length];
            for (int i = 0; i < display.length; i++) {
                column[i] = display[i][x];
            }
            for (int i = 0; i < display.length; i++) {
                display[(i + amount) % display.length][x] = column[i];
            }
        }
    }

    static final class RotateRow extends Instruction {
        private final int y;
        private final int amount;

        RotateRow(int y, int amount) {
            this.y = y;
            this.amount = amount;
        }

        public static RotateRow parse(String value) {
            String[] parts = value.replace("y=", "")
                    .split(" by ");
            return new RotateRow(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
        }

        @Override
        public void execute(char[][] display) {
            char[] row = Arrays.copyOf(display[y], display[y].length);
            for (int i = 0; i < display[y].length; i++) {
                display[y][(i + amount) % display[y].length] = row[i];
            }
        }
    }
}
