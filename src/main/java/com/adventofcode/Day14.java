package com.adventofcode;

import com.adventofcode.input.Input;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day14 {

    private static final int FINISH_SECOND = 2503;
    private final List<Input.Reindeer> reindeers;

    public Day14() throws IOException {
        reindeers = Input.day14();
    }

    long part1() {
        Map<Input.Reindeer, Integer> distances = new HashMap<>();
        reindeers.forEach(reindeer -> distances.put(reindeer, distance(reindeer)));
        return distances.values().stream().mapToInt(i -> i).max().orElseThrow();
    }

    long part2() {
        Map<Input.Reindeer, Integer[]> distances = new HashMap<>();
        reindeers.forEach(reindeer -> distances.put(reindeer, distances(reindeer)));
        Map<Input.Reindeer, Integer> scores = new HashMap<>();
        for (int second = 1; second <= FINISH_SECOND; second++) {
            int maxDistance = Integer.MIN_VALUE;
            for (Input.Reindeer reindeer : reindeers) {
                Integer[] reindeerDistances = distances.get(reindeer);
                if (reindeerDistances[second - 1] > maxDistance) maxDistance = reindeerDistances[second - 1];
            }
            for (Input.Reindeer reindeer : reindeers) {
                Integer[] reindeerDistances = distances.get(reindeer);
                if (reindeerDistances[second - 1] == maxDistance) scores.put(reindeer, scores.getOrDefault(reindeer, 0) + 1);
            }
        }
        return scores.values().stream().mapToInt(i -> i).max().orElseThrow();
    }

    private int distance(Input.Reindeer reindeer) {
        int totalCycleTime = reindeer.flyTime() + reindeer.restTime();
        int cycles = FINISH_SECOND / totalCycleTime;
        int remainingSeconds = FINISH_SECOND % totalCycleTime;
        int distance = cycles * reindeer.flyTime() * reindeer.speed();
        if (remainingSeconds > reindeer.flyTime())
            distance += reindeer.flyTime() * reindeer.speed();
        else distance += remainingSeconds * reindeer.speed();
        return distance;
    }

    private Integer[] distances(Input.Reindeer reindeer) {
        Integer[] distances = new Integer[FINISH_SECOND];
        int speed = reindeer.speed();
        int nextChangeAt = reindeer.flyTime();
        for (int i = 1; i <= FINISH_SECOND; i++) {
            distances[i - 1] = (i > 1 ? distances[i - 2] : 0) + speed;
            if (i == nextChangeAt) {
                speed = speed == 0 ? reindeer.speed() : 0;
                nextChangeAt += speed == 0 ? reindeer.restTime() : reindeer.flyTime();
            }
        }
        return distances;
    }
}