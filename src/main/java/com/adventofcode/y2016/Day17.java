package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Day17 {

    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
    private static final int UP_INDEX = 0;
    private static final int DOWN_INDEX = 1;
    private static final int LEFT_INDEX = 2;
    private static final int RIGHT_INDEX = 3;
    private final Set<Character> OPEN_DOOR_CHARS = Set.of('b', 'c', 'd', 'e', 'f');
    private final MessageDigest md = MessageDigest.getInstance("MD5");
    private final String passcode;
    private final String[] shortestAndLongestPath;

    public Day17() throws IOException, NoSuchAlgorithmException {
        this.passcode = Input.day17();
        shortestAndLongestPath = work();
    }

    String[] work() {
        List<Node> nodes = new ArrayList<>();
        Node startNode = new Node("", UP_INDEX, UP_INDEX, new ArrayList<>());
        nodes.add(startNode);
        return walk(nodes, startNode, null, null);
    }

    private String[] walk(List<Node> nodes, Node node, String minSolution, String maxSolution) {
        if (node.x == 3 && node.y == 3) {
            return compileResult(node, minSolution, maxSolution);
        }
        List<Node> newNodes = new ArrayList<>();
        char[] openDoorsMarker = firstFourCharsOfMd5(passcode + node.path, md);
        if (OPEN_DOOR_CHARS.contains(openDoorsMarker[UP_INDEX]) && node.y > 0) {
            Node newNode = new Node(node.path + "U", node.x, node.y - 1, new ArrayList<>());
            newNodes.add(newNode);
        }
        if (OPEN_DOOR_CHARS.contains(openDoorsMarker[DOWN_INDEX]) && node.y < 3) {
            Node newNode = new Node(node.path + "D", node.x, node.y + 1, new ArrayList<>());
            newNodes.add(newNode);
        }
        if (OPEN_DOOR_CHARS.contains(openDoorsMarker[LEFT_INDEX]) && node.x > 0) {
            Node newNode = new Node(node.path + "L", node.x - 1, node.y, new ArrayList<>());
            newNodes.add(newNode);
        }
        if (OPEN_DOOR_CHARS.contains(openDoorsMarker[RIGHT_INDEX]) && node.x < 3) {
            Node newNode = new Node(node.path + "R", node.x + 1, node.y, new ArrayList<>());
            newNodes.add(newNode);
        }
        nodes.addAll(newNodes);
        node.children.addAll(newNodes);
        for (Node newNode : newNodes) {
            String[] s = walk(nodes, newNode, minSolution, maxSolution);
            if (s[0] != null && (minSolution == null || minSolution.length() > s[0].length())) {
                minSolution = s[0];
            }
            if (s[1] != null && (maxSolution == null || maxSolution.length() < s[1].length())) {
                maxSolution = s[1];
            }
        }
        return new String[]{minSolution, maxSolution};
    }

    private static String @NonNull [] compileResult(Node node, String minSolution, String maxSolution) {
        return new String[]{
                minSolution == null || minSolution.length() > node.path.length()
                        ? node.path
                        : minSolution,
                maxSolution == null || maxSolution.length() < node.path.length()
                        ? node.path
                        : maxSolution};
    }

    String part1() {
        return shortestAndLongestPath[0];
    }

    long part2() {
        return shortestAndLongestPath[1].length();
    }

    private record Node(String path, int x, int y, List<Node> children) {
    }

    private char[] firstFourCharsOfMd5(String value, MessageDigest md) {
        md.reset();
        byte[] hash = md.digest(value.getBytes(StandardCharsets.UTF_8));
        char[] hex = new char[4];
        for (int i = UP_INDEX, j = UP_INDEX; j < 4; i++) {
            int b = hash[i] & 0xff;
            hex[j++] = HEX_DIGITS[b >>> 4];
            hex[j++] = HEX_DIGITS[b & 0x0f];
        }
        return hex;
    }
}
