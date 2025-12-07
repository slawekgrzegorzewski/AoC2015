package com.adventofcode;

import com.adventofcode.input.Input;

import java.io.IOException;

public class Day10 {

    private final String number;

    public Day10() throws IOException {
        number = Input.day10();
    }

    long part1() {
        String value = number;
        for (int i = 0; i < 40; i++) {
            value = lookAndSay(value);
        }
        return value.length();
    }

    long part2() {
        String value = number;
        for (int i = 0; i < 50; i++) {
            value = lookAndSay(value);
        }
        return value.length();
    }

    private String lookAndSay(String input) {
        StringBuilder result = new StringBuilder();
        char lastSeenDigit = '\n';
        int counter = 0;
        for (char character : input.toCharArray()) {
            if (lastSeenDigit != character) {
                if (lastSeenDigit != '\n') {
                    result.append(counter).append(lastSeenDigit);
                }
                counter = 0;
                lastSeenDigit = character;
            }
            counter++;
        }
        result.append(counter).append(lastSeenDigit);
        return result.toString();
    }

}