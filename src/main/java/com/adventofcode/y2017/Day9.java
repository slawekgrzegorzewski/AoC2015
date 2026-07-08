package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;

public class Day9 {
    private final char[] stream;


    public Day9() throws IOException {
        this.stream = Input.day9();
    }

    long part1() {
        return processStream()[0];
    }

    long part2() {
        return processStream()[1];
    }

    private int[] processStream() {
        State state = State.IDLE;
        int score = 0, sum = 0, garbageLength = 0;
        for (char c : stream) {
            if (state == State.GARBAGE_IGNORE_NEXT) {
                state = State.GARBAGE;
                continue;
            }
            if (state == State.GARBAGE) {
                if (c == '>') {
                    state = State.IDLE;
                } else if (c == '!') {
                    state = State.GARBAGE_IGNORE_NEXT;
                } else {
                    garbageLength++;
                }
                continue;
            }
            if (c == '<') {
                state = State.GARBAGE;
                continue;
            }
            if (c == '{') {
                score++;
                sum += score;
            }
            if (c == '}') {
                score--;
            }
        }
        if (score != 0) throw new IllegalStateException("Score should be 0");
        return new int[]{sum, garbageLength};
    }

    public enum State {
        IDLE, GARBAGE, GARBAGE_IGNORE_NEXT
    }
}
