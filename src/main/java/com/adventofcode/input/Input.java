package com.adventofcode.input;

import com.adventofcode.Box;
import com.google.common.base.Splitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;
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

    public static List<Reindeer> day14() throws IOException {
        Pattern pattern = Pattern.compile("([A-Za-z]+) can fly ([0-9]+) km/s for ([0-9]+) seconds, but then must rest for ([0-9]+) seconds.");
        return getInputFromFile("/day14")
                .stream()
                .map(pattern::matcher)
                .map(matcher -> {
                    if (!matcher.find()) throw new IllegalStateException();
                    return new Reindeer(
                            matcher.group(1),
                            Integer.parseInt(matcher.group(2)),
                            Integer.parseInt(matcher.group(3)),
                            Integer.parseInt(matcher.group(4))
                    );
                })
                .toList();
    }

    public static List<Ingredient> day15() throws IOException {
        Pattern pattern = Pattern.compile("([A-Za-z]+): capacity ([-]*[0-9]+), durability ([-]*[0-9]+), flavor ([-]*[0-9]+), texture ([-]*[0-9]+), calories ([-]*[0-9]+)");
        return getInputFromFile("/day15")
                .stream()
                .map(pattern::matcher)
                .map(matcher -> {
                    if (!matcher.find()) throw new IllegalStateException();
                    return new Ingredient(
                            matcher.group(1),
                            Integer.parseInt(matcher.group(2)),
                            Integer.parseInt(matcher.group(3)),
                            Integer.parseInt(matcher.group(4)),
                            Integer.parseInt(matcher.group(5)),
                            Integer.parseInt(matcher.group(6))
                    );
                })
                .toList();
    }

    public static List<AuntSue> day16() throws IOException {
        return getInputFromFile("/day16")
                .stream()
                .map(line -> {
                    String suePart = line.substring(0, line.indexOf(":"));
                    String propertiesPart = line.substring(line.indexOf(":") + 1).trim();
                    Map<String, Integer> prpperties = Splitter.on(", ")
                            .trimResults()
                            .omitEmptyStrings()
                            .splitToStream(propertiesPart)
                            .collect(Collectors.toMap(
                                    part -> part.substring(0, part.indexOf(":")),
                                    part -> Integer.parseInt(part.substring(part.indexOf(":") + 1).trim())
                            ));
                    return new AuntSue(
                            Integer.parseInt(suePart.replace("Sue ", "")),
                            prpperties
                    );
                })
                .toList();
    }

    public static List<Integer> day17() throws IOException {
        return getInputFromFile("/day17")
                .stream()
                .map(Integer::parseInt)
                .toList();
    }

    public static char[][] day18() throws IOException {
        return getInputFromFile("/day18")
                .stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }

    public static FusionPlantInput day19() throws IOException {
        List<String> inputFromFile = getInputFromFile("/day19");
        Map<Integer, List<List<Integer>>> replacements = new HashMap<>();
        Set<String> basicMolecules = new HashSet<>();
        for (int i = 0; i < inputFromFile.size() - 2; i++) {
            String[] replacementParts = inputFromFile.get(i).split(" => ");
            basicMolecules.addAll(new HashSet<>(findBasicMoleculesIn(replacementParts[0])));
            basicMolecules.addAll(new HashSet<>(findBasicMoleculesIn(replacementParts[1])));
        }
        Map<String, Integer> basicMoleculesMap = new HashMap<>();
        Map<Integer, String> basicMoleculesIndexes = new HashMap<>();
        basicMolecules.stream().sorted().forEach(molecule -> basicMoleculesMap.put(molecule, basicMoleculesMap.size()));
        basicMoleculesMap.forEach((molecule, index) -> basicMoleculesIndexes.put(index, molecule));
        for (int i = 0; i < inputFromFile.size() - 2; i++) {
            String[] replacementParts = inputFromFile.get(i).split(" => ");
            replacements.computeIfAbsent(basicMoleculesMap.get(replacementParts[0]), _ -> new ArrayList<>())
                    .add(findBasicMoleculesIn(replacementParts[1]).stream().map(basicMoleculesMap::get).toList());
        }
        return new FusionPlantInput(
                basicMoleculesMap,
                basicMoleculesIndexes,
                replacements,
                findBasicMoleculesIn(inputFromFile.getLast()).stream().map(basicMoleculesMap::get).toList());
    }

    private static List<String> findBasicMoleculesIn(String molecule) {
        char[] charArray = molecule.toCharArray();
        List<String> basicMolecules = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (char c : charArray) {
            if ('Z' >= c && c >= 'A' && !sb.isEmpty()) {
                basicMolecules.add(sb.toString());
                sb = new StringBuilder().append(c);
            } else {
                sb.append(c);
            }
        }
        basicMolecules.add(sb.toString());
        return basicMolecules;
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

    public record Reindeer(String name, int speed, int flyTime, int restTime) {
    }

    public record Ingredient(String name, int capacity, int durability, int flavor, int texture, int calories) {
    }

    public record AuntSue(int id, Map<String, Integer> properties) {
    }

    public record FusionPlantInput(Map<String, Integer> basicMolecules,
                                   Map<Integer, String> basicMoleculesIndexes,
                                   Map<Integer, List<List<Integer>>> replacements,
                                   List<Integer> moleculeToProduce) {
    }
}
