package com.adventofcode;

import com.adventofcode.input.Input;
import com.google.common.base.CharMatcher;

import java.io.IOException;

public class Day11 {

    private final String input;

    public Day11() throws IOException {
        input = Input.day11();
    }

    String part1() {
        String password = incrementPassword(input);
        while (!isPasswordValid(password)) {
            password = incrementPassword(password);
        }
        return password;
    }

    String part2() {
        String password = incrementPassword(input);
        while (!isPasswordValid(password)) {
            password = incrementPassword(password);
        }
        password = incrementPassword(password);
        while (!isPasswordValid(password)) {
            password = incrementPassword(password);
        }
        return password;
    }

    String incrementPassword(String password) {
        StringBuilder newPassword = new StringBuilder(password);
        incrementCharAt(newPassword, password.length() - 1);
        return newPassword.toString();
    }

    private void incrementCharAt(StringBuilder password, int charIndexToIncrement) {
        if (charIndexToIncrement < 0) return;
        char c = password.charAt(charIndexToIncrement);
        if (c == 'z') {
            password.setCharAt(charIndexToIncrement, 'a');
            incrementCharAt(password, charIndexToIncrement - 1);
        } else {
            password.setCharAt(charIndexToIncrement, (char) (c + 1));
        }
    }

    private boolean isPasswordValid(String password) {
        return passwordContainsThreeConsecutiveLetters(password)
                && passwordDoesntContainLetters(password)
                && passwordContainsTwoPairsOfLetters(password);
    }

    private boolean passwordContainsThreeConsecutiveLetters(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            if (password.charAt(i) == password.charAt(i + 1) - 1 && password.charAt(i + 1) == password.charAt(i + 2) - 1) {
                return true;
            }
        }
        return false;
    }

    private boolean passwordDoesntContainLetters(String password) {
        return !CharMatcher.anyOf("iol").matchesAnyOf(password);
    }

    private boolean passwordContainsTwoPairsOfLetters(String password) {
        int countOfPairs = 0;
        for (int i = 0; i < password.length() - 1; i++) {
            if (password.charAt(i) == password.charAt(i + 1)) {
                countOfPairs++;
                i++;
                if (countOfPairs == 2) return true;
            }
        }
        return false;
    }
}