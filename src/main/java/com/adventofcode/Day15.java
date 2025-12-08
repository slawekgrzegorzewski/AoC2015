package com.adventofcode;

import com.adventofcode.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day15 {

    private static final int TOTAL_SPOONS = 100;
    private final List<Input.Ingredient> ingredients;

    public Day15() throws IOException {
        ingredients = Input.day15();
    }

    long part1() {
        return findOptimumRecipe(new ArrayList<>(), new Result(new ArrayList<>(), 0), -1).score();
    }

    long part2() {
        return findOptimumRecipe(new ArrayList<>(), new Result(new ArrayList<>(), 0), 500).score();
    }

    private Result findOptimumRecipe(List<Integer> spoons, Result maxScoreSoFar, int limitCalories) {
        int totalSpoonsSoFar = spoons.stream().mapToInt(i -> i).sum();
        if (spoons.size() < ingredients.size()) {
            boolean lastElement = spoons.size() == ingredients.size() - 1;
            for (int i = (lastElement ? TOTAL_SPOONS - totalSpoonsSoFar : 0); i <= TOTAL_SPOONS - totalSpoonsSoFar; i++) {
                spoons.add(i);
                Result a = findOptimumRecipe(spoons, maxScoreSoFar, limitCalories);
                if (a.score() > maxScoreSoFar.score()) {
                    maxScoreSoFar = a;
                }
                spoons.removeLast();
            }
            return maxScoreSoFar;
        } else {
            if (totalSpoonsSoFar != TOTAL_SPOONS)
                System.out.println(spoons + " = " + totalSpoonsSoFar);
            if (limitCalories == -1 || calculateCalories(spoons) <= limitCalories) {
                long score = calculateScore(spoons);
                if (score > maxScoreSoFar.score()) {
                    return new Result(new ArrayList<>(spoons), score);
                } else {
                    return maxScoreSoFar;
                }
            } else {
                return maxScoreSoFar;
            }
        }
    }

    private long calculateCalories(List<Integer> spoons) {
        long sumOfCalories = 0;

        for (int i = 0; i < spoons.size(); i++) {
            sumOfCalories += (long) ingredients.get(i).calories() * spoons.get(i);
        }
        return sumOfCalories;
    }

    private long calculateScore(List<Integer> spoons) {
        int sumOfCapacity = 0;
        int sumOfDurability = 0;
        int sumOfFlavor = 0;
        int sumOfTexture = 0;

        for (int i = 0; i < spoons.size(); i++) {
            sumOfCapacity += ingredients.get(i).capacity() * spoons.get(i);
            sumOfDurability += ingredients.get(i).durability() * spoons.get(i);
            sumOfFlavor += ingredients.get(i).flavor() * spoons.get(i);
            sumOfTexture += ingredients.get(i).texture() * spoons.get(i);
        }
        return (long) Math.max(sumOfCapacity, 0) * Math.max(sumOfDurability, 0) * Math.max(sumOfFlavor, 0) * Math.max(sumOfTexture, 0);
    }

    private record Result(List<Integer> spoons, long score) {
    }
}