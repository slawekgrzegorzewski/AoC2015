package com.adventofcode;

import com.adventofcode.input.Input;

import java.io.IOException;
import java.util.List;

public class Day1 {
    private final List<String> input;


    public Day1() throws IOException {
        this.input = Input.day1();
    }

    long part1() {
        long floor = 0L;
        for (char c : input.get(0).toCharArray()) {
            if (c == '(') floor++;
            if (c == ')') floor--;
        }
        return floor;
    }

    long part2() {
        long floor = 0L;
        char[] chars = input.get(0).toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '(') floor++;
            if (c == ')') floor--;
            if(floor == -1)return i + 1;
        }
        throw new RuntimeException();
    }
}


