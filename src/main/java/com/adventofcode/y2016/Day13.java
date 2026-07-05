package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.*;

public class Day13 {
    private static final int TARGET_X = 31;
    private static final int TARGET_Y = 39;
    private static final int EXPECTED_COST = 50;
    private final int favouriteNumber;
    private final Set<Node> paths;


    public Day13() throws IOException {
        this.favouriteNumber = Input.day13();
        this.paths = findPaths();
    }

    long part1() {
        return paths.stream().filter(node -> node.x == TARGET_X && node.y == TARGET_Y).findAny().orElseThrow().steps;
    }

    long part2() {
        return paths.stream().filter(node -> node.steps <= EXPECTED_COST).count();
    }

    private Set<Node> findPaths() {
        Node startNode = new Node(1, 1);
        startNode.steps = 0;
        Set<Node> allNodes = new HashSet<>();
        allNodes.add(startNode);
        Set<Node> nodesToProcess = new HashSet<>(allNodes);
        boolean targetNodeFound = false;
        boolean minimumCostReached = false;
        while (!targetNodeFound || !minimumCostReached) {
            Node node = nodesToProcess.stream().min(Comparator.comparing(n -> n.steps)).orElseThrow();
            nodesToProcess.remove(node);
            if (node.x == TARGET_X && node.y == TARGET_Y) targetNodeFound = true;
            if (node.steps > EXPECTED_COST + 1) minimumCostReached = true;
            if (node.neighbours.isEmpty()) {
                createNeighbours(allNodes, node);
                nodesToProcess.addAll(node.neighbours);
            }
            for (Node neighbour : node.neighbours) {
                if (neighbour.steps > node.steps + 1) {
                    neighbour.steps = node.steps + 1;
                }
            }
        }
        return allNodes;
    }

    private void createNeighbours(Set<Node> allNodes, Node node) {
        addNodeIfValid(allNodes, node, node.x + 1, node.y);
        addNodeIfValid(allNodes, node, node.x, node.y - 1);
        addNodeIfValid(allNodes, node, node.x, node.y + 1);
        addNodeIfValid(allNodes, node, node.x - 1, node.y);
    }

    private void addNodeIfValid(Set<Node> allNodes, Node node, int x, int y) {
        if (x >= 0 && y >= 0 && !isWall(x, y)) {
            Node neighbour = new Node(x, y);
            if (allNodes.contains(neighbour)) {
                neighbour = allNodes.stream().filter(neighbour::equals).findAny().orElseThrow();
                allNodes.remove(neighbour);
            }
            node.neighbours.add(neighbour);
            allNodes.add(neighbour);
        }
    }

    private boolean isWall(int x, int y) {
        return Integer.bitCount(x * x + 3 * x + 2 * x * y + y + y * y + favouriteNumber) % 2 == 1;
    }

    private static class Node {
        private final int x;
        private final int y;
        private final List<Node> neighbours;
        private int steps;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
            this.neighbours = new ArrayList<>();
            this.steps = Integer.MAX_VALUE;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
