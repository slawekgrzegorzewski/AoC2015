package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day8 {
    private final List<Integer> tree;


    public Day8() throws IOException {
        this.tree = Input.day8();
    }

    long part1() {
        return getNode(0).node.sumMetadata();
    }

    long part2() {
        return getNode(0).node.value();
    }

    private NodeInListInfo getNode(int nodeStartIndex) {
        int currentIndex = nodeStartIndex;
        int childrenCount = tree.get(currentIndex);
        int metadataCount = tree.get(currentIndex + 1);
        List<Node> children = new ArrayList<>();
        currentIndex += 2;
        for (int i = 0; i < childrenCount; i++) {
            NodeInListInfo nodeInListInfo = getNode(currentIndex);
            currentIndex = nodeInListInfo.listToIndex + 1;
            children.add(nodeInListInfo.node);
        }
        return new NodeInListInfo(
                new Node(children, new ArrayList<>(tree.subList(currentIndex, currentIndex + metadataCount))),
                currentIndex + metadataCount - 1);
    }

    record NodeInListInfo(
            Node node,
            int listToIndex) {
    }

    record Node(List<Node> children, List<Integer> metadata) {

        public long sumMetadata() {
            return children.stream().mapToLong(Node::sumMetadata).sum()
                    + metadata.stream().mapToLong(Long::valueOf).sum();
        }

        public long value() {
            if (children().isEmpty()) {
                return metadata.stream().mapToLong(Long::valueOf).sum();
            } else {
                return metadata.stream()
                        .mapToInt(i -> i - 1)
                        .filter(index -> children.size() > index)
                        .mapToLong(index -> children.get(index).value())
                        .sum();
            }
        }
    }
}
