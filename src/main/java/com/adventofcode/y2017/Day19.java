package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;

public class Day19 {
    private final char[][] map;


    public Day19() throws IOException {
        this.map = Input.day19();
    }

    String part1() {
        return (String) navigate()[0];
    }

    long part2() {
        return (int) navigate()[1];
    }

    private Object[] navigate() {
        int y = 0;
        int x = 0;
        Direction direction = Direction.DOWN;
        for (; x < map[y].length; x++) {
            if (map[y][x] == '|') {
                break;
            }
        }
        int steps = 0;
        StringBuilder sb = new StringBuilder();
        char currentChar = map[y][x];
        while (true) {
            while (currentChar != '+' && currentChar != ' ') {
                switch (direction) {
                    case DOWN -> y++;
                    case UP -> y--;
                    case LEFT -> x--;
                    case RIGHT -> x++;
                }
                if (!inBounds(map, x, y)) {
                    break;
                }
                steps++;
                currentChar = map[y][x];
                if ((currentChar >= 'a' && currentChar <= 'z') || (currentChar >= 'A' && currentChar <= 'Z')) {
                    sb.append(currentChar);
                }
            }
            if (direction == Direction.DOWN || direction == Direction.UP) {
                if (inBounds(map, x - 1, y) && map[y][x - 1] != ' ') {
                    steps++;
                    x--;
                    direction = Direction.LEFT;
                } else if (inBounds(map, x + 1, y) && map[y][x + 1] != ' ') {
                    steps++;
                    x++;
                    direction = Direction.RIGHT;
                } else {
                    break;
                }
            } else {
                if (inBounds(map, x, y - 1) && map[y - 1][x] != ' ') {
                    steps++;
                    y--;
                    direction = Direction.UP;
                } else if (inBounds(map, x, y + 1) && map[y + 1][x] != ' ') {
                    steps++;
                    y++;
                    direction = Direction.DOWN;
                } else {
                    break;
                }
            }
            currentChar = map[y][x];
            if ((currentChar >= 'a' && currentChar <= 'z') || (currentChar >= 'A' && currentChar <= 'Z')) {
                sb.append(currentChar);
            }
        }
        return new Object[]{sb.toString(), steps};
    }

    private boolean inBounds(char[][] map, int x, int y) {
        return y >= 0 && y < map.length && x >= 0 && x < map[y].length;
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}
