package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day24 {
    private final List<Bridge> allBridges;

    public Day24() throws IOException {
        List<Component> components = new ArrayList<>(Input.day24());
        List<Bridge> bridges = new ArrayList<>();
        Bridge bridge = new Bridge(new ArrayList<>(), 0);
        searchForNextLinks(bridge, components, bridges);
        allBridges = bridges;
    }

    long part1() {
        return allBridges.stream()
                .mapToLong(Bridge::strength)
                .max()
                .orElseThrow();
    }

    long part2() {
        int maxBridgeLength = allBridges.stream()
                .mapToInt(bridge -> bridge.components().size())
                .max()
                .orElseThrow();
        return allBridges.stream()
                .filter(bridge -> bridge.components().size() == maxBridgeLength)
                .mapToLong(Bridge::strength)
                .max()
                .orElseThrow();
    }

    private void searchForNextLinks(Bridge bridge, List<Component> components, List<Bridge> bridges) {
        List<Component> possibleComponents = components.stream().filter(c -> bridge.linkToConnect == c.a || bridge.linkToConnect == c.b).toList();
        if (possibleComponents.isEmpty())
            bridges.add(new Bridge(new ArrayList<>(bridge.components()), bridge.linkToConnect()));
        for (Component possibleComponent : possibleComponents) {
            components.remove(possibleComponent);
            bridge.components().add(possibleComponent);
            searchForNextLinks(new Bridge(bridge.components(), possibleComponent.a() == bridge.linkToConnect() ? possibleComponent.b() : possibleComponent.a()), components, bridges);
            components.add(possibleComponent);
            bridge.components().remove(possibleComponent);
        }
    }

    public record Component(int a, int b) {
    }

    public record Bridge(List<Component> components, int linkToConnect) {
        public long strength() {
            return components.stream()
                    .mapToLong(c -> c.a + c.b)
                    .sum();
        }
    }
}
