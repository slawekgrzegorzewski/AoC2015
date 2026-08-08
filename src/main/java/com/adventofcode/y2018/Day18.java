package com.adventofcode.y2018;

import com.adventofcode.utils.Utils;
import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.function.Supplier;

public class Day18 {
    private final char[][] map;

    public Day18() throws IOException {
        this.map = Input.day18();
    }

    long part1() {
        return simulateNMinutes(10);
    }

    long part2() {
        return simulateNMinutes(1000000000);
    }

    private long simulateNMinutes(int i) {
        char[][] map = copy(this.map);
        char[][] copy = copy(map);
        Utils.LoopFinder.LoopInfo<Long> loopInfo = new Utils.LoopFinder<>(
                new Supplier<Long>() {
                    int currentIndex = 0;

                    @Override
                    public Long get() {
                        minute(map, copy, currentIndex % 2 == 1);
                        return evaluate(currentIndex++ % 2 == 1 ? map : copy);
                    }
                },
                result -> result,
                5)
                .findLoop()
                .orElseThrow();

        return i + 1 < loopInfo.loopStart()
                ? loopInfo.analyzedList().get(i - 1)
                : loopInfo.analyzedList().get(loopInfo.loopStart() + (i - loopInfo.loopStart() - 1) % loopInfo.loopSize());
    }

    private static long evaluate(char[][] map) {
        int woods = 0, lumberyard = 0;
        for (char[] chars : map) {
            for (char c : chars) {
                if (c == '|') woods++;
                if (c == '#') lumberyard++;
            }
        }
        return (long) woods * lumberyard;
    }

    private void minute(char[][] map, char[][] workingCopy, boolean reverse) {
        char[][] source = reverse ? workingCopy : map;
        char[][] target = reverse ? map : workingCopy;
        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < source[i].length; j++) {
                int woods = 0, lumberyards = 0;
                for (int i1 = -1; i1 <= 1; i1++) {
                    for (int j1 = -1; j1 <= 1; j1++) {
                        if (i1 == 0 && j1 == 0) {
                            continue;
                        }
                        char value = getValue(i + i1, j + j1, source);
                        if (value == '|') woods++;
                        if (value == '#') lumberyards++;
                    }
                }
                if (source[i][j] == '.' && woods >= 3) target[i][j] = '|';
                else if (source[i][j] == '|' && lumberyards >= 3) target[i][j] = '#';
                else if (source[i][j] == '#' && (woods < 1 || lumberyards < 1)) target[i][j] = '.';
                else target[i][j] = source[i][j];
            }
        }
    }

    private char getValue(int i, int j, char[][] map) {
        char[] row = null;
        if (i >= 0 && i < map.length)
            row = map[i];
        if (row != null && j >= 0 && j < row.length)
            return row[j];
        return 'c';
    }

    private char[][] copy(char[][] map) {
        char[][] copy = new char[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            char[] chars = map[i];
            copy[i] = new char[chars.length];
            System.arraycopy(chars, 0, copy[i], 0, chars.length);
        }
        return copy;
    }
}
