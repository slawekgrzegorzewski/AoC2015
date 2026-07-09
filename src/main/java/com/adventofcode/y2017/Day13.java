package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.Map;

public class Day13 {
    private final Map<Integer, Integer> firewallLayers;


    public Day13() throws IOException {
        this.firewallLayers = Input.day13();
    }

    long part1() {
        return firewallLayers.entrySet()
                .stream()
                .mapToInt(entry -> calculateScore(entry.getValue(), entry.getKey()))
                .sum();
    }

    long part2() {
        int delay = -1;
        while (caught(++delay)) ;
        return delay;
    }

    private boolean caught(int delay) {
        return firewallLayers.entrySet()
                .stream()
                .anyMatch(entry -> isCaught(entry.getValue(), entry.getKey() + delay));
    }

    private static boolean isCaught(Integer depth, Integer steps) {
        return getScannerPosition(depth, steps) == 1;
    }

    private static int calculateScore(Integer depth, Integer steps) {
        if (isCaught(depth, steps)) {
            return steps * depth;
        }
        return 0;
    }

    public static int getScannerPosition(Integer depth, int steps) {
        int period = 2 * (depth - 1);
        int p = steps % period;
        return (p < depth ? p : period - p) + 1;
    }
}