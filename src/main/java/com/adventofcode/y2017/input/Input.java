package com.adventofcode.y2017.input;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.adventofcode.Utils.getInputFromFile;

public class Input {

    public static char[] day1() throws IOException {
        return getInputFromFile("/y2017/day1").getFirst().toCharArray();
    }

    public static List<List<Integer>> day2() throws IOException {
        return getInputFromFile("/y2017/day2")
                .stream()
                .map(line -> Arrays.stream(line.split("\\s+")).map(Integer::parseInt).toList())
                .toList();
    }

    public static int day3() throws IOException {
        return Integer.parseInt(getInputFromFile("/y2017/day3").getFirst());
    }

    public static List<String> day4() throws IOException {
        return getInputFromFile("/y2017/day4");
    }

    public static List<String> day5() throws IOException {
        return getInputFromFile("/y2017/day5");
    }

    public static List<String> day6() throws IOException {
        return getInputFromFile("/y2017/day6");
    }

    public static List<String> day7() throws IOException {
        return getInputFromFile("/y2017/day7");
    }

    public static List<String> day8() throws IOException {
        return getInputFromFile("/y2017/day8");
    }

    public static List<String> day9() throws IOException {
        return getInputFromFile("/y2017/day9");
    }

    public static List<String> day10() throws IOException {
        return getInputFromFile("/y2017/day10");
    }

    public static List<String> day11() throws IOException {
        return getInputFromFile("/y2017/day11");
    }

    public static List<String> day12() throws IOException {
        return getInputFromFile("/y2017/day12");
    }

    public static List<String> day13() throws IOException {
        return getInputFromFile("/y2017/day13");
    }

    public static List<String> day14() throws IOException {
        return getInputFromFile("/y2017/day14");
    }

    public static List<String> day15() throws IOException {
        return getInputFromFile("/y2017/day15");
    }

    public static List<String> day16() throws IOException {
        return getInputFromFile("/y2017/day16");
    }

    public static List<String> day17() throws IOException {
        return getInputFromFile("/y2017/day17");
    }

    public static List<String> day18() throws IOException {
        return getInputFromFile("/y2017/day18");
    }

    public static List<String> day19() throws IOException {
        return getInputFromFile("/y2017/day19");
    }

    public static List<String> day20() throws IOException {
        return getInputFromFile("/y2017/day20");
    }

    public static List<String> day21() throws IOException {
        return getInputFromFile("/y2017/day21");
    }

    public static List<String> day22() throws IOException {
        return getInputFromFile("/y2017/day22");
    }

    public static List<String> day23() throws IOException {
        return getInputFromFile("/y2017/day23");
    }

    public static List<String> day24() throws IOException {
        return getInputFromFile("/y2017/day24");
    }

    public static List<String> day25() throws IOException {
        return getInputFromFile("/y2017/day25");
    }
}
