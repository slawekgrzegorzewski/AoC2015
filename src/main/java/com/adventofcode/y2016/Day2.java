package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.List;

public class Day2 {
    private final List<String> input;

    private final Character[][] fancyKeypadLayout = {
            {null, null, '1', null, null},
            {null, '2', '3', '4', null},
            {'5', '6', '7', '8', '9'},
            {null, 'A', 'B', 'C', null},
            {null, null, 'D', null, null},
    };

    public Day2() throws IOException {
        this.input = Input.day2();
    }

    String part1() {
        return getCode(null, new Position(1, 1));
    }

    String part2() {
        return getCode(fancyKeypadLayout, new Position(0, 2));
    }

    private String getCode(Character[][] keypadLayout, Position startPosition) {
        StringBuilder code = new StringBuilder();
        Position currentPosition = startPosition;
        for (String line : input) {
            for (byte move : line.getBytes()) {
                currentPosition = switch (move) {
                    case 'U' -> currentPosition.up(keypadLayout);
                    case 'D' -> currentPosition.down(keypadLayout);
                    case 'L' -> currentPosition.left(keypadLayout);
                    case 'R' -> currentPosition.right(keypadLayout);
                    default -> throw new IllegalStateException("Unexpected value: " + move);
                };
            }
            code.append(currentPosition.keypadValue(keypadLayout));
        }
        return code.toString();
    }

    public record Position(int x, int y) {
        public Position left(Character[][] notStandardKeypadLayout) {
            return moveAndNormalize(notStandardKeypadLayout, x - 1, y);
        }

        public Position right(Character[][] notStandardKeypadLayout) {
            return moveAndNormalize(notStandardKeypadLayout, x + 1, y);
        }

        public Position up(Character[][] notStandardKeypadLayout) {
            return moveAndNormalize(notStandardKeypadLayout, x, y - 1);
        }

        public Position down(Character[][] notStandardKeypadLayout) {
            return moveAndNormalize(notStandardKeypadLayout, x, y + 1);
        }

        private Position moveAndNormalize(Character[][] notStandardKeypadLayout, int x, int y) {
            if (notStandardKeypadLayout == null && (x < 0 || x >= 3 || y < 0 || y >= 3)) return this;
            if (notStandardKeypadLayout != null && coordinatesInBound(notStandardKeypadLayout, x, y)) return this;
            return new Position(x, y);
        }

        private static boolean coordinatesInBound(Character[][] notStandardKeypadLayout, int x, int y) {
            return y < 0
                    || y >= notStandardKeypadLayout.length
                    || x < 0
                    || x >= notStandardKeypadLayout[y].length
                    || notStandardKeypadLayout[y][x] == null;
        }

        public char keypadValue(Character[][] keypadLayout) {
            if (keypadLayout == null)
                return ("" + (x + 3 * y + 1)).charAt(0);
            else
                return keypadLayout[y][x];
        }
    }
}
