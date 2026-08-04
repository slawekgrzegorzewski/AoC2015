package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day4 {
    private final int[] range;


    public Day4() throws IOException {
        this.range = Input.day4();
    }

    long part1() {
        List<Integer> validNumbers = new ArrayList<>();
        for (int i = range[0]; i <= range[1]; i++) {
            if (isValid(digitsOf(i), false)) {
                validNumbers.add(i);
            }
        }
        return validNumbers.size();
    }

    long part2() {
        List<Integer> validNumbers = new ArrayList<>();
        for (int i = range[0]; i <= range[1]; i++) {
            if (isValid(digitsOf(i), true)) {
                validNumbers.add(i);
            }
        }
        return validNumbers.size();
    }

    private int[] digitsOf(int i) {
        int[] digits = new int[6];
        for (int position = digits.length - 1; position >= 0; position--) {
            digits[position] = i % 10;
            i /= 10;
        }
        return digits;
    }

    private boolean isValid(int[] digits, boolean part2) {
        int groupLength = 1;
        boolean anyGroupSatisfied = false;
        for (int position = 1; position <= digits.length; position++) {
            if(position < digits.length) {
                if (digits[position - 1] > digits[position]) {
                    return false;
                }
                if (digits[position - 1] == digits[position]) {
                    groupLength++;
                    continue;
                }
            }
            if (part2 ? groupLength == 2 : groupLength >= 2) {
                anyGroupSatisfied = true;
            }
            groupLength = 1;
        }
        return anyGroupSatisfied;
    }
}
