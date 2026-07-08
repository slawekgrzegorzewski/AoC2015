package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Day7 {
    private final List<Day7.Program> programs;
    private final Map<String, Program> programsByName;


    public Day7() throws IOException {
        this.programs = Input.day7();
        this.programsByName = programs.stream().collect(
                Collectors.toMap(
                        Program::name,
                        Function.identity()));
    }

    String part1() {
        return programsByName.entrySet()
                .stream()
                .max(Comparator.comparing(entry -> findDepth(entry.getKey(), programsByName)))
                .map(Map.Entry::getKey)
                .orElseThrow();
    }

    long part2() {
        Program unbalancedProgramHighestInTheStack = programsByName.entrySet()
                .stream()
                .filter(entry -> entry.getValue().children.stream().map(p -> findWeight(p, programsByName)).distinct().count() > 1)
                .map(Map.Entry::getKey)
                .map(programsByName::get)
                .min(Comparator.comparing(p -> findDepth(p.name(), programsByName)))
                .orElseThrow();
        Map<Integer, List<Program>> childrenWeights = unbalancedProgramHighestInTheStack.children().stream()
                .collect(Collectors.groupingBy(
                        child -> findWeight(child, programsByName),
                        Collectors.mapping(programsByName::get, toList())
                ));
        int incorrectWeight = childrenWeights.entrySet().stream()
                .filter(entry -> entry.getValue().size() == 1)
                .map(Map.Entry::getKey)
                .findAny()
                .orElseThrow();
        int correctWeight = childrenWeights.keySet().stream()
                .filter(weight -> weight != incorrectWeight)
                .findAny()
                .orElseThrow();
        return childrenWeights.get(incorrectWeight).getFirst().weight()
                + correctWeight
                - incorrectWeight;
    }

    private int findWeight(String programName, Map<String, Program> programsByName) {
        Program program = programsByName.get(programName);
        return program.weight() + program.children.stream().map(child -> findWeight(child, programsByName)).mapToInt(Integer::intValue).sum();
    }

    private int findDepth(String programName, Map<String, Program> programsByName) {
        return 1 + programsByName.get(programName).children.stream().map(child -> findDepth(child, programsByName)).mapToInt(Integer::intValue).max().orElse(0);
    }

    public record Program(String name, int weight, List<String> children) {
        public static Program parse(String line) {
            String[] split = line.split(" -> ");
            Pattern pattern = Pattern.compile("([a-z]+)\\s*\\((\\d+)\\)");
            Matcher matcher = pattern.matcher(split[0]);
            if (!matcher.find()) throw new IllegalArgumentException("Invalid input");
            String name = matcher.group(1);
            int weight = Integer.parseInt(matcher.group(2));
            List<String> children = new ArrayList<>();
            if (split.length > 1) {
                Collections.addAll(children, split[1].split(", "));
            }
            return new Program(name, weight, children);
        }
    }
}
