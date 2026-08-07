package com.adventofcode.y2019.intcode.commands;

import java.util.Queue;

public record CommandInput(int[] memory, int instructionPointer, Queue<Integer> input) {
}
