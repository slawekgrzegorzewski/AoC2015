package com.adventofcode.y2018;

import com.adventofcode.Utils;
import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Predicate;

public class Day14 {
    public static final int NOT_FOUND = -1;
    private final int requiredLearningLengthAsInt;
    private final Utils.ByteArrayWithSize requiredLearningLengthAsSequence;

    public Day14() throws IOException {
        this(Input.day14());
    }

    public Day14(String requiredLearningLength) {
        this.requiredLearningLengthAsInt = Integer.parseInt(requiredLearningLength);
        char[] requiredLearningLengthCharArray = requiredLearningLength.toCharArray();
        this.requiredLearningLengthAsSequence = new Utils.ByteArrayWithSize(requiredLearningLengthCharArray.length);
        for (char c : requiredLearningLengthCharArray) {
            requiredLearningLengthAsSequence.add((byte) (c - '0'));
        }
    }

    String part1() {
        return continueMakingRecipes(
                scores -> scores.size() < requiredLearningLengthAsInt + 10,
                scores -> getStringBetweenIndexes(scores, requiredLearningLengthAsInt, requiredLearningLengthAsInt + 10),
                requiredLearningLengthAsInt + 10);
    }

    String part2() {
        return continueMakingRecipes(
                scores -> firstIndexOf(scores, requiredLearningLengthAsSequence) == NOT_FOUND,
                scores -> String.valueOf(firstIndexOf(scores, requiredLearningLengthAsSequence)),
                21_000_000);
    }

    private String continueMakingRecipes(
            Predicate<Utils.ByteArrayWithSize> continuePredicate,
            Function<Utils.ByteArrayWithSize, String> resultConverter,
            int initialCapacity) {
        Utils.ByteArrayWithSize scores = new Utils.ByteArrayWithSize(initialCapacity);
        scores.add((byte) 3);
        scores.add((byte) 7);
        int elf1Position = 0;
        int elf2Position = 1;
        while (continuePredicate.test(scores)) {
            byte scoreOfElf1Recipe = scores.get(elf1Position);
            byte scoreOfElf2Recipe = scores.get(elf2Position);
            byte newScore = (byte) (scoreOfElf1Recipe + scoreOfElf2Recipe);
            if (newScore > 9) {
                scores.add((byte) 1);
                newScore -= 10;
            }
            scores.add(newScore);
            elf1Position = (elf1Position + scoreOfElf1Recipe + 1) % scores.size();
            elf2Position = (elf2Position + scoreOfElf2Recipe + 1) % scores.size();
        }
        return resultConverter.apply(scores);
    }

    private int firstIndexOf(Utils.ByteArrayWithSize test, Utils.ByteArrayWithSize sequenceToFind) {
        if (test.size() < requiredLearningLengthAsSequence.size() + 1) return NOT_FOUND;
        int size = firstIndexOf(test, sequenceToFind, 0);
        return size > NOT_FOUND
                ? size
                : firstIndexOf(test, sequenceToFind, 1);
    }

    private int firstIndexOf(Utils.ByteArrayWithSize test, Utils.ByteArrayWithSize sequenceToFind, int offset) {
        int start = test.size() - sequenceToFind.size() - offset;
        for (int i = 0; i < sequenceToFind.size(); i++) {
            if (test.get(start + i) != sequenceToFind.get(i))
                return NOT_FOUND;
        }
        return start;
    }

    private String getStringBetweenIndexes(Utils.ByteArrayWithSize scores, int from, int to) {
        StringBuilder sb = new StringBuilder();
        for (int i = from; i < to; i++) {
            sb.append(scores.get(i));
        }
        return sb.toString();
    }
}
