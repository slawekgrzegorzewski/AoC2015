package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day9 {
    private final String input;


    public Day9() throws IOException {
        this.input = Input.day9();
    }

    long part1() {
        return calculateLength(input, false);
    }

    long part2() {
        return calculateLength(input, true);
    }

    private static long calculateLength(String line, boolean expandRepetitions) {
        long outputLenght = 0;
        Pattern pattern = Pattern.compile("\\((\\d+)x(\\d+)\\)");
        Matcher matcher = pattern.matcher(line);
        int nextIndex = 0;
        while (matcher.find(nextIndex)) {
            int length = Integer.parseInt(matcher.group(1));
            int repeat = Integer.parseInt(matcher.group(2));
            String patternToRepeat = line.substring(matcher.end(), matcher.end() + length);
            outputLenght += matcher.start() - nextIndex + repeat * (expandRepetitions ? calculateLength(patternToRepeat, expandRepetitions) : length);
            nextIndex = matcher.end() + length;
        }
        outputLenght += line.length() - nextIndex;
        return outputLenght;
    }
}
