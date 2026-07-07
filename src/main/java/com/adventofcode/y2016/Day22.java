package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day22 {
    private final List<Node> nodes;


    public Day22() throws IOException {
        this.nodes = Input.day22();
    }

    long part1() {
        long viablePairs = 0;
        for (int i = 0; i < nodes.size(); i++) {
            Node nodeA = nodes.get(i);
            if (nodeA.used() == 0) {
                continue;
            }
            for (int j = 0; j < nodes.size(); j++) {
                if (i == j) {
                    continue;
                }
                Node nodeB = nodes.get(j);
                if (nodeA.used() <= nodeB.available()) {
                    viablePairs++;
                }
            }
        }
        return viablePairs;
    }

    long part2() {
        int maxX = nodes.stream().mapToInt(Node::x).max().orElseThrow();
        int maxY = nodes.stream().mapToInt(Node::y).max().orElseThrow();
        Node[][] grid = new Node[maxY + 1][maxX + 1];
        nodes.forEach(node -> grid[node.y()][node.x()] = node);
        Node goalNode = grid[0][grid[0].length - 1];
        Set<Node> emptyNodes = nodes.stream()
                .filter(node -> node.available() > goalNode.used())
                .collect(Collectors.toSet());
        int shortestPathToMakeGap = emptyNodes.stream()
                .mapToInt(emptyNode -> findShortestPath(grid, emptyNode, grid[0][grid[0].length - 2]))
                .filter(i -> i != Integer.MAX_VALUE)
                .min()
                .orElseThrow();
        if (!canMove(goalNode, grid[0][grid[0].length - 2]))
            throw new RuntimeException();
        shortestPathToMakeGap++;
        for (int i = grid[0].length - 2; i > 0; i--)
            shortestPathToMakeGap += moveOneLeft(goalNode.used(), grid[0][i], grid);

        return shortestPathToMakeGap;
    }

    private int moveOneLeft(int payload, Node node, Node[][] grid) {
        int moves = 0;
        if (canMove(grid[node.y() + 1][node.x() + 1], grid[node.y()][node.x() + 1]))
            moves++;
        else throw new RuntimeException();
        if (canMove(grid[node.y() + 1][node.x()], grid[node.y() + 1][node.x() + 1]))
            moves++;
        else throw new RuntimeException();
        if (canMove(grid[node.y() + 1][node.x() - 1], grid[node.y() + 1][node.x()]))
            moves++;
        else throw new RuntimeException();
        if (canMove(grid[node.y()][node.x() - 1], grid[node.y() + 1][node.x() - 1]))
            moves++;
        else throw new RuntimeException();
        if (grid[node.y()][node.x() - 1].size() >= payload)
            moves++;
        else throw new RuntimeException();
        return moves;
    }

    private int findShortestPath(Node[][] grid, Node from, Node to) {
        Set<Node> visited = new HashSet<>();
        int[][] distances = new int[grid.length][grid[0].length];
        Arrays.stream(distances).forEach(row -> Arrays.fill(row, Integer.MAX_VALUE));
        distances[from.y()][from.x()] = 0;
        Set<Node> queue = new HashSet<>();
        queue.add(from);
        while (!queue.isEmpty()) {
            Node node = queue.stream().min(Comparator.comparing(n -> distance(distances, n))).orElseThrow();
            queue.remove(node);
            List<Node> neighbors = getNeighbors(grid, node, visited);
            for (Node neighbor : neighbors) {
                if (distance(distances, neighbor) > distance(distances, node) + 1) {
                    setDistance(distances, neighbor, distance(distances, node) + 1);
                    queue.add(neighbor);
                }
            }
        }
        return distance(distances, to);
    }

    int distance(int[][] distances, Node node) {
        return distances[node.y()][node.x()];
    }

    void setDistance(int[][] distances, Node node, int distance) {
        distances[node.y()][node.x()] = distance;
    }

    private static List<Node> getNeighbors(Node[][] grid, Node from, Set<Node> visited) {
        return Stream.of(from.x() > 0 ? grid[from.y()][from.x() - 1] : null,
                        from.x() < grid[from.y()].length - 1 ? grid[from.y()][from.x() + 1] : null,
                        from.y() > 0 ? grid[from.y() - 1][from.x()] : null,
                        from.y() < grid.length - 1 ? grid[from.y() + 1][from.x()] : null
                )
                .filter(Objects::nonNull)
                .filter(Predicate.not(visited::contains))
                .filter(node -> canMove(from, node))
                .toList();
    }

    private static boolean canMove(Node to, Node from) {
        return from.used() <= to.size();
    }

    public record Node(int x, int y, int size, int used, int available) {
        private static final Pattern NODE_PATTERN = Pattern.compile("/dev/grid/node-x(\\d+)-y(\\d+)\\s+(\\d+)T\\s+(\\d+)T\\s+(\\d+)T\\s+(\\d+)%");

        public static Node parse(String line) {
            var matcher = NODE_PATTERN.matcher(line);
            if (matcher.matches()) {
                return new Node(Integer.parseInt(matcher.group(1)),
                        Integer.parseInt(matcher.group(2)),
                        Integer.parseInt(matcher.group(3)),
                        Integer.parseInt(matcher.group(4)),
                        Integer.parseInt(matcher.group(5)));
            }
            throw new IllegalArgumentException("Invalid node line: " + line);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return x == node.x && y == node.y && size == node.size && used == node.used && available == node.available;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

}
