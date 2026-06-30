package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day4 {
    public static final int ALPHABET_SIZE = ('z' - 'a' + 1);
    private final List<String> input;


    public Day4() throws IOException {
        this.input = Input.day4();
    }

    long part1() {
        return input.stream()
                .map(Room::tryParse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .mapToLong(room -> room.id)
                .sum();
    }

    long part2() {
        return input.stream()
                .map(Room::tryParse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(room -> "northpole-object-storage".equals(room.decryptName()))
                .map(Room::id)
                .findAny()
                .orElseThrow();
    }

    public record Room(String encryptedName, int id) {
        public static Optional<Room> tryParse(String line) {
            Map<Character, Integer> charStats = new HashMap<>();
            StringBuilder name = new StringBuilder();
            int id = 0;
            StringBuilder checkSum = new StringBuilder();
            boolean checksumPart = false;
            for (char c : line.toCharArray()) {
                if (c == '-') {
                    if (!checksumPart)
                        name.append(c);
                } else if (c >= 'a' && c <= 'z') {
                    if (checksumPart) checkSum.append(c);
                    else {
                        charStats.compute(c, (_, v) -> v == null ? 1 : v + 1);
                        name.append(c);
                    }
                } else if (c == '[') {
                    checksumPart = true;
                } else if (c >= '0' && c <= '9') {
                    id = id * 10 + (c - '0');
                } else if (c != ']') throw new RuntimeException();
            }

            if (name.charAt(name.length() - 1) == '-')
                name.deleteCharAt(name.length() - 1);

            String realCheckSum = charStats.keySet().stream()
                    .sorted(Comparator.<Character, Integer>comparing(charStats::get).reversed().thenComparing(c -> c))
                    .limit(5)
                    .map(Object::toString)
                    .collect(Collectors.joining(""));
            return realCheckSum.contentEquals(checkSum)
                    ? Optional.of(new Room(name.toString(), id))
                    : Optional.empty();
        }

        public String decryptName() {
            int shift = id % ALPHABET_SIZE;
            return shift(encryptedName, shift);
        }

        private String shift(String encryptedName, int shift) {
            StringBuilder decryptedName = new StringBuilder();
            for (char c : encryptedName.toCharArray()) {
                if (c == '-') decryptedName.append('-');
                else {
                    int newChar = c + shift;
                    if (newChar > 'z') newChar -= ALPHABET_SIZE;
                    decryptedName.append((char) newChar);
                }
            }
            return decryptedName.toString();
        }
    }
}
