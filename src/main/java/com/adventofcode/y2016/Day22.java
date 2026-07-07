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
//        printGrid(grid);
        Node goalNode = grid[0][grid[0].length - 1];
        Set<Node> emptyNodes = nodes.stream()
                .filter(node -> node.available() > goalNode.used())
                .collect(Collectors.toSet());
        int shortestPathToMakeGap = emptyNodes.stream()
                .map(emptyNode -> findShortestPath(grid, emptyNode, grid[0][grid[0].length - 2]))
                .filter(OptionalInt::isPresent)
                .mapToInt(OptionalInt::getAsInt)
                .min()
                .orElseThrow();
        long count = nodes.stream()
                .filter(node -> node.size() > goalNode.used())
                .count();
        System.out.println(count);

        return 0L;
    }

    private OptionalInt findShortestPath(Node[][] grid, Node from, Node to) {
        Set<Node> visited = new HashSet<>();
        int shortestPath = walk(0, from, to, grid, visited);
        return OptionalInt.empty();
    }

    int shortestPathSoFar = 100;

    private int walk(int currentPathLength, Node from, Node to, Node[][] grid, Set<Node> visited) {
        if (shortestPathSoFar != Integer.MAX_VALUE && currentPathLength >= shortestPathSoFar)
            return Integer.MAX_VALUE;
        if (from.equals(to)) {
            if (shortestPathSoFar > currentPathLength) {
                shortestPathSoFar = currentPathLength;
                System.out.println("shortestPathSoFar = " + shortestPathSoFar);
            }
            return currentPathLength;
        }
        visited.add(from);
        try {
            return Stream.of(from.x() > 0 ? grid[from.y()][from.x() - 1] : null,
                            from.x() < grid[from.y()].length - 1 ? grid[from.y()][from.x() + 1] : null,
                            from.y() > 0 ? grid[from.y() - 1][from.x()] : null,
                            from.y() < grid.length - 1 ? grid[from.y() + 1][from.x()] : null
                    )
                    .filter(Objects::nonNull)
                    .filter(Predicate.not(visited::contains))
                    .filter(node -> node.used() <= from.size())
                    .mapToInt(node -> walk(currentPathLength + 1, node, to, grid, visited))
                    .min()
                    .orElse(Integer.MAX_VALUE);
        } finally {
            visited.remove(from);
        }
    }

    private void printGrid(Node[][] grid) {
        String representation = Arrays.stream(grid)
                .map(row -> Arrays.stream(row)
                        .map(node -> node == null ? "\t" : node.used() + "/" + node.size())
                        .collect(Collectors.joining("\t--\t")))
                .collect(Collectors.joining("\n"));
        System.out.println(representation);
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
