package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Integer.numberOfTrailingZeros;

public class Day11 {

    private static final Pattern FLOOR_PATTERN = Pattern.compile("The ([a-z]+) floor contains (.*)\\.");
    private static final Pattern EQUIPMENT_PATTERN = Pattern.compile("a ([a-z]+)(-compatible microchip| generator)");
    private final List<String> input;


    public Day11() throws IOException {
        this.input = Input.day11();
    }

    long part1() {
        Arrangement arrangement = parse();
        return work(arrangement);
    }

    long part2() {
        Arrangement arrangement = parse();
        arrangement = new Arrangement(
                arrangement.firstFloorElements | 0b11110000000000,
                arrangement.secondFloorElements,
                arrangement.thirdFloorElements,
                arrangement.fourthFloorElements,
                1
        );
        return work(arrangement);
    }

    private int work(Arrangement arrangement) {
        Set<Arrangement> allArrangements = new HashSet<>();
        Set<Arrangement> arrangementsCreatedInStep = new HashSet<>();
        Set<Arrangement> workingArrangements = new HashSet<>();

        allArrangements.add(arrangement);
        workingArrangements.add(arrangement);

        int cost = 0;
        while (!workingArrangements.isEmpty()) {
            for (Arrangement workingArrangement : workingArrangements) {
                if (workingArrangement.allElementsAtLastFloor()) {
                    return cost;
                }
                Set<Arrangement> nextArrangements = getNextArrangements(allArrangements, workingArrangement);
                arrangementsCreatedInStep.addAll(nextArrangements);
            }
            workingArrangements.clear();
            workingArrangements.addAll(arrangementsCreatedInStep);
            arrangementsCreatedInStep.clear();
            cost++;
        }
        throw new RuntimeException("No solution found");
    }

    private Set<Arrangement> getNextArrangements(Set<Arrangement> allArrangements, Arrangement arrangement) {
        Set<Arrangement> newArrangements = new HashSet<>();
        int floorPlan = arrangement.elevatorFloorElements();
        for (int cargo = floorPlan; cargo != 0; cargo = clearLowestSetBit(cargo)) {
            int singleElementCargo = lowestSetBitMask(cargo);
            addNewArrangementIfValid(allArrangements, arrangement, newArrangements, singleElementCargo);
            for (int remaining = clearLowestSetBit(cargo); remaining != 0; remaining = clearLowestSetBit(remaining)) {
                int pairCargo = singleElementCargo | lowestSetBitMask(remaining);
                addNewArrangementIfValid(allArrangements, arrangement, newArrangements, pairCargo);
            }
        }
        return newArrangements;
    }

    private static int lowestSetBitMask(int number) {
        return number & -number;
    }

    private static int clearLowestSetBit(int number) {
        return number & number - 1;
    }

    private static void addNewArrangementIfValid(Set<Arrangement> allArrangements, Arrangement arrangement, Set<Arrangement> newArrangements, int cargo) {
        if (arrangement.canMoveDown()) {
            Arrangement movedDown = arrangement.moveDown(cargo);
            if (movedDown.allElementsIntact() && !allArrangements.contains(movedDown)) {
                newArrangements.add(movedDown);
                allArrangements.add(movedDown);
            }
        }
        if (arrangement.canMoveUp()) {
            Arrangement movedUp = arrangement.moveUp(cargo);
            if (movedUp.allElementsIntact() && !allArrangements.contains(movedUp)) {
                newArrangements.add(movedUp);
                allArrangements.add(movedUp);
            }
        }
    }

    private Arrangement parse() {
        Map<Integer, List<String>> floorsMap = new HashMap<>();
        for (String line : input) {
            Matcher matcher = FLOOR_PATTERN.matcher(line);
            if (!matcher.find()) throw new IllegalArgumentException("Invalid input");
            int floor = getNumber(matcher.group(1));
            floorsMap.putIfAbsent(floor, new ArrayList<>());
            Matcher matcher1 = EQUIPMENT_PATTERN.matcher(matcher.group(2));
            while (matcher1.find()) {
                floorsMap.get(floor).add(
                        ((Character) matcher1.group(1).charAt(0)).toString().toUpperCase()
                                + (matcher1.group(2).equals(" generator") ? "G" : "M")
                );
            }
        }
        List<String> elementLetters = floorsMap.values().stream()
                .flatMap(List::stream)
                .map(element -> String.valueOf(element.charAt(0)))
                .distinct()
                .sorted()
                .toList();
        Map<Integer, Integer> floorsMapAsInteger = floorsMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> addElements(0, translateLetters(elementLetters, entry.getValue()))));

        return new Arrangement(floorsMapAsInteger.get(1),
                floorsMapAsInteger.get(2),
                floorsMapAsInteger.get(3),
                floorsMapAsInteger.get(4),
                1);
    }

    private List<String> translateLetters(List<String> elementLetters, List<String> value) {
        return value.stream()
                .map(element -> String.valueOf((char) ('A' + elementLetters.indexOf(String.valueOf(element.charAt(0))))) + element.charAt(1))
                .toList();
    }

    private static int addElements(int newFloorElements, List<String> elements) {
        for (String element : elements) {
            int elementIndex = element.charAt(0) - 'A';
            int elementOffset = element.charAt(1) == 'G' ? 0 : 1;
            newFloorElements |= 1 << (elementIndex * 2 + elementOffset);
        }
        return newFloorElements;
    }


    private int getNumber(String orderNumber) {
        return switch (orderNumber) {
            case "first" -> 1;
            case "second" -> 2;
            case "third" -> 3;
            case "fourth" -> 4;
            default -> throw new IllegalArgumentException("Invalid input");
        };
    }

    private record Arrangement(int firstFloorElements,
                               int secondFloorElements,
                               int thirdFloorElements,
                               int fourthFloorElements,
                               FloorStats firstFloorStats,
                               FloorStats secondFloorStats,
                               FloorStats thirdFloorStats,
                               FloorStats fourthFloorStats,
                               int numberOfElements,
                               int elevatorFloor,
                               int hash) {

        public Arrangement(int firstFloorElements,
                           int secondFloorElements,
                           int thirdFloorElements,
                           int fourthFloorElements,
                           int elevatorFloor) {
            FloorStats firstFloorStatsToSet = new FloorStats(firstFloorElements);
            FloorStats secondFloorStatsToSet = new FloorStats(secondFloorElements);
            FloorStats thirdFloorStatsToSet = new FloorStats(thirdFloorElements);
            FloorStats fourthFloorStatsToSet = new FloorStats(fourthFloorElements);
            int numberOfElementsToSet = numberOfTrailingZeros(firstFloorElements + secondFloorElements + thirdFloorElements + fourthFloorElements + 1) / 2;
            this(firstFloorElements,
                    secondFloorElements,
                    thirdFloorElements,
                    fourthFloorElements,
                    firstFloorStatsToSet,
                    secondFloorStatsToSet,
                    thirdFloorStatsToSet,
                    fourthFloorStatsToSet,
                    numberOfElementsToSet,
                    elevatorFloor,
                    Objects.hash(firstFloorStatsToSet,
                            secondFloorStatsToSet,
                            thirdFloorStatsToSet,
                            fourthFloorStatsToSet,
                            numberOfElementsToSet,
                            elevatorFloor));
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Arrangement that = (Arrangement) o;
            return elevatorFloor == that.elevatorFloor
                    && firstFloorStats.equals(that.firstFloorStats)
                    && secondFloorStats.equals(that.secondFloorStats)
                    && thirdFloorStats.equals(that.thirdFloorStats)
                    && fourthFloorStats.equals(that.fourthFloorStats);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        public boolean allElementsIntact() {
            return firstFloorStats.isIntact()
                    && secondFloorStats.isIntact()
                    && thirdFloorStats.isIntact()
                    && fourthFloorStats.isIntact();
        }

        public boolean allElementsAtLastFloor() {
            return fourthFloorElements == ((1 << 2 * numberOfElements) - 1);
        }

        public Integer elevatorFloorElements() {
            return switch (elevatorFloor) {
                case 1 -> firstFloorElements;
                case 2 -> secondFloorElements;
                case 3 -> thirdFloorElements;
                case 4 -> fourthFloorElements;
                default -> throw new RuntimeException();
            };
        }

        public boolean canMoveUp() {
            return elevatorFloor < 4;
        }

        public Arrangement moveUp(int elements) {
            return moveToFloor(elements, elevatorFloor + 1);
        }

        public boolean canMoveDown() {
            return elevatorFloor > 1;
        }

        public Arrangement moveDown(int elements) {
            return moveToFloor(elements, elevatorFloor - 1);
        }

        private Arrangement moveToFloor(int elements, int nextFloor) {
            if (nextFloor > 4 || nextFloor < 1) throw new RuntimeException();
            if (elements == 0) throw new RuntimeException();
            int[] floors = {firstFloorElements, secondFloorElements, thirdFloorElements, fourthFloorElements};
            floors[elevatorFloor - 1] -= elements;
            floors[nextFloor - 1] += elements;
            return new Arrangement(floors[0], floors[1], floors[2], floors[3], nextFloor);
        }
    }

    private record FloorStats(int pairs, int generatorsOnly, int microchipsOnly) {

        private static final int GENERATOR_MASK = 0b1010101010101010101010101010101;
        private static final int MICROCHIP_MASK = 0b10101010101010101010101010101010;

        public FloorStats(int floorMap) {
            int pairs = Integer.bitCount(floorMap & (floorMap >> 1) & GENERATOR_MASK);
            int totalGenerators = Integer.bitCount(floorMap & GENERATOR_MASK);
            int totalMicrochips = Integer.bitCount(floorMap & MICROCHIP_MASK);
            if (pairs > 7 || totalGenerators > 7 || totalMicrochips > 7)
                throw new RuntimeException();
            this(pairs, totalGenerators - pairs, totalMicrochips - pairs);
        }

        public boolean isIntact() {
            return microchipsOnly == 0 || generatorsOnly == 0;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            return hashCode() == o.hashCode();
        }

        @Override
        public int hashCode() {
            return pairs | generatorsOnly << 3 | microchipsOnly << 6;
        }
    }
}
