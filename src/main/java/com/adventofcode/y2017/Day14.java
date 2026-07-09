package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;
import com.google.common.base.Strings;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day14 {
    private final String key;
    private final List<String> knotHashes;

    public Day14() throws IOException {
        this.key = Input.day14();
        this.knotHashes = IntStream.range(0, 128)
                .mapToObj(i -> Day10.knotHash(key + "-" + i)).toList();
    }

    long part1() {
        return knotHashes
                .stream()
                .map(this::toInts)
                .map(Arrays::stream)
                .flatMap(IntStream::boxed)
                .mapToInt(Integer::bitCount)
                .sum();
    }

    long part2() {
        char[][] diskUsageMap = knotHashes
                .stream()
                .map(this::toInts)
                .map(ints -> Arrays.stream(ints)
                        .mapToObj(Integer::toBinaryString)
                        .map(string -> Strings.padStart(string, 32, '0'))
                        .collect(Collectors.joining()))
                .map(String::toCharArray)
                .toArray(char[][]::new);
        List<Coordinate> allUsed = new ArrayList<>();
        for (int i = 0; i < diskUsageMap.length; i++) {
            for (int j = 0; j < diskUsageMap[i].length; j++) {
                if (diskUsageMap[i][j] == '1')
                    allUsed.add(new Coordinate(j, i));
            }
        }
        List<List<Coordinate>> regions = new ArrayList<>();
        Set<Coordinate> region = new HashSet<>();
        List<Coordinate> toVisit = new ArrayList<>();
        while (!allUsed.isEmpty()) {
            toVisit.add(allUsed.removeFirst());
            while (!toVisit.isEmpty()) {
                Coordinate coordinate = toVisit.removeFirst();
                region.add(coordinate);
                allUsed.remove(coordinate);
                Stream.of(coordinate.up(), coordinate.down(), coordinate.left(), coordinate.right())
                        .filter(allUsed::contains)
                        .forEach(c -> {
                            region.add(c);
                            allUsed.remove(c);
                            toVisit.add(c);
                        });
            }
            regions.add(new ArrayList<>(region));
            region.clear();
        }
        return regions.size();
    }

    private int[] toInts(@NonNull String knotHash) {
        int[] result = new int[4];
        char[] hashChars = knotHash.toCharArray();
        for (int i = 0; i < hashChars.length; i += 8) {
            for (int j = 0; j < 8; j++) {
                int value = charToNumber(hashChars[i + j]);
                result[i / 8] |= (value << 4 * (7 - j));
            }
        }
        return result;
    }

    private static int charToNumber(char c) {
        return (c - '0') < 10 ? c - '0' : c - 'a' + 10;
    }

    public record Coordinate(int x, int y) {
        public Coordinate left() {
            return new Coordinate(x - 1, y);
        }

        public Coordinate right() {
            return new Coordinate(x + 1, y);
        }

        public Coordinate up() {
            return new Coordinate(x, y - 1);
        }

        public Coordinate down() {
            return new Coordinate(x, y + 1);
        }
    }
}
