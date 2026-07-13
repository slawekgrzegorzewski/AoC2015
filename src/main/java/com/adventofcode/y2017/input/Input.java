package com.adventofcode.y2017.input;

import com.adventofcode.y2017.*;

import java.io.IOException;
import java.util.*;
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

    public static Map<String, char[][]> day21() throws IOException {
        return getInputFromFile("/y2017/day21")
                .stream()
                .map(line -> line.split(" => "))
                .collect(Collectors.toMap(
                        line -> line[0],
                        line -> Arrays.stream(line[1].split("/")).map(String::toCharArray).toArray(char[][]::new)
                ));
    }

    public static Day22.TaskInput day22() throws IOException {
        char[][] array = getInputFromFile("/y2017/day22")
                .stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);
        Map<Integer, Map<Integer, Character>> map = new HashMap<>();
        for (int row = 0; row < array.length; row++) {
            for (int column = 0; column < array[row].length; column++) {
                if (array[row][column] == '#')
                    map.computeIfAbsent(row, _ -> new HashMap<>()).put(column, 'i');
            }
        }
        return new Day22.TaskInput(map, array[0].length / 2, array.length / 2);
    }

    public static List<String> day23() throws IOException {
        return getInputFromFile("/y2017/day23");
    }

    public static List<Day24.Component> day24() throws IOException {
        return getInputFromFile("/y2017/day24")
                .stream()
                .map(line -> line.split("/"))
                .map(line -> new Day24.Component(Integer.parseInt(line[0]), Integer.parseInt(line[1])))
                .toList();
    }

    public static Day25.TuringMachine day25() throws IOException {
        List<String> inputFromFile = getInputFromFile("/y2017/day25");
        String initialState = inputFromFile.get(0).replace("Begin in state ", "").replace(".", "");
        int diagnosticsAfterSteps = Integer.parseInt(inputFromFile.get(1).replace("Perform a diagnostic checksum after ", "").replace(" steps.", ""));
        Map<String, Day25.State> states = new HashMap<>();
        for (int i = 3; i + 8 < inputFromFile.size(); i += 10) {
            String stateName = inputFromFile.get(i).replace("In state ", "").replace(":", "");
            int writeValueZero = Integer.parseInt(inputFromFile.get(i + 2).replace("    - Write the value ", "").replace(".", ""));
            boolean moveRightZero = "    - Move one slot to the right.".equals(inputFromFile.get(i + 3));
            String nextStateZero = inputFromFile.get(i + 4).replace("    - Continue with state ", "").replace(".", "");
            int writeValueOne = Integer.parseInt(inputFromFile.get(i + 6).replace("    - Write the value ", "").replace(".", ""));
            boolean moveRightOne = "    - Move one slot to the right.".equals(inputFromFile.get(i + 7));
            String nextStateOne = inputFromFile.get(i + 8).replace("    - Continue with state ", "").replace(".", "");
            Day25.State state = new Day25.State(stateName, Map.of(0, new Day25.Action(writeValueZero, moveRightZero, nextStateZero), 1, new Day25.Action(writeValueOne, moveRightOne, nextStateOne)));
            states.put(stateName, state);
        }
        return new Day25.TuringMachine(
                states.get(initialState),
                states,
                new LinkedList<>(),
                diagnosticsAfterSteps
        );
    }
}
