package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.rotate;

public class Day21 {
    private final static Pattern SWAP_LETTER = Pattern.compile("swap letter ([a-z]) with letter ([a-z])");
    private final static Pattern SWAP_POSITION = Pattern.compile("swap position (\\d) with position (\\d)");
    private final static Pattern MOVE_POSITION = Pattern.compile("move position (\\d) to position (\\d)");
    private final static Pattern REVERSE_POSITIONS = Pattern.compile("reverse positions (\\d) through (\\d)");
    private final static Pattern ROTATE = Pattern.compile("rotate based on position of letter ([a-z])");
    private final static Pattern ROTATE_RIGHT = Pattern.compile("rotate right (\\d) step[s]*");
    private final static Pattern ROTATE_LEFT = Pattern.compile("rotate left (\\d) step[s]*");
    private final List<String> commands;

    public Day21() throws IOException {
        this.commands = Input.day21();
    }

    String part1() {
        return scramblePassword("abcdefgh", commands, false);
    }

    String part2() {
        return scramblePassword("fbgdceah", commands.reversed(), true);
    }

    private String scramblePassword(String toScramble, List<String> commands, boolean reverse) {
        List<Character> password = new ArrayList<>();
        for (char c : toScramble.toCharArray())
            password.add(c);
        for (String command : commands) {
            Matcher matcher = SWAP_LETTER.matcher(command);
            if (matcher.matches()) {
                char letter1 = matcher.group(1).charAt(0);
                char letter2 = matcher.group(2).charAt(0);
                swapLetters(password, letter1, letter2);
                continue;
            }
            matcher = SWAP_POSITION.matcher(command);
            if (matcher.matches()) {
                int letter1Index = Integer.parseInt(matcher.group(1));
                int letter2Index = Integer.parseInt(matcher.group(2));
                swapPosition(password, letter1Index, letter2Index);
                continue;
            }
            matcher = MOVE_POSITION.matcher(command);
            if (matcher.matches()) {
                int fromPosition = Integer.parseInt(matcher.group(1));
                int toPosition = Integer.parseInt(matcher.group(2));
                movePosition(password, fromPosition, toPosition, reverse);
                continue;
            }
            matcher = REVERSE_POSITIONS.matcher(command);
            if (matcher.matches()) {
                int fromPosition = Integer.parseInt(matcher.group(1));
                int toPosition = Integer.parseInt(matcher.group(2));
                reversePosition(password, fromPosition, toPosition);
                continue;
            }
            matcher = ROTATE.matcher(command);
            if (matcher.matches()) {
                char letter1 = matcher.group(1).charAt(0);
                rotatePassword(password, letter1, reverse);
                continue;
            }
            matcher = ROTATE_RIGHT.matcher(command);
            if (matcher.matches()) {
                rotate(password, Integer.parseInt(matcher.group(1)) * (reverse ? -1 : 1));
                continue;
            }
            matcher = ROTATE_LEFT.matcher(command);
            if (matcher.matches()) {
                rotate(password, Integer.parseInt(matcher.group(1)) * (reverse ? 1 : -1));
                continue;
            }
            throw new IllegalArgumentException("Command not supported: " + command);
        }
        return toString(password);
    }

    private static void swapLetters(List<Character> password, char letter1, char letter2) {
        int letter1Index = findIndex(password, letter1);
        int letter2Index = findIndex(password, letter2);
        password.set(letter1Index, letter2);
        password.set(letter2Index, letter1);
    }

    private static void swapPosition(List<Character> password, int letter1Index, int letter2Index) {
        char letter1 = password.get(letter1Index);
        char letter2 = password.get(letter2Index);
        password.set(letter1Index, letter2);
        password.set(letter2Index, letter1);
    }

    private static void movePosition(List<Character> password, int fromPosition, int toPosition, boolean reverse) {
        char letter = password.remove(reverse ? toPosition : fromPosition);
        password.add(reverse ? fromPosition : toPosition, letter);
    }

    private static void reversePosition(List<Character> password, int fromPosition, int toPosition) {
        List<Character> reversed = password.subList(fromPosition, toPosition + 1).reversed();
        ArrayList<Character> newPassword = new ArrayList<>(password.subList(0, fromPosition));
        newPassword.addAll(reversed);
        newPassword.addAll(password.subList(toPosition + 1, password.size()));
        password.clear();
        password.addAll(newPassword);
    }

    private static void rotatePassword(List<Character> password, char letter1, boolean reverse) {
        int indexOfLetter1 = password.indexOf(letter1);
        int distance = reverse ? undoLetterPositionRotation(indexOfLetter1) : 1 + indexOfLetter1 + (indexOfLetter1 >= 4 ? 1 : 0);
        rotate(password, distance);
    }

    private static int undoLetterPositionRotation(int indexOfLetter1) {
        return switch (indexOfLetter1) {
            case 0, 1 -> -1;
            case 2 -> 2;
            case 3 -> -2;
            case 4 -> 1;
            case 5 -> -3;
            case 6 -> 0;
            case 7 -> -4;
            default -> throw new IllegalStateException("Unexpected value: " + indexOfLetter1);
        };
    }

    private static String toString(List<Character> chars) {
        StringBuilder sb = new StringBuilder(chars.size());
        chars.forEach(sb::append);
        return sb.toString();
    }

    private static int findIndex(List<Character> chars, char find) {
        for (int i = 0; i < chars.size(); i++)
            if (chars.get(i) == find) return i;
        throw new IllegalArgumentException("Letter not found");
    }
}
