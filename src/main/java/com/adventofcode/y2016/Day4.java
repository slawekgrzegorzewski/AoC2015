package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        private static final Pattern ROOM = Pattern.compile("([a-z-]+)-(\\d+)\\[([a-z]{5})]");

        public static Optional<Room> tryParse(String line) {
            Matcher m = ROOM.matcher(line);
            if (!m.matches()) {
                throw new IllegalArgumentException("Invalid room format: " + line);
            }
            String name = m.group(1);
            int id = Integer.parseInt(m.group(2));
            String givenChecksum = m.group(3);

            Map<Character, Long> charStats = name.chars()
                    .filter(c -> c != '-')
                    .mapToObj(c -> (char) c)
                    .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

            String realCheckSum = charStats.entrySet().stream()
                    .sorted(Map.Entry.<Character, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                    .limit(5)
                    .map(e -> e.getKey().toString())
                    .collect(Collectors.joining());

            return realCheckSum.contentEquals(givenChecksum)
                    ? Optional.of(new Room(name, id))
                    : Optional.empty();
        }

        public String decryptName() {
            StringBuilder decryptedName = new StringBuilder();
            for (char c : encryptedName.toCharArray()) {
                if (c == '-')
                    decryptedName.append('-');
                else
                    decryptedName.append((char) ('a' + (c - 'a' + id) % ALPHABET_SIZE));
            }
            return decryptedName.toString();
        }

    }
}
