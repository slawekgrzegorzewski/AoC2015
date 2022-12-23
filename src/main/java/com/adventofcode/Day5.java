package com.adventofcode;

import com.adventofcode.input.Input;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class Day5 {

    private final List<String> input;

    public Day5() throws IOException {
        input = Input.day5();
    }

    long part1() {
        return input.stream().filter(this::isNiceStringV1).count();
    }

    boolean isNiceStringV1(String value) {
        char[][] forbiddenChars = new char[][]{
                new char[]{'a', 'b'},
                new char[]{'c', 'd'},
                new char[]{'p', 'q'},
                new char[]{'x', 'y'}
        };

        char[] chars = value.toCharArray();
        int vowelsCount = 0;
        char previousChar = chars[0];
        if (isVowel(previousChar)) vowelsCount++;
        boolean twoConsecutiveChars = false;
        boolean doesntContainForbiddenSequences = true;
        for (int i = 1; i < chars.length; i++) {
            char currentChar = chars[i];
            if (vowelsCount < 3 && isVowel(currentChar)) {
                vowelsCount++;
            }
            twoConsecutiveChars = twoConsecutiveChars || (previousChar == currentChar);
            doesntContainForbiddenSequences = containsForbiddenSequence(forbiddenChars, previousChar, currentChar);
            previousChar = currentChar;
            if (!doesntContainForbiddenSequences) break;
        }
        return vowelsCount >= 3 && twoConsecutiveChars && doesntContainForbiddenSequences;
    }

    private boolean containsForbiddenSequence(char[][] forbiddenChars, char previousChar, char currentChar) {
        return Arrays.stream(forbiddenChars).noneMatch(twoLetters -> twoLetters[0] == previousChar && twoLetters[1] == currentChar);
    }

    private static boolean isVowel(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
    }

    long part2() {
        return input.stream().filter(this::isNiceStringV2).count();
    }

    boolean isNiceStringV2(String value) {
        Map<String, List<Integer>> pairs = new HashMap<>();

        char[] chars = value.toCharArray();
        char secondPreviousChar = chars[0];
        char previousChar = chars[1];
        pairs.computeIfAbsent(stringFrom(secondPreviousChar, previousChar), s -> new ArrayList<>()).add(0);
        boolean containsSecondPreviousAndCurrentEqual = false;
        boolean twoPairsFound = false;
        for (int i = 2; i < chars.length; i++) {
            char currentChar = chars[i];
            if (!twoPairsFound) {
                List<Integer> indicies = pairs.computeIfAbsent(stringFrom(previousChar, currentChar), c -> new ArrayList<>());
                if (indicies.isEmpty())
                    indicies.add(i - 1);
                else if (indicies.get(0) + 1 < i - 1)
                    twoPairsFound = true;
            }
            containsSecondPreviousAndCurrentEqual = containsSecondPreviousAndCurrentEqual || secondPreviousChar == currentChar;
            if (containsSecondPreviousAndCurrentEqual && twoPairsFound)
                break;
            secondPreviousChar = previousChar;
            previousChar = currentChar;
        }
        return containsSecondPreviousAndCurrentEqual && twoPairsFound;
    }

    @NotNull
    private static String stringFrom(char c1, char c2) {
        return String.valueOf(c1) + c2;
    }
}


