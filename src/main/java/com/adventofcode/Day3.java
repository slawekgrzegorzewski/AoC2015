package com.adventofcode;

import com.adventofcode.input.Input;
import com.adventofcode.input.XY;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Day3 {


    private final String input;

    public Day3() throws IOException {
        input = Input.day3();
    }

    long part1() {
        XY currentPosition = new XY(0, 0);
        Set<XY> visitedHouses = new HashSet<>();
        visitedHouses.add(currentPosition);
        for (char c : input.toCharArray()) {
            currentPosition = currentPosition.nextPosition(c);
            visitedHouses.add(currentPosition);
        }
        return visitedHouses.size();
    }

    long part2() {
        XY santaPosition = new XY(0, 0);
        XY roboSantaPosition = new XY(0, 0);
        Set<XY> visitedHouses = new HashSet<>();
        visitedHouses.add(santaPosition);
        visitedHouses.add(roboSantaPosition);
        char[] charArray = input.toCharArray();
        for (int i = 0; i < charArray.length; i += 2) {
            santaPosition = santaPosition.nextPosition(charArray[i]);
            roboSantaPosition = roboSantaPosition.nextPosition(charArray[i + 1]);
            visitedHouses.add(santaPosition);
            visitedHouses.add(roboSantaPosition);
        }
        return visitedHouses.size();
    }
}


