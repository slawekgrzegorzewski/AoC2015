package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;

import java.io.IOException;

import static com.adventofcode.utils.Utils.lcm;

public class Day16 {
    private final int[] signal;

    public Day16() throws IOException {
        this.signal = Input.day16();
    }

    int[] part1() {
        int[] fftResult = fft(this.signal, 100, 0, this.signal.length);
        int[] result = new int[8];
        for (int i = 0; i < 8; i++) {
            result[i] = fftResult[i];
        }
        return result;
    }

    long part2() {
        int[] realSignal = new int[signal.length * 10_000];
        for (int i = 0; i < 10_000; i++) {
            System.arraycopy(signal, 0, realSignal, i * signal.length, signal.length);
        }
        int offset = 0;
        for (int i = 0; i < 7; i++) {
            offset = offset * 10 + signal[i];
        }
        int[] fftResult = fft(realSignal, 100, 0, realSignal.length);
        return 0L;
    }

    public static int[] fft(int[] input, int phases, int fromIndexInclusive, int toIndexExclusive) {
        for (int i = 0; i < phases; i++)
            input = fftSinglePhase(input, fromIndexInclusive, toIndexExclusive);
        return input;
    }

    private static int[] fftSinglePhase(int[] input, int fromIndexInclusive, int toIndexExclusive) {
        int[] pattern = new int[]{0, 1, 0, -1};
        int[] output = new int[toIndexExclusive - fromIndexInclusive];
        for (int outputIndex = fromIndexInclusive; outputIndex < toIndexExclusive; outputIndex++) {
            int value = 0;
            int cycleLength = (int)lcm(650, (outputIndex + 1) * 4L);
            for (int inputIndex = 0; inputIndex < Math.min(cycleLength, input.length); inputIndex++) {
                value += input[inputIndex] * getPatternElementForPosition(outputIndex, inputIndex, pattern);
            }
            int fullCycles = input.length / cycleLength;
            value *= fullCycles;
            for (int inputIndex = fullCycles * cycleLength; inputIndex < input.length; inputIndex++) {
                value += input[inputIndex] * getPatternElementForPosition(outputIndex, inputIndex, pattern);
            }
            output[outputIndex - fromIndexInclusive] = Math.abs(value) % 10;
        }
        return output;
    }

    public static int getPatternElementForPosition(int outputIndex, int inputIndex, int[] pattern) {
        int repeat = outputIndex + 1;
        int indexFromPattern = inputIndex + 1;
        return pattern[(indexFromPattern / repeat) % pattern.length];
    }
}
