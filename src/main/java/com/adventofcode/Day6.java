package com.adventofcode;

import com.adventofcode.input.Input;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;

public class Day6 {

    private final List<Input.LitInstruction> litInstructions;

    public Day6() throws IOException {
        litInstructions = Input.day6();
    }

    long part1() {
        return solution(litInstruction -> switch (litInstruction.operation()) {
            case "turn on" -> _ -> 1;
            case "turn off" -> _ -> 0;
            case "toggle" -> v -> v == 0 ? 1 : 0;
            default -> throw new IllegalStateException("Unknown operation: " + litInstruction.operation());
        });
    }

    long part2() {
        return solution(litInstruction -> switch (litInstruction.operation()) {
            case "turn on" -> v -> v + 1;
            case "turn off" -> v -> v > 0 ? v - 1 : 0;
            case "toggle" -> v -> v + 2;
            default -> throw new IllegalStateException("Unknown operation: " + litInstruction.operation());
        });
    }

    private long solution(Function<Input.LitInstruction, IntFunction<Integer>> operationFunction) {
        int[][] lights = new int[1000][];
        for (int i = 0; i < lights.length; i++) {
            lights[i] = new int[1000];
        }
        for (Input.LitInstruction litInstruction : litInstructions) {
            IntFunction<Integer> operation = operationFunction.apply(litInstruction);
            for (int x = litInstruction.leftTopX(); x <= litInstruction.rightDownX(); x++) {
                for (int y = litInstruction.leftTopY(); y <= litInstruction.rightDownY(); y++) {
                    lights[x][y] = operation.apply(lights[x][y]);
                }
            }
        }
        return Arrays.stream(lights)
                .flatMapToInt(Arrays::stream)
                .mapToLong(i -> (long) i)
                .sum();
    }
}


