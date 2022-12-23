package com.adventofcode.input;

import com.adventofcode.Box;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
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

    public static List<String> day4() throws IOException {
        return getInputFromFile("/day4");
    }

    public static List<String> day5() throws IOException {
        return getInputFromFile("/day5");
    }

    public static List<String> day6() throws IOException {
        return getInputFromFile("/day6");
    }

    public static List<String> day7() throws IOException {
        return getInputFromFile("/day7");
    }

    public static List<String> day8() throws IOException {
        return getInputFromFile("/day8");
    }

    public static List<String> day9() throws IOException {
        return getInputFromFile("/day9");
    }

    public static List<String> day10() throws IOException {
        return getInputFromFile("/day10");
    }

    public static List<String> day11() throws IOException {
        return getInputFromFile("/day11");
    }

    public static List<String> day12() throws IOException {
        return getInputFromFile("/day12");
    }

    public static List<String> day13() throws IOException {
        return getInputFromFile("/day13");
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
}
