package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.List;

public class Day4 {

    private final List<List<String>> passphrasesWords;


    public Day4() throws IOException {
        this.passphrasesWords = Input.day4();
    }

    long part1() {
        return passphrasesWords.stream()
                .filter(Day4::unique)
                .count();
    }

    long part2() {
        //433 too high
        return passphrasesWords.stream()
                .filter(Day4::unique)
                .filter(Day4::noAnagrams)
                .count();
    }

    private static boolean unique(List<String> words) {
        return words.stream().distinct().count() == words.size();
    }

    private static boolean noAnagrams(List<String> words) {
        for (int i = 0; i < words.size(); i++) {
            String firstWord = words.get(i);
            for (int j = i + 1; j < words.size(); j++) {
                String secondWord = words.get(j);
                if (areAnagrams(firstWord, secondWord)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean areAnagrams(String firstWord, String secondWord) {
        if (firstWord.length() != secondWord.length()) {
            return false;
        }
        int[] lettersCount = new int['z' - 'a' + 1];
        for (char c : firstWord.toCharArray()) {
            lettersCount[c - 'a']++;
        }
        for (char c : secondWord.toCharArray()) {
            lettersCount[c - 'a']--;
        }
        for (int stat : lettersCount) {
            if (stat != 0) {
                return false;
            }
        }
        return true;
    }
}
