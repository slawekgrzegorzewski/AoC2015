package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;
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
        return continueMakingRecipes(scores1 -> scores1.size() < requiredLearningLengthAsInt + 10)
                .subList(requiredLearningLengthAsInt, requiredLearningLengthAsInt + 10)
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    String part2() {
        return String.valueOf(
                solution(
                        continueMakingRecipes(scores1 -> solution(scores1, requiredLearningLengthAsSequence).isEmpty()),
                        requiredLearningLengthAsSequence)
                        .orElseThrow());
    }

    private List<Integer> continueMakingRecipes(Predicate<List<Integer>> continuePredicate) {
        List<Integer> scores = new ArrayList<>(requiredLearningLengthAsInt + 10);
        scores.add(3);
        scores.add(7);
        int elf1Position = 0;
        int elf2Position = 1;
        while (continuePredicate.test(scores)) {
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
        }
        return scores;
    }

    private OptionalInt solution(List<Integer> test, List<Integer> sequenceToFind) {
        if (test.size() < requiredLearningLengthAsSequence.size() + 1) return OptionalInt.empty();
        return containsSequenceNElementsFromEnd(test, sequenceToFind, 0)
                ? OptionalInt.of(test.size() - sequenceToFind.size())
                : (containsSequenceNElementsFromEnd(test, sequenceToFind, 1)
                ? OptionalInt.of(test.size() - sequenceToFind.size() - 1)
                : OptionalInt.empty());
    }

    private boolean containsSequenceNElementsFromEnd(List<Integer> test, List<Integer> sequenceToFind, int offset) {
        return test.subList(test.size() - sequenceToFind.size() - offset, test.size() - offset).equals(sequenceToFind);
    }
}
