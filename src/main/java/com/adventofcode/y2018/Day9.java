package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Day9 {
    private final GameInfo gameInfo;

    public Day9() throws IOException {
        this.gameInfo = Input.day9();
    }

    long part1() {
        return winnerScore(gameInfo.lastMarbleValue);
    }

    long part2() {
        return winnerScore(gameInfo.lastMarbleValue * 100);
    }

    private long winnerScore(int lastMarbleValue) {
        Node node = new Node(0);
        node.next = node;
        node.prev = node;

        Map<Integer, Long> scores = new HashMap<>();

        int playerIndex = 0;

        for (int i = 1; i <= lastMarbleValue; i++) {
            int marbleValue = i;
            if (marbleValue % 23 == 0) {
                Node nodeToRemove = node.prev.prev.prev.prev.prev.prev.prev;
                scores.compute(playerIndex, (_, v) -> (v == null ? 0 : v) + marbleValue + nodeToRemove.marbleValue);
                node = removeNode(nodeToRemove);
            } else {
                node = insertNodeAfter(new Node(marbleValue), node);
            }
            playerIndex = (playerIndex + 1) % gameInfo.players;
        }
        return scores.values().stream().mapToLong(Long::valueOf).max().orElse(0L);
    }

    private static Node removeNode(Node nodeToRemove) {
        Node prevNode = nodeToRemove.prev;
        Node nextNode = nodeToRemove.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
        nodeToRemove.next = null;
        nodeToRemove.prev = null;
        return nextNode;
    }

    private static Node insertNodeAfter(Node newNode, Node after) {
        after = after.next;
        newNode.next = after.next;
        newNode.prev = after;
        after.next = newNode;
        newNode.next.prev = newNode;
        return newNode;
    }

    public static class Node {
        int marbleValue;
        Node next;
        Node prev;

        public Node(int marbleValue) {
            this.marbleValue = marbleValue;
        }
    }

    public record GameInfo(int players, int lastMarbleValue) {
        private static final Pattern pattern = Pattern.compile("(\\d+) players; last marble is worth (\\d+) points");

        public static GameInfo parse(String line) {
            var matcher = pattern.matcher(line);
            if (matcher.matches()) {
                return new GameInfo(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
            }
            throw new IllegalArgumentException("Invalid line: " + line);
        }
    }
}
