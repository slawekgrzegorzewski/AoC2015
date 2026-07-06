package com.adventofcode.y2016.input;

import com.adventofcode.y2016.Day15;
import com.google.common.base.Splitter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.adventofcode.Utils.getInputFromFile;

public class Input {

    public static List<String> day1() throws IOException {
        return Arrays.stream(getInputFromFile("/y2016/day1")
                        .getFirst()
                        .split(", "))
                .toList();
    }

    public static List<String> day2() throws IOException {
        return getInputFromFile("/y2016/day2");
    }

    public static List<List<Integer>> day3() throws IOException {
        return getInputFromFile("/y2016/day3")
                .stream()
                .map(line -> Splitter.on(" ").omitEmptyStrings().trimResults().splitToList(line).stream().map(Integer::parseInt).toList())
                .toList();
    }

    public static List<String> day4() throws IOException {
        return getInputFromFile("/y2016/day4");
    }

    public static String day5() throws IOException {
        return getInputFromFile("/y2016/day5").getFirst();
    }

    public static List<String> day6() throws IOException {
        return getInputFromFile("/y2016/day6");
    }

    public static List<String> day7() throws IOException {
        return getInputFromFile("/y2016/day7");
    }

    public static List<String> day8() throws IOException {
        return getInputFromFile("/y2016/day8");
    }

    public static String day9() throws IOException {
        return getInputFromFile("/y2016/day9").getFirst();
    }

    public static List<String> day10() throws IOException {
        return getInputFromFile("/y2016/day10");
    }

    public static List<String> day11() throws IOException {
        return getInputFromFile("/y2016/day11");
    }

    public static List<String> day12() throws IOException {
        return getInputFromFile("/y2016/day12");
    }

    public static int day13() throws IOException {
        return Integer.parseInt(getInputFromFile("/y2016/day13").getFirst());
    }

    public static String day14() throws IOException {
        return getInputFromFile("/y2016/day14").getFirst();
    }

    public static Map<Integer, Day15.Disc> day15() throws IOException {
        return getInputFromFile("/y2016/day15")
                .stream()
                .map(Day15.Disc::parse)
                .collect(Collectors.toMap(
                        Day15.Disc::number,
                        Function.identity()
                ));
    }

    public static char[] day16() throws IOException {
        return getInputFromFile("/y2016/day16").getFirst().toCharArray();
    }

    public static String day17() throws IOException {
        return getInputFromFile("/y2016/day17").getFirst();
    }

    public static List<String> day18() throws IOException {
        return getInputFromFile("/y2016/day18");
    }

    public static List<String> day19() throws IOException {
        return getInputFromFile("/y2016/day19");
    }

    public static List<String> day20() throws IOException {
        return getInputFromFile("/y2016/day20");
    }

    public static List<String> day21() throws IOException {
        return getInputFromFile("/y2016/day21");
    }

    public static List<String> day22() throws IOException {
        return getInputFromFile("/y2016/day22");
    }

    public static List<String> day23() throws IOException {
        return getInputFromFile("/y2016/day23");
    }

    public static List<String> day24() throws IOException {
        return getInputFromFile("/y2016/day24");
    }

    public static List<String> day25() throws IOException {
        return getInputFromFile("/y2016/day25");
    }
}
