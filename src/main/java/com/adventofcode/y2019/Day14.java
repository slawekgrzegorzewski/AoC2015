package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;
import com.google.common.base.Splitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day14 {
    private final int ORE_ID;
    private final Map<String, Chemical> chemicals = new HashMap<>();
    private final Map<Integer, Reaction> productToReaction;

    public Day14() throws IOException {
        List<Reaction> reactions = Input.day14();
        for (Reaction reaction : reactions) {
            chemicals.putIfAbsent(reaction.output().name(), reaction.output());
            for (Chemical chemical : reaction.inputs()) {
                chemicals.put(chemical.name(), chemical);
            }
        }
        ORE_ID = chemicals.get("ORE").id();
        this.productToReaction = reactions.stream()
                .collect(Collectors.toMap(
                        reaction -> reaction.output().id(),
                        reaction -> reaction
                ));
    }

    long part1() {
        return oreRequired(1);
    }

    long part2() {
        long oreCount = 1000000000000L;
        long upperBound = findUpperBound(oreCount);
        return binarySearch(upperBound / 2, upperBound, oreCount);
    }

    private int findUpperBound(long oreCount) {
        int upperBound = 1;
        long oreRequired;
        do {
            upperBound *= 2;
            oreRequired = oreRequired(upperBound);
        } while (oreRequired <= oreCount);
        return upperBound;
    }

    private long binarySearch(long lowerBound, long upperBound, long oreCount) {
        while (upperBound - lowerBound > 1) {
            long mid = lowerBound + (upperBound - lowerBound) / 2;
            if (oreRequired(mid) <= oreCount) {
                lowerBound = mid;
            } else {
                upperBound = mid;
            }
        }
        for (long i = upperBound; i >= lowerBound; i--) {
            if (oreRequired(i) <= oreCount)
                return i;
        }
        throw new IllegalStateException("Could not find fuel quantity");
    }

    public long oreRequired(long fuelQuantity) {
        long[] stashedChemicals = new long[chemicals.size()];
        long[] chemicalsToProduce = new long[chemicals.size()];
        chemicalsToProduce[chemicals.get("FUEL").id()] = fuelQuantity;
        while (countNonZeroNonOre(chemicalsToProduce) > 0) {
            long[] newChemicalsToProduce = new long[chemicals.size()];
            for (int chemicalId = 0; chemicalId < chemicalsToProduce.length; chemicalId++) {
                long neededQuantity = chemicalsToProduce[chemicalId];
                if (ORE_ID == chemicalId) {
                    newChemicalsToProduce[chemicalId] += neededQuantity;
                    continue;
                }
                neededQuantity -= takeFromStash(chemicalId, neededQuantity, stashedChemicals);
                if (neededQuantity == 0)
                    continue;
                Reaction reaction = productToReaction.get(chemicalId);
                long reactionMultiplier = Math.ceilDiv(neededQuantity, reaction.output().quantity());
                long reactionOutput = reactionMultiplier * reaction.output().quantity();
                stashedChemicals[chemicalId] += (reactionOutput - neededQuantity);
                for (Chemical chemical : reaction.inputs()) {
                    long neededInputQuantity = reactionMultiplier * chemical.quantity();
                    neededInputQuantity -= takeFromStash(chemical.id(), neededInputQuantity, stashedChemicals);
                    newChemicalsToProduce[chemical.id()] += neededInputQuantity;
                }
            }
            chemicalsToProduce = newChemicalsToProduce;
        }
        return chemicalsToProduce[ORE_ID];
    }

    private int countNonZeroNonOre(long[] chemicalsToProduce) {
        int count = 0;
        for (int i = 0; i < chemicalsToProduce.length; i++) {
            if (i == ORE_ID)
                continue;
            if (chemicalsToProduce[i] > 0) count++;
        }
        return count;
    }

    private long takeFromStash(int chemicalId, long neededQuantity, long[] stashedChemicals) {
        long available = stashedChemicals[chemicalId];
        long taken = Math.min(available, neededQuantity);
        stashedChemicals[chemicalId] = available - taken;
        return taken;
    }

    public record Chemical(int id, String name, long quantity) {
        public static Chemical parse(String value, Map<String, Integer> chemicalIdMap) {
            if (value.contains(" ")) {
                String[] parts = value.split(" ");
                int id = chemicalIdMap.computeIfAbsent(parts[1], _ -> chemicalIdMap.size());
                return new Chemical(id, parts[1], Long.parseLong(parts[0]));
            }
            int id = chemicalIdMap.computeIfAbsent(value, _ -> chemicalIdMap.size());
            return new Chemical(id, value, 1);
        }
    }

    public record Reaction(List<Chemical> inputs, Chemical output) {
        public static Reaction parse(String value, Map<String, Integer> chemicalIdMap) {
            String[] parts = value.split(" => ");
            return new Reaction(
                    Splitter.on(", ")
                            .trimResults()
                            .splitToStream(parts[0])
                            .map(inputElement -> Chemical.parse(inputElement, chemicalIdMap))
                            .toList(),
                    Chemical.parse(parts[1], chemicalIdMap));
        }
    }
}
