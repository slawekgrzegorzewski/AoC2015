package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.Arrays;

public class Day18 {
    private final boolean[] trapsMap;


    public Day18() throws IOException {
        this.trapsMap = Input.day18();
    }

    long part1() {
        return work(40);
    }

    long part2() {
        return work(400_000);
    }

    private int work(int totalRows) {
        int safeTiles = 0;
        for (boolean b : trapsMap) {
            if (b) {
                safeTiles++;
            }
        }
        boolean[] line = trapsMap;
        boolean[] nextLine = new boolean[trapsMap.length];
        for (int i = 0; i < totalRows - 1; i++) {
            for (int j = 0; j < line.length; j++) {
                boolean leftIsSafe = j == 0 || line[j - 1];
                boolean middleIsSafe = line[j];
                boolean rightIsSafe = j == line.length - 1 || line[j + 1];
                //..^ - trap
                //.^^ - trap
                //^^. - trap
                //^.. - trap

                //... - safe
                //.^. - safe
                //^.^ - safe
                //^^^ - safe
                nextLine[j] = (leftIsSafe && middleIsSafe && rightIsSafe)
                        || (leftIsSafe && !middleIsSafe && rightIsSafe)
                        || (!leftIsSafe && middleIsSafe && !rightIsSafe)
                        || (!leftIsSafe && !middleIsSafe && !rightIsSafe);
                if (nextLine[j]) {
                    safeTiles++;
                }
            }
            line = Arrays.copyOf(nextLine, nextLine.length);
        }
        return safeTiles;
    }
}
