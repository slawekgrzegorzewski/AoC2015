package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;
import com.adventofcode.utils.Utils;

import java.io.IOException;
import java.util.*;

public class Day12 {
    private final List<int[]> positions;

    public Day12() throws IOException {
        this.positions = Input.day12();
    }

    long part1() {
        List<int[]> positions = copy(this.positions);
        List<int[]> speeds = new ArrayList<>();
        for (int[] _ : this.positions) {
            speeds.add(new int[]{0, 0, 0});
        }
        for (int i = 1; i <= 1000; i++) {
            simulateStep(positions, speeds);
        }
        return getTotalEnergy(positions, speeds);
    }

    long part2() {
        List<int[]> positions = copy(this.positions);
        List<int[]> speeds = new ArrayList<>();
        for (int[] _ : this.positions) {
            speeds.add(new int[]{0, 0, 0});
        }
        SixIntegersCycleFinder xCycleFinder = new SixIntegersCycleFinder();
        SixIntegersCycleFinder yCycleFinder = new SixIntegersCycleFinder();
        SixIntegersCycleFinder zCycleFinder = new SixIntegersCycleFinder();
        xCycleFinder.registerValue(new SixIntegers(positions, speeds, 0));
        yCycleFinder.registerValue(new SixIntegers(positions, speeds, 1));
        zCycleFinder.registerValue(new SixIntegers(positions, speeds, 2));
        while (true) {
            simulateStep(positions, speeds);
            xCycleFinder.registerValue(new SixIntegers(positions, speeds, 0));
            yCycleFinder.registerValue(new SixIntegers(positions, speeds, 1));
            zCycleFinder.registerValue(new SixIntegers(positions, speeds, 2));
            if (xCycleFinder.cycleFound() && yCycleFinder.cycleFound() && zCycleFinder.cycleFound()) {break;
            }
        }
        long xPeriod = xCycleFinder.getAllUniqueValuesSize();
        long yPeriod = yCycleFinder.getAllUniqueValuesSize();
        long zPeriod = zCycleFinder.getAllUniqueValuesSize();
        return Utils.lcm(Utils.lcm(xPeriod, yPeriod), zPeriod);
    }

    private long getTotalEnergy(List<int[]> positions, List<int[]> speeds) {
        long totalEnergy = 0;
        for (int i = 0; i < positions.size(); i++) {
            long potentialEnergy = energy(positions.get(i));
            long kineticEnergy = energy(speeds.get(i));
            totalEnergy += potentialEnergy * kineticEnergy;
        }
        return totalEnergy;
    }

    private void print(int steps, List<int[]> positions, List<int[]> speeds) {
        System.out.println("After " + steps + " steps:");
        for (int i = 0; i < positions.size(); i++) {
            System.out.println("pos=" + toString(positions.get(i)) + ", vel=" + toString(speeds.get(i)));
        }
    }

    private long energy(int[] params) {
        return Math.abs(params[0]) + Math.abs(params[1]) + Math.abs(params[2]);
    }

    private String toString(int[] elements) {
        return "<x=" + String.format("%3d", elements[0]) + ", y=" + String.format("%3d", elements[1]) + ", z=" + String.format("%3d", elements[2]) + ">";
    }

    private List<int[]> copy(List<int[]> positions) {
        return new ArrayList<>(positions.stream().map(array -> Arrays.copyOf(array, array.length)).toList());
    }

    private void simulateStep(List<int[]> positions, List<int[]> speeds) {
        for (int i = 0; i < positions.size(); i++) {
            int[] iPosition = positions.get(i);
            for (int j = i + 1; j < positions.size(); j++) {
                int[] jPosition = positions.get(j);
                for (int k = 0; k < iPosition.length; k++) {
                    if (iPosition[k] < jPosition[k]) {
                        speeds.get(i)[k]++;
                        speeds.get(j)[k]--;
                    } else if (iPosition[k] > jPosition[k]) {
                        speeds.get(i)[k]--;
                        speeds.get(j)[k]++;
                    }
                }
            }
        }
        for (int i = 0; i < positions.size(); i++) {
            int[] iPosition = positions.get(i);
            int[] iSpeed = speeds.get(i);
            for (int k = 0; k < iPosition.length; k++) {
                iPosition[k] += iSpeed[k];
            }
        }
    }

    public record SixIntegers(int x1, int x2, int x3, int vx1, int vx2, int vx3) {
        SixIntegers(List<int[]> positions, List<int[]> speeds, int position) {
            this(positions.get(0)[position],
                    positions.get(1)[position],
                    positions.get(2)[position],
                    speeds.get(0)[position],
                    speeds.get(1)[position],
                    speeds.get(2)[position]);
        }
    }

    public static class SixIntegersCycleFinder {
        public final Set<SixIntegers> valuesSet = new HashSet<>();
        public final List<SixIntegers> values = new ArrayList<>();
        public int cycleStart = -1;
        public int currentPosition = -1;

        public int getCycleStart() {
            return cycleStart;
        }

        public int getCycleLength() {
            return cycleStart == -1 ? -1 : values.size() - cycleStart + 1;
        }

        public int getAllUniqueValuesSize() {
            return values.size();
        }

        public void registerValue(SixIntegers value) {
            if (cycleStart == -1) {
                if (valuesSet.contains(value)) {
                    cycleStart = values.indexOf(value);
                    currentPosition = cycleStart;
                } else {
                    values.add(value);
                    valuesSet.add(value);
                    currentPosition++;
                }
            } else {
                currentPosition++;
                if (currentPosition == values.size()) {
                    currentPosition = cycleStart;
                }
                if (!values.get(currentPosition).equals(value)) {
                    throw new IllegalStateException("Cycle broke");
                }
            }
        }

        public boolean cycleFound() {
            return cycleStart != -1;
        }
    }
}
