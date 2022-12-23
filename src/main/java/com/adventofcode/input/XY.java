package com.adventofcode.input;

import org.jetbrains.annotations.NotNull;

public record XY(long x, long y) {
    public XY moveUp() {
        return new XY(x, y + 1);
    }

    public XY moveDown() {
        return new XY(x, y - 1);
    }

    public XY moveLeft() {
        return new XY(x - 1, y);
    }

    public XY moveRight() {
        return new XY(x + 1, y);
    }

    @NotNull
    public XY nextPosition(char c) {
        return switch (c) {
            case '>' -> moveRight();
            case '<' -> moveLeft();
            case '^' -> moveUp();
            case 'v' -> moveDown();
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
    }
}
