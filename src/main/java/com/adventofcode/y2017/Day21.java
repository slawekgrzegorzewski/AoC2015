package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Day21 {
    private final Map<String, char[][]> enhancements = new HashMap<>();

    public Day21() throws IOException {
        Input.day21().forEach((key, value) -> {
            char[][] cell = fromString(key);
            for (int i = 0; i < 4; i++) {
                enhancements.put(getString(cell, "/"), value);
                enhancements.put(getString(flipHorizontally(cell), "/"), value);
                enhancements.put(getString(flipVertically(cell), "/"), value);
                cell = rotate(cell);
            }
        });
    }

    long part1() {
        return work(5);
    }

    long part2() {
        return work(18);
    }

    private int work(int x) {
        char[][] paint = new char[][]{
                {'.', '#', '.'},
                {'.', '.', '#'},
                {'#', '#', '#'}
        };
        for (int i = 0; i < x; i++) {
            paint = iterate(paint);
        }
        return countPixelsOn(paint);
    }


    private char[][] iterate(char[][] paint) {
        int newSize = paint.length % 2 == 0 ? paint.length / 2 * 3 : paint.length / 3 * 4;
        char[][] newPaint = new char[newSize][newSize];
        findAndReplaceCells(paint, newPaint);
        return newPaint;
    }

    private void findAndReplaceCells(char[][] paint, char[][] newPaint) {
        int size = paint.length;
        int stepSize = paint.length % 2 == 0 ? 2 : 3;
        for (int row = 0; row < size; row += stepSize) {
            for (int column = 0; column < size; column += stepSize) {
                char[][] cell = new char[stepSize][stepSize];
                for (int k = 0; k < stepSize; k++) {
                    System.arraycopy(paint[row + k], column, cell[k], 0, stepSize);
                }
                copy(newPaint, enhancements.get(getString(cell, "/")), row, column, stepSize);
            }
        }
    }

    public static void copy(char[][] destination, char[][] source, int sourceRow, int sourceColumn, int stepSize) {
        int toSize = stepSize + 1;
        for (int k = 0; k < toSize; k++) {
            System.arraycopy(source[k], 0, destination[sourceRow / stepSize * toSize + k], sourceColumn / stepSize * toSize, toSize);
        }
    }

    private static int countPixelsOn(char[][] paint) {
        int onCount = 0;
        for (char[] chars : paint) {
            for (char c : chars) {
                if (c == '#') {
                    onCount++;
                }
            }
        }
        return onCount;
    }

    public static char[][] rotate(char[][] array) {
        return transform(array, (_, y) -> array.length - 1 - y, (x, _) -> x);
    }

    public static char[][] flipVertically(char[][] array) {
        return transform(array, (x, _) -> x, (_, y) -> array.length - 1 - y);
    }

    public static char[][] flipHorizontally(char[][] array) {
        return transform(array, (x, _) -> array.length - 1 - x, (_, y) -> y);
    }

    private static char[][] transform(
            char[][] array,
            BiFunction<Integer, Integer, Integer> newX, BiFunction<Integer, Integer, Integer> newY) {
        char[][] rotated = new char[array.length][array[0].length];
        for (int x = 0; x < array.length; x++) {
            for (int y = 0; y < array.length; y++) {
                rotated[newY.apply(x, y)][newX.apply(x, y)] = array[y][x];
            }
        }
        return rotated;
    }

    private static void print(char[][] array) {
        System.out.println(getString(array, "\n"));
    }

    private static String getString(char[][] array, String delimiter) {
        return Arrays.stream(array)
                .map(String::new)
                .collect(Collectors.joining(delimiter));
    }

    private char[][] fromString(String value) {
        return Arrays.stream(value.split("/")).map(String::toCharArray).toArray(char[][]::new);
    }

}