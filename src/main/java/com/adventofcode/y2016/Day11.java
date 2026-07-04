package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day11 {
    private final List<String> input;


    public Day11() throws IOException {
        this.input = Input.day11();
    }

    long part1() {
        Arrangement arrangement = parse();
        Set<Node> nodes = new HashSet<>();
        Node node = new Node(arrangement, 0);
        nodes.add(node);
//        return walk(nodes, node, 100);
        return walk2(node);
    }

    private int walk2(Node firstNode) {
        Set<Node> allNodes = new HashSet<>();
        allNodes.add(firstNode);
        Set<Node> nodesCreatedInStep = new HashSet<>();
        List<Node> workingNodes = new ArrayList<>();
        workingNodes.add(firstNode);
        while (!workingNodes.isEmpty()) {
            System.out.println("allNodes.size() = " + allNodes.size());
            System.out.println("workingNodes.size() = " + workingNodes.size());
            System.out.println("current cost = " + workingNodes.getFirst().cost);
            System.out.println();
            for (int i = 0; i < workingNodes.size(); i++) {
                Node node = workingNodes.get(i);
                if (node.arrangement.allElementsAtLastFloor()) {
                    return node.cost;
                }
                Set<Node> newChildrenOfNode = getNewChildrenOfNode(allNodes, node);
                nodesCreatedInStep.addAll(newChildrenOfNode);
                allNodes.addAll(newChildrenOfNode);
            }
            workingNodes.clear();
            workingNodes.addAll(nodesCreatedInStep);
            nodesCreatedInStep.clear();
        }
        throw new RuntimeException("No solution found");
    }

    private Set<Node> getNewChildrenOfNode(Set<Node> allNodes, Node node) {
        return getElevatorCargoCombinations(node.arrangement.elevatorFloorElements())
                .stream()
                .flatMap(cargo -> Stream.of(
                        node.arrangement.canMoveDown() ? node.arrangement.moveDown(cargo) : null,
                        node.arrangement.canMoveUp() ? node.arrangement.moveUp(cargo) : null))
                .filter(Objects::nonNull)
                .filter(Arrangement::allElementsIntact)
                .map(newArrangement -> {
                    Node newChild = new Node(newArrangement, node.cost + 1);
                    return allNodes.contains(newChild) ? null : newChild;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private static int level = 0;

    private int walk(Set<Node> nodes, Node node, int minCostSoFar) {
        level++;
        try {
            if (node.arrangement.allElementsAtLastFloor()) {
                debug("1", node, nodes);
                return node.cost;
            }
            if (node.cost >= minCostSoFar) {
                debug("2", node, nodes);
                return Integer.MAX_VALUE;
            }
            if (node.children.isEmpty()) {
                debug("3", node, nodes);
                List<Node> children = getElevatorCargoCombinations(node.arrangement.elevatorFloorElements())
                        .stream()
                        .flatMap(cargo -> Stream.of(
                                node.arrangement.canMoveDown() ? node.arrangement.moveDown(cargo) : null,
                                node.arrangement.canMoveUp() ? node.arrangement.moveUp(cargo) : null))
                        .filter(Objects::nonNull)
                        .filter(Arrangement::allElementsIntact)
                        .map(newArrangement -> {
                            Node newChild = new Node(newArrangement, node.cost + 1);
                            Optional<Node> existingNode = nodes.stream().filter(newChild::equals).findAny();
                            if (existingNode.isEmpty()) nodes.add(newChild);
                            return existingNode.map(exisitingChild -> {
                                if (exisitingChild.cost > newChild.cost) {
                                    exisitingChild.cost = newChild.cost;
                                    return exisitingChild;
                                }
                                return exisitingChild;
                            }).orElse(newChild);
                        })
                        .toList();
                children.forEach(node::addChild);
                int minCostFromThisNode = Integer.MAX_VALUE;
                for (Node child : children) {
                    minCostFromThisNode = Math.min(minCostFromThisNode, walk(nodes, child, minCostSoFar));
                    minCostSoFar = Math.min(minCostSoFar, minCostFromThisNode);
                }
                return minCostFromThisNode;
            } else {
                debug("4", node, nodes);
                int minCostFromThisNode = Integer.MAX_VALUE;
                for (Node child : node.children) {
                    if (node.cost < child.cost - 1) {
                        child.cost = node.cost + 1;
                        minCostFromThisNode = Math.min(minCostFromThisNode, walk(nodes, child, minCostSoFar));
                        minCostSoFar = Math.min(minCostSoFar, minCostFromThisNode);
                    }
                }
                return minCostFromThisNode;
            }
        } finally {
            level--;
        }
    }

    private static void debug(String discriminator, Node node, Collection<Node> nodes) {
        System.out.printf("option %s, level = %d, node.cost = %d, nodes created = %d%n",
                discriminator, level, node.cost, nodes.size());
    }

    private List<List<String>> getElevatorCargoCombinations(List<String> floorElements) {
        List<List<String>> cargoCombinations = new ArrayList<>();
        for (int i = 0; i < floorElements.size() - 1; i++) {
            cargoCombinations.add(List.of(floorElements.get(i)));
            for (int j = i + 1; j < floorElements.size(); j++) {
                cargoCombinations.add(List.of(floorElements.get(i), floorElements.get(j)));
            }
        }
        cargoCombinations.add(List.of(floorElements.getLast()));
        return cargoCombinations;
    }

    private Arrangement parse() {
        Map<Integer, List<String>> floorsMap = new HashMap<>();
        Pattern floorPattern = Pattern.compile("The ([a-z]+) floor contains (.*)\\.");
        Pattern equipmentPattern = Pattern.compile("a ([a-z]+)((?:-compatible microchip| generator))");
        for (String line : input) {
            Matcher matcher = floorPattern.matcher(line);
            if (!matcher.find()) throw new IllegalArgumentException("Invalid input");
            int floor = getNumber(matcher.group(1));
            floorsMap.putIfAbsent(floor, new ArrayList<>());
            Matcher matcher1 = equipmentPattern.matcher(matcher.group(2));
            while (matcher1.find()) {
                floorsMap.get(floor).add(
                        ((Character) matcher1.group(1).charAt(0)).toString().toUpperCase()
                                + (matcher1.group(2).equals(" generator") ? "G" : "M")
                );
            }
        }
        return new Arrangement(floorsMap,
                1,
                floorsMap.keySet().stream().mapToInt(i -> i).min().orElseThrow(),
                floorsMap.keySet().stream().mapToInt(i -> i).max().orElseThrow());
    }

    private int getNumber(String orderNumber) {
        return switch (orderNumber) {
            case "first" -> 1;
            case "second" -> 2;
            case "third" -> 3;
            case "fourth" -> 4;
            default -> throw new IllegalArgumentException("Invalid input");
        };
    }

    long part2() {
        return 0L;
    }

    private class Node {
        private final Arrangement arrangement;
        private List<Node> children = new ArrayList<>();
        private int cost;

        private Node(Arrangement arrangement, int cost) {
            this.arrangement = arrangement;
            this.cost = cost;
        }

        public Arrangement getArrangement() {
            return arrangement;
        }

        public int getCost() {
            return cost;
        }

        public Node setCost(int cost) {
            this.cost = cost;
            return this;
        }

        public List<Node> getChildren() {
            return children;
        }

        public void addChild(Node node) {
            children.add(node);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(arrangement, node.arrangement);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(arrangement);
        }
    }

    private record Arrangement(Map<Integer, List<String>> floorsMap,
                               int elevatorFloor,
                               int firstFloor,
                               int lastFloor) {

        @Override
        @NonNull
        public String toString() {
            List<String> sorted = floorsMap.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .sorted()
                    .toList();
            return floorsMap.entrySet().stream()
                    .sorted(Map.Entry.<Integer, List<String>>comparingByKey().reversed())
                    .map(entry -> "F" + entry.getKey() + (entry.getKey().equals(elevatorFloor) ? "\tE\t" : "\t.\t") + getString(sorted, entry.getValue()))
                    .collect(Collectors.joining("\n"));
        }

        private String getString(List<String> elements, List<String> value) {
            return elements
                    .stream()
                    .map(element -> value.contains(element) ? element : ".")
                    .collect(Collectors.joining("\t"));
        }

        public boolean allElementsIntact() {
            return floorsMap()
                    .values()
                    .stream()
                    .allMatch(this::floorElementsIntact);
        }

        private boolean floorElementsIntact(List<String> floorElements) {
            Set<Character> floorGenerators = new HashSet<>();
            Set<Character> floorMicrochips = new HashSet<>();
            floorElements.forEach(floorElement -> {
                Set<Character> collector;
                if (floorElement.charAt(1) == 'G') {
                    collector = floorGenerators;
                } else {
                    collector = floorMicrochips;
                }
                collector.add(floorElement.charAt(0));
            });
            if (floorGenerators.isEmpty()) return true;
            return floorGenerators.containsAll(floorMicrochips);
        }

        public boolean allElementsAtLastFloor() {
            return floorsMap.entrySet().stream()
                    .allMatch(entry -> (entry.getKey() == lastFloor && !entry.getValue().isEmpty()) || (entry.getKey() != lastFloor && entry.getValue().isEmpty()));
        }

        public List<String> elevatorFloorElements() {
            return floorsMap.get(elevatorFloor);
        }

        public Arrangement moveUp(List<String> elements) {
            return moveToFloor(elements, elevatorFloor + 1);
        }

        public boolean canMoveUp() {
            return elevatorFloor < lastFloor;
        }

        public Arrangement moveDown(List<String> elements) {
            return moveToFloor(elements, elevatorFloor - 1);
        }

        public boolean canMoveDown() {
            return elevatorFloor > firstFloor;
        }

        private Arrangement moveToFloor(List<String> elements, int nextFloor) {
            if (nextFloor > lastFloor || nextFloor < firstFloor) throw new RuntimeException();
            if (elements.isEmpty()) throw new RuntimeException();
            Map<Integer, List<String>> newFloorsMap = new HashMap<>();
            floorsMap.forEach((key, value) -> {
                ArrayList<String> newFloorElements = new ArrayList<>(value);
                if (key == elevatorFloor) newFloorElements.removeAll(elements);
                if (key == nextFloor) newFloorElements.addAll(elements);
                newFloorsMap.put(key, newFloorElements);
            });
            return new Arrangement(newFloorsMap, nextFloor, firstFloor, lastFloor);
        }
    }
}
