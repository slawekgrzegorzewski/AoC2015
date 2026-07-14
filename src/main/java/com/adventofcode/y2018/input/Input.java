package com.adventofcode.y2018.input;

import com.adventofcode.y2018.Day3;
import com.adventofcode.y2018.Day4;
import com.adventofcode.y2018.Day5;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static List<String> day6() throws IOException {
        return getInputFromFile("/y2018/day6");
    }

    public static List<String> day7() throws IOException {
        return getInputFromFile("/y2018/day7");
    }

    public static List<String> day8() throws IOException {
        return getInputFromFile("/y2018/day8");
    }

    public static List<String> day9() throws IOException {
        return getInputFromFile("/y2018/day9");
    }

    public static List<String> day10() throws IOException {
        return getInputFromFile("/y2018/day10");
    }

    public static List<String> day11() throws IOException {
        return getInputFromFile("/y2018/day11");
    }

    public static List<String> day12() throws IOException {
        return getInputFromFile("/y2018/day12");
    }

    public static List<String> day13() throws IOException {
        return getInputFromFile("/y2018/day13");
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
