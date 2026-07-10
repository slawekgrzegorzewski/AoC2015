package com.adventofcode.y2017.input;

import com.adventofcode.y2017.Day11;
import com.adventofcode.y2017.Day20;
import com.adventofcode.y2017.Day7;
import com.adventofcode.y2017.Day8;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static List<List<String>> day4() throws IOException {
        return getInputFromFile("/y2017/day4")
                .stream()
                .map(line -> Arrays.stream(line.split("\\s+")).toList())
                .collect(Collectors.toList());
    }

    public static int[] day5() throws IOException {
        return getInputFromFile("/y2017/day5")
                .stream()
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    public static List<Integer> day6() throws IOException {
        return Arrays.stream(getInputFromFile("/y2017/day6")
                        .getFirst()
                        .split("\\s+"))
                .map(Integer::parseInt)
                .toList();
    }

    public static List<Day7.Program> day7() throws IOException {
        return getInputFromFile("/y2017/day7")
                .stream()
                .map(Day7.Program::parse)
                .toList();
    }

    public static List<Day8.Instruction> day8() throws IOException {
        return getInputFromFile("/y2017/day8")
                .stream()
                .map(Day8.Instruction::parse)
                .toList();
    }

    public static char[] day9() throws IOException {
        return getInputFromFile("/y2017/day9").getFirst().toCharArray();
    }

    public static String day10() throws IOException {
        return getInputFromFile("/y2017/day10").getFirst();
    }

    public static List<Day11.Direction> day11() throws IOException {
        return Arrays.stream(getInputFromFile("/y2017/day11")
                        .getFirst()
                        .split(","))
                .map(Day11.Direction::fromString)
                .toList();
    }

    public static Map<Integer, List<Integer>> day12() throws IOException {
        return getInputFromFile("/y2017/day12")
                .stream()
                .map(line -> line.split(" <-> "))
                .collect(Collectors.toMap(
                        parts -> Integer.parseInt(parts[0]),
                        parts -> Arrays.stream(parts[1].split(", "))
                                .map(Integer::parseInt)
                                .toList()
                ));
    }

    public static Map<Integer, Integer> day13() throws IOException {
        return getInputFromFile("/y2017/day13")
                .stream()
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(
                        parts -> Integer.parseInt(parts[0]),
                        parts -> Integer.parseInt(parts[1])
                ));
    }

    public static String day14() throws IOException {
        return getInputFromFile("/y2017/day14").getFirst();
    }

    public static int[] day15() throws IOException {
        return getInputFromFile("/y2017/day15")
                .stream()
                .map(line -> line.replaceAll("Generator [AB] starts with ", ""))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    public static List<String> day16() throws IOException {
        return Arrays.stream(getInputFromFile("/y2017/day16")
                .getFirst()
                .split(","))
                .toList();
    }

    public static int day17() throws IOException {
        return Integer.parseInt(getInputFromFile("/y2017/day17").getFirst());
    }

    public static List<String> day18() throws IOException {
        return getInputFromFile("/y2017/day18");
    }

    public static char[][] day19() throws IOException {
        return getInputFromFile("/y2017/day19")
                .stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }

    public static List<Day20.Particle> day20() throws IOException {
        return getInputFromFile("/y2017/day20")
                .stream()
                .map(Day20.Particle::parse)
                .toList();
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
