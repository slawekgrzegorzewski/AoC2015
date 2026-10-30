package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;

import java.io.IOException;

public class Day16 {
    private final int[] signal;

    public Day16() throws IOException {
        this.signal = Input.day16();
    }

    int[] part1() {
        return firstNElements(fft(this.signal, 100), 8);
    }

    int[] part2() {
        int[] realSignal = repeat(signal);
        int offset = getNumber(signal);
        if (offset > realSignal.length / 2 + 1) {
            return fft(realSignal, 100, offset);
        } else {
            return firstNElements(fft(realSignal, 100), 8);
        }
    }


    public static int[] fft(int[] input, int phases) {
        int[] result = firstNElements(input, input.length);
        for (int i = 0; i < phases; i++) {
            int[] runningSums = runningSums(result);
            int[] newResult = new int[input.length];
            for (int outputIndex = 0; outputIndex < input.length; outputIndex++) {
                int value = 0;
                int patternElementForPosition = 1;
                for (int inputIndex = outputIndex; inputIndex < input.length; inputIndex += (2 * (outputIndex + 1))) {
                    value += (runningSums[Math.min(inputIndex + outputIndex + 1, input.length)] - runningSums[inputIndex])
                            * patternElementForPosition;
                    patternElementForPosition *= -1;
                }
                newResult[outputIndex] = Math.abs(value) % 10;
            }
            result = newResult;
        }
        return result;
    }

    public static int[] fft(int[] input, int phases, int offset) {
        int[] result = lastNElements(input, input.length - offset);
        for (int i = 0; i < phases; i++) {
            int[] newResult = new int[result.length];
            for (int outputIndex = result.length - 1; outputIndex >= 0; outputIndex--) {
                if (outputIndex == result.length - 1) {
                    newResult[outputIndex] = result[outputIndex];
                } else {
                    newResult[outputIndex] = result[outputIndex] + newResult[outputIndex + 1];
                    newResult[outputIndex] %= 10;
                }
            }
            result = newResult;
        }
        return firstNElements(result, 8);
    }

    private static int getNumber(int[] input) {
        int number = 0;
        for (int i = 0; i < 7; i++) {
            number = number * 10 + input[i];
        }
        return number;
    }

    private static int[] lastNElements(int[] input, int n) {
        int[] result = new int[n];
        System.arraycopy(input, input.length - n, result, 0, n);
        return result;
    }

    private static int[] firstNElements(int[] input, int n) {
        int[] result = new int[n];
        System.arraycopy(input, 0, result, 0, n);
        return result;
    }

    private static int[] repeat(int[] input) {
        int[] realSignal = new int[input.length * 10000];
        for (int i = 0; i < 10000; i++) {
            System.arraycopy(input, 0, realSignal, i * input.length, input.length);
        }
        return realSignal;
    }

    private static int[] runningSums(int[] result) {
        int[] runningSums = new int[result.length + 1];
        int runningSum = 0;
        runningSums[0] = 0;
        for (int j = 0; j < result.length; j++) {
            runningSum += result[j];
            runningSums[j + 1] = runningSum;
        }
        return runningSums;
    }
}
