package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.Deque;
import java.util.Map;

public class Day25 {
    private final TuringMachine turingMachine;


    public Day25() throws IOException {
        this.turingMachine = Input.day25();
    }

    long part1() {
        Node node = new Node(0);
        State state = turingMachine.initialState();
        for (int i = 0; i < turingMachine.diagnosticsAfterSteps(); i++) {
            Action action = state.actions().get(node.value);
            node.value = action.write();
            node = action.moveRight() ? node.next() : node.previous();
            state = turingMachine.states().get(action.nextState());
        }
        while (node.next != null)
            node = node.next;
        int onesCount = 0;
        do {
            if(node.value == 1)
                onesCount++;
            node = node.previous;
        } while (node != null);
        return onesCount;
    }

    long part2() {
        return 0L;
    }

    public record TuringMachine(State initialState, Map<String, State> states, Deque<Integer> tape,
                                int diagnosticsAfterSteps) {
    }

    public record State(String name, Map<Integer, Action> actions) {
    }

    public record Action(int write, boolean moveRight, String nextState) {
    }

    public static class Node {
        int value;
        Node previous;
        Node next;

        public Node(int value) {
            this.value = value;
        }

        public Node next() {
            if (next == null) {
                next = new Node(0);
                next.previous = this;
            }
            return next;
        }

        public Node previous() {
            if (previous == null) {
                previous = new Node(0);
                previous.next = this;
            }
            return previous;
        }
    }
}
