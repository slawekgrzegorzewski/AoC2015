package com.adventofcode.y2019.input;

import com.adventofcode.y2019.Day10;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.adventofcode.utils.Utils.getInputFromFile;

public class Input {

    public static List<Long> day1() throws IOException {
        return getInputFromFile("/y2019/day1")
                .stream()
                .mapToLong(Long::parseLong)
                .boxed()
                .collect(Collectors.toList());
    }

    public static List<Long> day2() throws IOException {
        return parseProgram("/y2019/day2");
    }

    public static List<List<String>> day3() throws IOException {
        return getInputFromFile("/y2019/day3")
                .stream()
                .limit(2)
                .map(line -> Arrays.stream(line.split(",")).toList())
                .collect(Collectors.toList());
    }

    public static int[] day4() throws IOException {
        return Arrays.stream(getInputFromFile("/y2019/day4")
                        .getFirst()
                        .split("-"))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    public static List<Long> day5() throws IOException {
        return parseProgram("/y2019/day5");
    }

    public static Map<String, List<String>> day6() throws IOException {
        Map<String, List<String>> orbits = new HashMap<>();
        for (String line : getInputFromFile("/y2019/day6")) {
            String[] split = line.split("\\)");
            orbits.computeIfAbsent(split[0], _ -> new ArrayList<>()).add(split[1]);
        }
        return orbits;
    }

    public static List<Long> day7() throws IOException {
        return parseProgram("/y2019/day7");
    }

    public static char[] day8() throws IOException {
        return getInputFromFile("/y2019/day8")
                .getFirst()
                .toCharArray();
    }

    public static List<Long> day9() throws IOException {
        return parseProgram("/y2019/day9");
    }

    public static Set<Day10.Coordinate> day10() throws IOException {
        Set<Day10.Coordinate> asteroids = new HashSet<>();
        List<String> inputFromFile = getInputFromFile("/y2019/day10");
        for (int j = 0; j < inputFromFile.size(); j++) {
            String line = inputFromFile.get(j);
            char[] lineChars = line.toCharArray();
            for (int i = 0; i < lineChars.length; i++) {
                if (lineChars[i] == '#') {
                    asteroids.add(new Day10.Coordinate(i, j));
                }
            }
        }
        return asteroids;
    }

    public static List<Long> day11() throws IOException {
        return parseProgram("/y2019/day11");
    }

    public static List<String> day12() throws IOException {
        return getInputFromFile("/y2019/day12");
    }

    public static List<String> day13() throws IOException {
        return getInputFromFile("/y2019/day13");
    }

    public static List<String> day14() throws IOException {
        return getInputFromFile("/y2019/day14");
    }

    public static List<String> day15() throws IOException {
        return getInputFromFile("/y2019/day15");
    }

    public static List<String> day16() throws IOException {
        return getInputFromFile("/y2019/day16");
    }

    public static List<String> day17() throws IOException {
        return getInputFromFile("/y2019/day17");
    }

    public static List<String> day18() throws IOException {
        return getInputFromFile("/y2019/day18");
    }

    public static List<String> day19() throws IOException {
        return getInputFromFile("/y2019/day19");
    }

    public static List<String> day20() throws IOException {
        return getInputFromFile("/y2019/day20");
    }

    public static List<String> day21() throws IOException {
        return getInputFromFile("/y2019/day21");
    }

    public static List<String> day22() throws IOException {
        return getInputFromFile("/y2019/day22");
    }

    public static List<String> day23() throws IOException {
        return getInputFromFile("/y2019/day23");
    }

    public static List<String> day24() throws IOException {
        return getInputFromFile("/y2019/day24");
    }

    public static List<String> day25() throws IOException {
        return getInputFromFile("/y2019/day25");
    }

    private static @NonNull List<Long> parseProgram(String path) throws IOException {
        return Arrays.stream(getInputFromFile(path)
                        .getFirst()
                        .split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

}
