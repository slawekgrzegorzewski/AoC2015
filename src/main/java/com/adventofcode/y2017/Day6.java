package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.*;

public class Day6 {
    private final List<Integer> banks;


    public Day6() throws IOException {
        this.banks = Input.day6();
    }

    long part1() {
        return checkForCycle()[0];
    }

    long part2() {
        return checkForCycle()[1];
    }

    private int[] checkForCycle() {
        List<Integer> workingCopy = new ArrayList<>(banks);
        LinkedHashSet<List<Integer>> seen = new LinkedHashSet<>();
        while (!seen.contains(workingCopy)) {
            seen.add(new ArrayList<>(workingCopy));
            redistribute(workingCopy);
        }
        return new int[]{seen.size(), seen.size() - getIndexOfPreviousOccurrence(seen, workingCopy)};
    }

    private void redistribute(List<Integer> banks) {
        int originIndex = banks.indexOf(Collections.max(banks));
        int blocks = banks.get(originIndex);
        banks.set(originIndex, 0);
        while (blocks-- > 0) {
            int index = (++originIndex) % banks.size();
            banks.set(index, banks.get(index) + 1);
        }
    }

    private static int getIndexOfPreviousOccurrence(LinkedHashSet<List<Integer>> seen, List<Integer> workingCopy) {
        int index = 0;
        for (List<Integer> element : seen) {
            if (element.equals(workingCopy)) {
                return index;
            }
            index++;
        }
        throw new IllegalStateException("No previous occurrence found");
    }
}
