package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;

import java.io.IOException;
import java.util.*;

public class Day8 {
    private final char[] digits;
    final int width = 25;
    final int height = 6;


    public Day8() throws IOException {
        this.digits = Input.day8();
    }

    long part1() {
        List<Map<Character, Integer>> layers = new ArrayList<>();
        int layerSize = width * height;
        for (int i = 0; i < digits.length; i += layerSize) {
            HashMap<Character, Integer> layerStats = new HashMap<>();
            layers.add(layerStats);
            for (int j = 0; j < layerSize; j++) {
                switch (digits[i + j]) {
                    case '0' -> layerStats.compute('0', (_, v) -> v == null ? 1 : v + 1);
                    case '1' -> layerStats.compute('1', (_, v) -> v == null ? 1 : v + 1);
                    case '2' -> layerStats.compute('2', (_, v) -> v == null ? 1 : v + 1);
                }
            }
        }
        return layers.stream()
                .min(Comparator.comparing(layer -> layer.get('0')))
                .map(layer -> layer.get('1') * layer.get('2'))
                .orElseThrow();
    }

    String part2() {
        char[][] layers = new char[height][width];
        for (char[] layer : layers) {
            Arrays.fill(layer, '2');
        }
        int layerSize = width * height;
        for (int i = 0; i < digits.length; i++) {
            int layerIndex = i / layerSize;
            int positionInLayer = i - layerIndex * layerSize;
            int layerIndexY = positionInLayer / width;
            int layerIndexX = positionInLayer % width;
            if (layers[layerIndexY][layerIndexX] == '2')
                layers[layerIndexY][layerIndexX] = digits[i];
        }
        print(layers);
        return "BCYEF";
    }

    private void print(char[][] layers) {
        for (char[] layer : layers) {
            for (char c : layer) {
                switch (c) {
                    case '0' -> System.out.print(' ');
                    case '1' -> System.out.print('#');
                    case '2' -> throw new IllegalStateException();
                }
            }
            System.out.println();
        }
    }
}
