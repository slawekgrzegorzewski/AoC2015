package com.adventofcode;

import com.adventofcode.input.Input;

import java.io.IOException;

public class Day18 {

    private final char[][] lights;

    public Day18() throws IOException {
        lights = Input.day18();
    }

    long part1() {
        return animate(false);
    }

    long part2() {
        return animate(true);
    }

    private int animate(boolean powerOnCorners) {
        char[][] newLights = copy(lights);
        if (powerOnCorners) {
            powerOnLightsOnCorners(newLights);
        }
        for (int i = 0; i < 100; i++) {
            newLights = nextTurn(newLights);
            if (powerOnCorners) {
                powerOnLightsOnCorners(newLights);
            }
        }
        int poweredOn = 0;
        for (char[] newLight : newLights) {
            for (char c : newLight) {
                if (c == '#') poweredOn++;
            }
        }
        return poweredOn;
    }

    private void powerOnLightsOnCorners(char[][] newLights) {
        newLights[0][0] = '#';
        newLights[0][newLights[0].length - 1] = '#';
        newLights[newLights.length - 1][0] = '#';
        newLights[newLights.length - 1][newLights[0].length - 1] = '#';
    }

    private char[][] copy(char[][] lights) {
        char[][] copy = new char[lights.length][lights[0].length];
        for (int i = 0; i < lights.length; i++) {
            System.arraycopy(lights[i], 0, copy[i], 0, lights[0].length);
        }
        return copy;
    }

    private char[][] nextTurn(char[][] lights) {
        char[][] newLights = new char[lights.length][lights[0].length];
        for (int i = 0; i < lights.length; i++) {
            for (int j = 0; j < lights[0].length; j++) {
                int poweredOnNeighbours = countPoweredOnNeighbours(i, j, lights);
                if (lights[i][j] == '#') {
                    newLights[i][j] = (poweredOnNeighbours == 2 || poweredOnNeighbours == 3) ? '#' : '.';
                } else if (lights[i][j] == '.') {
                    newLights[i][j] = poweredOnNeighbours == 3 ? '#' : '.';
                }
            }
        }
        return newLights;
    }

    private int countPoweredOnNeighbours(int x, int y, char[][] lights) {
        int count = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i == x && j == y) continue;
                if (i >= 0 && j >= 0 && i < lights.length && j < lights[0].length && lights[i][j] == '#')
                    count++;
            }
        }
        return count;
    }

    private void print(char[][] lights) {
        for (char[] row : lights) {
            System.out.println(new String(row));
        }
        System.out.println();
    }
}