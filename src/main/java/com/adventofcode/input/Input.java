package com.adventofcode.input;

import com.adventofcode.Box;
import com.google.common.base.Splitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Input {

    public static List<String> day1() throws IOException {
        return getInputFromFile("/day1");
    }

    public static List<Box> day2() throws IOException {
        return getInputFromFile("/day2").stream().map(Box::parse).collect(Collectors.toList());
    }

    public static String day3() throws IOException {
        return String.join("", getInputFromFile("/day3"));
    }

    public static String day4() throws IOException {
        return String.join("", getInputFromFile("/day4"));
    }

    public static List<String> day5() throws IOException {
        return getInputFromFile("/day5");
    }

    public static List<LitInstruction> day6() throws IOException {
        return getInputFromFile("/day6").
                stream()
                .map(line -> {
                    String operation;
                    if (line.startsWith("turn on")) operation = "turn on";
                    else if (line.startsWith("turn off")) operation = "turn off";
                    else if (line.startsWith("toggle")) operation = "toggle";
                    else throw new IllegalStateException("Unknown operation: " + line);
                    line = line.replace(operation + " ", "");
                    String[] parts = line.split(" through ");
                    String[] leftTopParts = parts[0].split(",");
                    String[] rightDownParts = parts[1].split(",");
                    return new LitInstruction(
                            operation,
                            Integer.parseInt(leftTopParts[0]),
                            Integer.parseInt(leftTopParts[1]),
                            Integer.parseInt(rightDownParts[0]),
                            Integer.parseInt(rightDownParts[1])
                    );
                })
                .toList();
    }

    public static List<String> day7() throws IOException {
        return getInputFromFile("/day7");
    }

    public static List<String> day8() throws IOException {
        return getInputFromFile("/day8");
    }

    public static Map<StringPair, Integer> day9() throws IOException {
        return getInputFromFile("/day9")
                .stream()
                .map(Splitter.on(" = ").trimResults().omitEmptyStrings()::splitToList)
                .collect(Collectors.toMap(
                        parts -> {
                            List<String> locations = Splitter.on(" to ").trimResults().omitEmptyStrings().splitToList(parts.getFirst());
                            return new StringPair(locations.getFirst(), locations.getLast());
                        },
                        parts -> Integer.parseInt(parts.getLast())
                ));
    }

    public static String day10() throws IOException {
        return getInputFromFile("/day10").getFirst();
    }

    public static String day11() throws IOException {
        return getInputFromFile("/day11").getFirst();
    }

    public static String day12() throws IOException {
        return getInputFromFile("/day12").getFirst();
    }

    public static Map<StringPair, Integer> day13() throws IOException {
        Map<StringPair, Integer> input = new HashMap<>();
        getInputFromFile("/day13")
                .forEach(line -> {
                    String[] whoEffectParts;
                    int effect;
                    if (line.contains("would gain")) {
                        whoEffectParts = line.split(" would gain ");
                        effect = 1;
                    } else {
                        whoEffectParts = line.split(" would lose ");
                        effect = -1;
                    }
                    String[] pointsWhoParts = whoEffectParts[1].split(" happiness units by sitting next to ");
                    input.put(new StringPair(whoEffectParts[0], pointsWhoParts[1].replace(".", "")), effect * Integer.parseInt(pointsWhoParts[0]));
                });
        return input;
    }

    public static List<String> day14() throws IOException {
        return getInputFromFile("/day14");
    }

    public static List<String> day15() throws IOException {
        return getInputFromFile("/day15");
    }

    public static List<String> day16() throws IOException {
        return getInputFromFile("/day16");
    }

    public static List<String> day17() throws IOException {
        return getInputFromFile("/day17");
    }

    public static List<String> day18() throws IOException {
        return getInputFromFile("/day18");
    }

    public static List<String> day19() throws IOException {
        return getInputFromFile("/day19");
    }

    public static List<String> day20() throws IOException {
        return getInputFromFile("/day20");
    }

    public static List<String> day21() throws IOException {
        return getInputFromFile("/day21");
    }

    public static List<String> day22() throws IOException {
        return getInputFromFile("/day22");
    }

    public static List<String> day23() throws IOException {
        return getInputFromFile("/day23");
    }

    public static List<String> day24() throws IOException {
        return getInputFromFile("/day24");
    }

    public static List<String> day25() throws IOException {
        return getInputFromFile("/day25");
    }

    private static List<String> getInputFromFile(String resourceName) throws IOException {
        try (InputStreamReader in = new InputStreamReader(Objects.requireNonNull(Input.class.getResourceAsStream(resourceName))); BufferedReader reader = new BufferedReader(in)) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    public record LitInstruction(String operation, int leftTopX, int leftTopY, int rightDownX, int rightDownY) {
    }

    public record StringPair(String first, String second) {
    }
}
