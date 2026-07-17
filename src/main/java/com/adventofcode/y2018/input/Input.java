package com.adventofcode.y2018.input;

import com.adventofcode.Utils;
import com.adventofcode.y2018.*;
import com.google.common.base.Splitter;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.adventofcode.Utils.BooleanArrayCollector.convertToAnIndex;
import static com.adventofcode.Utils.getInputFromFile;

public class Input {

    public static int[] day1() throws IOException {
        return getInputFromFile("/y2018/day1")
                .stream()
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    public static List<String> day2() throws IOException {
        return getInputFromFile("/y2018/day2");
    }

    public static List<Day3.Claim> day3() throws IOException {
        return getInputFromFile("/y2018/day3")
                .stream()
                .map(Day3.Claim::new)
                .toList();
    }

    public static Map<Day4.Timestamp, String> day4() throws IOException {
        return getInputFromFile("/y2018/day4")
                .stream()
                .collect(Collectors.toMap(
                        line -> Day4.Timestamp.parse(line.substring(0, line.indexOf(']') + 1)),
                        line -> line.substring(line.indexOf(']') + 2)
                ));
    }

    public static Day5.Particle day5(Character filterOut) throws IOException {
        char[] particles = getInputFromFile("/y2018/day5").getFirst().toCharArray();
        Day5.Particle firstParticle = new Day5.Particle(particles[0]);
        Day5.Particle particle = firstParticle;
        for (int i = 1; i < particles.length; i++) {
            char p = particles[i];
            if (filterOut != null && Character.toLowerCase(p) == Character.toLowerCase(filterOut))
                continue;
            particle.next = new Day5.Particle(p);
            particle.next.previous = particle;
            particle = particle.next;
        }
        return firstParticle;
    }

    public static List<Day6.Coordinate> day6() throws IOException {
        return getInputFromFile("/y2018/day6")
                .stream()
                .map(Day6.Coordinate::parse)
                .toList();
    }

    public static Map<String, List<String>> day7() throws IOException {
        Map<String, List<String>> stepsPrerequisites = new HashMap<>();
        Pattern pattern = Pattern.compile("Step (\\w) must be finished before step (\\w) can begin.");
        getInputFromFile("/y2018/day7")
                .stream()
                .map(pattern::matcher)
                .map(matcher -> {
                    if (!matcher.find()) throw new IllegalArgumentException("Invalid input");
                    return new String[]{matcher.group(1), matcher.group(2)};
                })
                .forEach(strings -> {
                    stepsPrerequisites.computeIfAbsent(strings[1], _ -> new ArrayList<>()).add(strings[0]);
                    stepsPrerequisites.computeIfAbsent(strings[0], _ -> new ArrayList<>());
                });
        return stepsPrerequisites;
    }

    public static List<Integer> day8() throws IOException {
        return Arrays.stream(getInputFromFile("/y2018/day8")
                        .getFirst()
                        .split(" "))
                .map(Integer::parseInt)
                .toList();
    }

    public static Day9.GameInfo day9() throws IOException {
        return Day9.GameInfo.parse(getInputFromFile("/y2018/day9").getFirst());
    }

    public static Map<Day10.Position, Day10.Velocity> day10() throws IOException {
        return getInputFromFile("/y2018/day10")
                .stream()
                .map(line -> line.replace("position=<", "").replace("velocity=<", "").replace(">", "").replace(",", ""))
                .map(line -> Splitter.on(" ").trimResults().omitEmptyStrings().splitToList(line))
                .collect(Collectors.toMap(
                        split -> new Day10.Position(Integer.parseInt(split.getFirst()), Integer.parseInt(split.get(1))),
                        split -> new Day10.Velocity(Integer.parseInt(split.get(2)), Integer.parseInt(split.getLast())),
                        (v, v1) -> {
                            if (v.equals(v1))
                                return v;
                            throw new IllegalArgumentException("Duplicate key");
                        }
                ));
    }

    public static int day11() throws IOException {
        return Integer.parseInt(getInputFromFile("/y2018/day11").getFirst());
    }

    public static Day12.PlantsNotes day12() throws IOException {
        List<String> input = getInputFromFile("/y2018/day12");
        return new Day12.PlantsNotes(
                input.getFirst().replace("initial state: ", ""),
                input.stream()
                        .skip(2)
                        .map(line -> line.split(" => "))
                        .collect(
                                new Utils.BooleanArrayCollector<>(
                                        () -> {
                                            boolean[] rules = new boolean[32];
                                            Arrays.fill(rules, false);
                                            return rules;
                                        },
                                        parts -> convertToAnIndex(parts[0]),
                                        parts -> parts[1].charAt(0) == '#'
                                )));
    }

    public static Day13.TrackAndCarts day13() throws IOException {
        List<Day13.Cart> carts = new ArrayList<>();
        Map<Day13.Coordinate, Character> track = new HashMap<>();
        List<String> input = getInputFromFile("/y2018/day13");
        int cartId = 0;
        for (int y = 0; y < input.size(); y++) {
            char[] row = input.get(y).toCharArray();
            for (int x = 0; x < row.length; x++) {
                final Day13.Coordinate coordinate = new Day13.Coordinate(x, y);
                switch (row[x]) {
                    case '<', '>' -> {
                        track.put(coordinate, '-');
                        carts.add(new Day13.Cart(cartId++, coordinate, row[x] == '<' ? Day13.Direction.LEFT : Day13.Direction.RIGHT, -1));
                    }
                    case 'v', '^' -> {
                        track.put(coordinate, '|');
                        carts.add(new Day13.Cart(cartId++, coordinate, row[x] == 'v' ? Day13.Direction.DOWN : Day13.Direction.UP, -1));
                    }
                    case '-', '|', '/', '\\', '+' -> track.put(coordinate, row[x]);
                }
            }
        }
        return new Day13.TrackAndCarts(track, carts);
    }

    public static List<String> day14() throws IOException {
        return getInputFromFile("/y2018/day14");
    }

    public static List<String> day15() throws IOException {
        return getInputFromFile("/y2018/day15");
    }

    public static List<String> day16() throws IOException {
        return getInputFromFile("/y2018/day16");
    }

    public static List<String> day17() throws IOException {
        return getInputFromFile("/y2018/day17");
    }

    public static List<String> day18() throws IOException {
        return getInputFromFile("/y2018/day18");
    }

    public static List<String> day19() throws IOException {
        return getInputFromFile("/y2018/day19");
    }

    public static List<String> day20() throws IOException {
        return getInputFromFile("/y2018/day20");
    }

    public static List<String> day21() throws IOException {
        return getInputFromFile("/y2018/day21");
    }

    public static List<String> day22() throws IOException {
        return getInputFromFile("/y2018/day22");
    }

    public static List<String> day23() throws IOException {
        return getInputFromFile("/y2018/day23");
    }

    public static List<String> day24() throws IOException {
        return getInputFromFile("/y2018/day24");
    }

    public static List<String> day25() throws IOException {
        return getInputFromFile("/y2018/day25");
    }
}
