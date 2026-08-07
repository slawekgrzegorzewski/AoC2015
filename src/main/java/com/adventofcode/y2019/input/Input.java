package com.adventofcode.y2019.input;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.adventofcode.Utils.getInputFromFile;

public class Input {

    public static List<Long> day1() throws IOException {
        return getInputFromFile("/y2019/day1")
                .stream()
                .mapToLong(Long::parseLong)
                .boxed()
                .collect(Collectors.toList());
    }

    public static List<Integer> day2() throws IOException {
        return Arrays.stream(getInputFromFile("/y2019/day2")
                        .getFirst()
                        .split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
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

    public static List<Integer> day5() throws IOException {
        return Arrays.stream(getInputFromFile("/y2019/day5")
                        .getFirst()
                        .split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public static List<String> day6() throws IOException {
        return getInputFromFile("/y2019/day6");
    }

    public static List<String> day7() throws IOException {
        return getInputFromFile("/y2019/day7");
    }

    public static List<String> day8() throws IOException {
        return getInputFromFile("/y2019/day8");
    }

    public static List<String> day9() throws IOException {
        return getInputFromFile("/y2019/day9");
    }

    public static List<String> day10() throws IOException {
        return getInputFromFile("/y2019/day10");
    }

    public static List<String> day11() throws IOException {
        return getInputFromFile("/y2019/day11");
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
}
