package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class Day23 {
    private final List<String> program;


    public Day23() throws IOException {
        this.program = Input.day23();
    }

    long part1() {
        var mulCounter = new Consumer<Day18.DebugInfo>() {
            int mulCount = 0;

            @Override
            public void accept(Day18.DebugInfo debugInfo) {
                if (debugInfo.program().get(debugInfo.currentPointer()).startsWith("mul")) mulCount++;
            }

            public int getMulCount() {
                return mulCount;
            }
        };

        Day18.execute(0, program, null, null, mulCounter, null, new HashMap<>());
        return mulCounter.getMulCount();
    }

    long part2() {
        int count = 0;
        for (int number = 108_100; number <= 125100; number += 17) {
            for (int divisor = 2; divisor * divisor < number; divisor++) {
                if (number % divisor == 0) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }
}
