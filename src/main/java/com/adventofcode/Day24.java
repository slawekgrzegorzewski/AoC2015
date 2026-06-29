package com.adventofcode;

import com.adventofcode.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class Day24 {

    private final List<Integer> packageWeights;

    public Day24() throws IOException {
        packageWeights = Input.day24();
        packageWeights.sort(Comparator.naturalOrder());
    }

    long part1() {
        return findMinimumQuantumEntanglement(new TaskParameters(3, packageWeights));
    }

    long part2() {
        return findMinimumQuantumEntanglement(new TaskParameters(4, packageWeights));
    }

    private long findMinimumQuantumEntanglement(TaskParameters p) {
        return IntStream.rangeClosed(p.minPossibleGroupSize(), p.maxPossibleGroupSize())
                .mapToObj(groupSize -> findAllGroupsOfGivenSize(groupSize, p))
                .mapToLong(parts -> parts.stream()
                        .filter(group -> canSplit(p.removeUsedGroup(group)))
                        .mapToLong(Day24::quantumEntanglement)
                        .min()
                        .orElse(0L))
                .filter(l -> l > 0)
                .findFirst()
                .orElseThrow();
    }

    private boolean canSplit(TaskParameters p) {
        for (int groupSize = p.minPossibleGroupSize(); groupSize <= p.maxPossibleGroupSize(); groupSize++) {
            boolean canSplit = findAllGroupsOfGivenSize(groupSize, p.setPackageWeights(packageWeights))
                    .stream()
                    .anyMatch(group -> {
                        if (p.groupsCount() == 2)
                            return !group.isEmpty();
                        return canSplit(p.removeUsedGroup(group));
                    });
            if (canSplit)
                return true;
        }
        return false;
    }

    private List<List<Integer>> findAllGroupsOfGivenSize(int groupSize, TaskParameters p) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackCombinations(p, groupSize, new ArrayList<>(), 0, result);
        return result;
    }

    private void backtrackCombinations(TaskParameters p, int groupSize, ArrayList<Integer> currentGroup, int currentIndex, List<List<Integer>> foundGroups) {
        for (int j = currentIndex; j < packageWeights.size(); j++) {
            currentGroup.add(packageWeights.get(j));
            if (currentGroup.size() == groupSize) {
                int sum = sumIntegers(currentGroup);
                if (sum == p.packageGroupWeight()) {
                    foundGroups.add(new ArrayList<>(currentGroup));
                }
            } else {
                backtrackCombinations(p, groupSize, currentGroup, j + 1, foundGroups);
            }
            currentGroup.removeLast();
        }
    }

    private record TaskParameters(int groupsCount,
                                  int packageGroupWeight,
                                  int minPossibleGroupSize,
                                  int maxPossibleGroupSize,
                                  List<Integer> packageWeights) {

        public TaskParameters(int groupsCount, List<Integer> packageWeights) {
            int weightOfSingleParcel = sumIntegers(packageWeights) / groupsCount;
            int minPossibleGroupSize = findMinimalPossibleGroupCount(packageWeights, weightOfSingleParcel);
            this(
                    groupsCount,
                    weightOfSingleParcel,
                    minPossibleGroupSize,
                    packageWeights.size() - (groupsCount - 1) * minPossibleGroupSize,
                    List.copyOf(packageWeights));
        }

        public TaskParameters setPackageWeights(List<Integer> packageWeights) {
            return new TaskParameters(groupsCount, packageGroupWeight, minPossibleGroupSize, maxPossibleGroupSize, packageWeights);
        }

        private TaskParameters removeUsedGroup(List<Integer> group) {
            ArrayList<Integer> packageWeightsCopy = new ArrayList<>(packageWeights);
            packageWeightsCopy.removeAll(group);
            return new TaskParameters(groupsCount - 1, packageGroupWeight, minPossibleGroupSize, maxPossibleGroupSize, packageWeightsCopy);
        }

        private static int findMinimalPossibleGroupCount(List<Integer> packageWeights, int packageGroupWeight) {
            int j = packageWeights.size() - 1;
            for (; j >= 0; j--) {
                if (sumIntegers(packageWeights.subList(j, packageWeights.size())) >= packageGroupWeight)
                    break;
            }
            return packageWeights.size() - j;
        }
    }

    private static int sumIntegers(List<Integer> integers) {
        return integers.stream().mapToInt(Integer::intValue).sum();
    }

    private static long quantumEntanglement(List<Integer> group) {
        return group.stream().mapToLong(Integer::longValue).reduce(1L, Math::multiplyExact);
    }
}