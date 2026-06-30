package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Day7 {
    private final List<String> input;


    public Day7() throws IOException {
        this.input = Input.day7();
    }

    long part1() {
        return input.stream()
                .map(IPv7::tryParse)
                .map(Optional::orElseThrow)
                .filter(IPv7::supportsTLS)
                .count();
    }

    long part2() {
        return input.stream()
                .map(IPv7::tryParse)
                .map(Optional::orElseThrow)
                .filter(IPv7::supportsSSL)
                .count();
    }

    private record IPv7(List<String> supernetSequences, List<String> hypernetSequences) {
        public static Optional<IPv7> tryParse(String line) {
            char[] lineCharArray = line.toCharArray();
            List<String> ipParts = new ArrayList<>();
            List<String> hypernetSequences = new ArrayList<>();
            boolean inHypernetSequence = false;
            StringBuilder sb = new StringBuilder();
            for (char c : lineCharArray) {
                if (c == '[') {
                    inHypernetSequence = true;
                    if (!sb.isEmpty()) {
                        ipParts.add(sb.toString());
                        sb = new StringBuilder();
                    }
                } else if (c == ']') {
                    inHypernetSequence = false;
                    if (!sb.isEmpty()) {
                        hypernetSequences.add(sb.toString());
                        sb = new StringBuilder();
                    }
                } else if (c >= 'a' && c <= 'z') {
                    sb.append(c);
                } else {
                    return Optional.empty();
                }
            }
            if (!sb.isEmpty()) {
                if (inHypernetSequence) {
                    hypernetSequences.add(sb.toString());
                } else {
                    ipParts.add(sb.toString());
                }
            }
            return Optional.of(new IPv7(ipParts, hypernetSequences));
        }

        public boolean supportsTLS() {
            return supernetSequences.stream().anyMatch(this::containsAbba) && hypernetSequences.stream().noneMatch(this::containsAbba);
        }

        public boolean supportsSSL() {
            return supernetSequences.stream()
                    .map(this::findABAs)
                    .flatMap(List::stream)
                    .map(this::convertToBAB)
                    .anyMatch(bab -> hypernetSequences.stream().anyMatch(hypernetSequence -> hypernetSequence.contains(bab)));
        }

        private boolean containsAbba(String value) {
            char[] valueCharArray = value.toCharArray();
            for (int i = 0; i < valueCharArray.length - 3; i++) {
                if (valueCharArray[i] == valueCharArray[i + 3] && valueCharArray[i + 1] == valueCharArray[i + 2]
                        && valueCharArray[i] != valueCharArray[i + 1]) {
                    return true;
                }
            }
            return false;
        }

        private List<String> findABAs(String value) {
            char[] valueCharArray = value.toCharArray();
            List<String> abas = new ArrayList<>();
            for (int i = 0; i < valueCharArray.length - 2; i++) {
                if (valueCharArray[i] == valueCharArray[i + 2] && valueCharArray[i] != valueCharArray[i + 1]) {
                    abas.add(valueCharArray[i] + "" + valueCharArray[i + 1] + valueCharArray[i]);
                }
            }
            return abas;
        }

        private String convertToBAB(String aba) {
            return aba.charAt(1) + "" + aba.charAt(0) + aba.charAt(1);
        }
    }
}
