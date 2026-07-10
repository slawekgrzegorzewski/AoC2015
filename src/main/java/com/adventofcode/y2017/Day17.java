package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;

public class Day17 {
    private final int steps;


    public Day17() throws IOException {
        this.steps = Input.day17();
    }

    long part1() {
        Node node = new Node();
        node.value = 0;
        node.next = node;
        node.prev = node;
        for (int current = 1; current <= 2017; current++) {
            for (int i = 0; i < steps; i++) {
                node = node.next;
            }
            Node newNode = new Node();
            newNode.value = current;
            newNode.next = node.next;
            newNode.next.prev = newNode;
            newNode.prev = node;
            node.next = newNode;
            node = newNode;
        }
        return node.next.value;
    }

    long part2() {
        int length = 1;
        int currentIndex = 0;
        int currentMoment = 1;
        int valueAfterZero = 0;
        for (; currentMoment <= 50_000_000; currentMoment++) {
            currentIndex = (currentIndex + steps) % length;
            if (currentIndex == 0)
                valueAfterZero = currentMoment;
            length++;
            currentIndex++;
        }
        return valueAfterZero;
    }

    public static class Node {
        int value;
        Node next;
        Node prev;
    }
}
