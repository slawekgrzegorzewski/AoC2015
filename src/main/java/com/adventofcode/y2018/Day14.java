package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day14 {
    private final int requiredLearningLengthAsInt;
    private final List<Integer> requiredLearningLengthAsSequence;

    public Day14() throws IOException {
        this(Input.day14());
    }

    public Day14(String requiredLearningLength) {
        this.requiredLearningLengthAsInt = Integer.parseInt(requiredLearningLength);
        this.requiredLearningLengthAsSequence = new ArrayList<>();
        for (char c : requiredLearningLength.toCharArray()) {
            requiredLearningLengthAsSequence.add(c - '0');
        }
    }

    String part1() {
        boolean part1 = true;
        return extracted(part1);
    }

    String part2() {
        int i = 665705300;
        return extracted(false);
        //665705299 too high
        //665705300 too high
    }

    private String extracted(boolean part1) {
        List<Integer> scores = new ArrayList<>(requiredLearningLengthAsInt);
        int elf1Position = 0;
        int elf2Position = 1;
        int elf1LastPosition = 0;
        int elf2LastPosition = 1;
        int elf1GoingForwardFrom = -1;
        int elf2GoingForwardFrom = -1;
        scores.add(3);
        scores.add(7);
        while ((part1 && scores.size() < requiredLearningLengthAsInt + 10)
                || (!part1
                && (scores.size() < requiredLearningLengthAsSequence.size()
                || !scores.subList(scores.size() - requiredLearningLengthAsSequence.size(), scores.size()).equals(requiredLearningLengthAsSequence)))) {
            int scoreOfElf1Recipe = scores.get(elf1Position);
            int scoreOfElf2Recipe = scores.get(elf2Position);
            int newScore = scoreOfElf1Recipe + scoreOfElf2Recipe;
            if (newScore > 9) {
                scores.add(1);
                scores.add(newScore - 10);
            } else {
                scores.add(newScore);
            }
            elf1Position = (elf1Position + scoreOfElf1Recipe + 1) % scores.size();
            elf2Position = (elf2Position + scoreOfElf2Recipe + 1) % scores.size();
            if (elf1LastPosition <= elf1Position) {
                if (elf1GoingForwardFrom < 0) {
                    elf1GoingForwardFrom = scores.size();
                }
            } else {
                elf1GoingForwardFrom = -1;
            }
            if (elf2LastPosition <= elf2Position) {
                if (elf2GoingForwardFrom < 0) {
                    elf2GoingForwardFrom = scores.size();
                }
            } else {
                elf2GoingForwardFrom = -1;
            }
            if (elf1GoingForwardFrom > 0) {
                System.out.println("elf1GoingForwardFrom = " + elf1GoingForwardFrom);
            }
            if (elf2GoingForwardFrom > 0) {
                System.out.println("elf2GoingForwardFrom = " + elf2GoingForwardFrom);
            }
        }
        return part1
                ? scores.subList(requiredLearningLengthAsInt, requiredLearningLengthAsInt + 10)
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining())
                : String.valueOf(scores.size() - requiredLearningLengthAsSequence.size());
    }

    private boolean a(List<Integer> scores, List<Integer> integers) {
        if (scores.size() < integers.size()) return false;
        for (int i = 0; i < integers.size(); i++) {
            if (!scores.get(scores.size() - integers.size() + i).equals(integers.get(i))) return false;
        }
        return true;
    }

    private void print(List<Integer> scores, int elf1Position, int elf2Position) {
        for (int i = 0; i < scores.size(); i++) {
            if (i == elf1Position) {
                System.out.print("(");
            }
            if (i == elf2Position) {
                System.out.print("[");
            }
            System.out.print(scores.get(i));
            if (i == elf1Position) {
                System.out.print(")");
            }
            if (i == elf2Position) {
                System.out.print("]");
            }
            System.out.print("  ");
        }
        System.out.println();
    }
}
