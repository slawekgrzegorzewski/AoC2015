package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                arrangement.numberOfElements + 2,
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
        Set<Arrangement> newChildren = new HashSet<>();
        int floorPlan = arrangement.elevatorFloorElements();

        for (int cargo = floorPlan; cargo != 0; cargo = clearLowestSetBit(cargo)) {
            int singleElementCargo = lowestSetBitMask(cargo);
            addChildIfValid(allArrangements, arrangement, newChildren, singleElementCargo);
            for (int remaining = clearLowestSetBit(cargo); remaining != 0; remaining = clearLowestSetBit(remaining)) {
                int pairCargo = singleElementCargo | lowestSetBitMask(remaining);
                addChildIfValid(allArrangements, arrangement, newChildren, pairCargo);
            }
        }
        return newChildren;
    }

    private static int lowestSetBitMask(int number) {
        return number & -number;
    }

    private static int clearLowestSetBit(int number) {
        return number & number - 1;
    }

    private static void addChildIfValid(Set<Arrangement> allArrangements, Arrangement arrangement, Set<Arrangement> newArrangements, int cargo) {
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
                elementLetters.size(),
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
                               int numberOfElements,
                               int elevatorFloor) {
        private static final Map<Integer, Byte> OFFSETS = Map.of(
                1, (byte) 0,
                2, (byte) 2,
                3, (byte) 4,
                4, (byte) 6,
                5, (byte) 8,
                6, (byte) 10,
                7, (byte) 12,
                8, (byte) 14);

        @Override
        @NonNull
        public String toString() {
            StringBuilder result = new StringBuilder();
            List<Integer> floors = List.of(fourthFloorElements, thirdFloorElements, secondFloorElements, firstFloorElements);
            for (int i = 0; i < floors.size(); i++) {
                result.append("F")
                        .append(i + 1)
                        .append((i + 1) == (elevatorFloor) ? "\tE\t" : "\t.\t")
                        .append(elementsString(floors.get(i)))
                        .append(i == floors.size() - 1 ? "" : "\n");
            }
            return result.toString();
        }

        private String elementsString(int value) {
            StringBuilder sb = new StringBuilder();
            for (int i = numberOfElements; i > 0; i--) {
                if (!sb.isEmpty()) sb.append("\t");
                char elementLetter = (char) ('A' + i - 1);
                byte elementConfiguration = (byte) ((value >> OFFSETS.get(i)) & 0x3);
                switch (elementConfiguration) {
                    case 0 -> sb.append(".\t.");
                    case 1 -> sb.append(".\t").append(elementLetter).append("G");
                    case 2 -> sb.append(elementLetter).append("M\t.");
                    default -> sb.append(elementLetter).append("M\t").append(elementLetter).append("G");
                }
            }
            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Arrangement that = (Arrangement) o;
            return elevatorFloor == that.elevatorFloor
                    && numberOfElements == that.numberOfElements
                    && new FloorStats(firstFloorElements).equals(new FloorStats(that.firstFloorElements))
                    && new FloorStats(thirdFloorElements).equals(new FloorStats(that.thirdFloorElements))
                    && new FloorStats(secondFloorElements).equals(new FloorStats(that.secondFloorElements))
                    && new FloorStats(fourthFloorElements).equals(new FloorStats(that.fourthFloorElements));
        }

        @Override
        public int hashCode() {
            return Objects.hash(new FloorStats(firstFloorElements), new FloorStats(secondFloorElements), new FloorStats(thirdFloorElements), new FloorStats(fourthFloorElements), numberOfElements, elevatorFloor);
        }

        public boolean allElementsIntact() {
            return Stream.of(firstFloorElements, secondFloorElements, thirdFloorElements, fourthFloorElements)
                    .allMatch(this::floorElementsIntact);
        }

        private boolean floorElementsIntact(int floorElements) {
            boolean containsGenerator = false;
            boolean containsUnshieldedMicrochip = false;
            int elements = floorElements;
            while (elements != 0) {
                byte lastPair = (byte) (elements & 0x3);
                switch (lastPair) {
                    case 3, 1 -> containsGenerator = true;
                    case 2 -> containsUnshieldedMicrochip = true;
                }
                elements >>= 2;
            }
            return !containsGenerator || !containsUnshieldedMicrochip;
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

        public Arrangement moveUp(int elements) {
            return moveToFloor(elements, elevatorFloor + 1);
        }

        public boolean canMoveUp() {
            return elevatorFloor < 4;
        }

        public Arrangement moveDown(int elements) {
            return moveToFloor(elements, elevatorFloor - 1);
        }

        public boolean canMoveDown() {
            return elevatorFloor > 1;
        }

        private Arrangement moveToFloor(int elements, int nextFloor) {
            if (nextFloor > 4 || nextFloor < 1) throw new RuntimeException();
            if (elements == 0) throw new RuntimeException();

            int firstFloorElementsCopy = firstFloorElements;
            int secondFloorElementsCopy = secondFloorElements;
            int thirdFloorElementsCopy = thirdFloorElements;
            int fourthFloorElementsCopy = fourthFloorElements;

            switch (elevatorFloor) {
                case 1 -> firstFloorElementsCopy -= elements;
                case 2 -> secondFloorElementsCopy -= elements;
                case 3 -> thirdFloorElementsCopy -= elements;
                case 4 -> fourthFloorElementsCopy -= elements;
                default -> throw new RuntimeException();
            }

            switch (nextFloor) {
                case 1 -> firstFloorElementsCopy += elements;
                case 2 -> secondFloorElementsCopy += elements;
                case 3 -> thirdFloorElementsCopy += elements;
                case 4 -> fourthFloorElementsCopy += elements;
                default -> throw new RuntimeException();
            }
            return new Arrangement(firstFloorElementsCopy, secondFloorElementsCopy, thirdFloorElementsCopy, fourthFloorElementsCopy, numberOfElements, nextFloor);
        }
    }

    private record FloorStats(int pairs, int generatorsOnly, int microchipsOnly) {
        public FloorStats(int floorMap) {
            int pairs = 0, generatorsOnly = 0, microchipsOnly = 0;
            while (floorMap != 0) {
                int element = floorMap & 0b11;
                switch (element) {
                    case 0b01 -> generatorsOnly++;
                    case 0b10 -> microchipsOnly++;
                    case 0b11 -> pairs++;
                }
                floorMap >>= 2;
            }
            this(pairs, generatorsOnly, microchipsOnly);
        }
    }
}
