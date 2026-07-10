package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.swap;

public class Day16 {
    private final List<String> moves;
    private final List<Character> programs = new ArrayList<>();

    public Day16() throws IOException {
        this.moves = Input.day16();
    }

    String part1() {
        initPrograms();
        return getString(performDance(0));
    }

    String part2() {
        initPrograms();
        Cycle cycle = findCycle();
        return cycle.cycle()
                .get((1_000_000_000 - cycle.cycleBeginIndex()) % cycle.cycle().size() - 1);
    }

    private int performDance(int position) {
        for (String move : moves) {
            char kind = move.charAt(0);
            String args = move.substring(1);
            switch (kind) {
                case 's' -> {
                    position -= Integer.parseInt(args);
                    position %= programs.size();
                    while (position < 0)
                        position = position + programs.size();
                }
                case 'x' -> {
                    String[] split = args.split("/");
                    swap(programs, alignIndex(Integer.parseInt(split[0]), position), alignIndex(Integer.parseInt(split[1]), position));
                }
                case 'p' -> swap(
                        programs,
                        programs.indexOf(args.charAt(0)),
                        programs.indexOf(args.charAt(2)));
            }
        }
        return position;
    }

    public Cycle findCycle() {
        int position = 0;
        List<String> positions = new ArrayList<>();
        while (true) {
            position = performDance(position);
            String string = getString(position);
            if (!positions.contains(string)) {
                positions.add(string);
            } else {
                int cycleBeginIndex = positions.indexOf(string);
                return new Cycle(cycleBeginIndex, positions.subList(cycleBeginIndex, positions.size()));
            }
        }
    }

    public record Cycle(int cycleBeginIndex, List<String> cycle) {
    }

    private void initPrograms() {
        programs.clear();
        for (char c : "abcdefghijklmnop".toCharArray()) {
            programs.add(c);
        }
    }

    private String getString(int position) {
        StringBuilder sb = new StringBuilder();
        programs.forEach(sb::append);
        String string = sb.toString();
        return string.substring(position) + string.substring(0, position);
    }

    private int alignIndex(int index, int position) {
        int i = (index + position) % programs.size();
        while (i < 0)
            i = i + programs.size();
        return i;
    }
}
