package com.adventofcode.y2015;

import com.adventofcode.y2015.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

import static com.adventofcode.y2015.Utils.divideIntsAndCeil;

public class Day21 {

    private static final Map<String, ItemStats> weapons = Map.of(
            "Dagger", new ItemStats(8, 4, 0),
            "Shortsword", new ItemStats(10, 5, 0),
            "Warhammer", new ItemStats(25, 6, 0),
            "Longsword", new ItemStats(40, 7, 0),
            "Greataxe", new ItemStats(74, 8, 0)
    );

    private static final Map<String, ItemStats> armors = Map.of(
            "Leather", new ItemStats(13, 0, 1),
            "Chainmail", new ItemStats(31, 0, 2),
            "Splintmail", new ItemStats(53, 0, 3),
            "Bandedmail", new ItemStats(75, 0, 4),
            "Platemail", new ItemStats(102, 0, 5)
    );

    public static final int PLAYER_HIT_POINTS = 100;
    private static final Map<String, ItemStats> rings = Map.of(
            "Damage +1", new ItemStats(25, 1, 0),
            "Damage +2", new ItemStats(50, 2, 0),
            "Damage +3", new ItemStats(PLAYER_HIT_POINTS, 3, 0),
            "Defense +1", new ItemStats(20, 0, 1),
            "Defense +2", new ItemStats(40, 0, 2),
            "Defense +3", new ItemStats(80, 0, 3)
    );

    private final Input.Stats bossStats;
    private final List<ItemSet> allSetups;

    public Day21() throws IOException {
        bossStats = Input.day21();
        allSetups = generateAllCombinations();
    }

    long part1() {
        return allSetups
                .stream()
                .filter(setup -> simulateFight(setup.getStats(), bossStats) == -1)
                .mapToInt(ItemSet::getCost)
                .min()
                .orElseThrow();
    }

    long part2() {
        return allSetups
                .stream()
                .filter(setup -> simulateFight(setup.getStats(), bossStats) == 1)
                .mapToInt(ItemSet::getCost)
                .max()
                .orElseThrow();
    }

    private List<ItemSet> generateAllCombinations() {
        List<ItemSet> allCombinations = new ArrayList<>();
        List<List<String>> ringCombinations = new ArrayList<>();
        List<String> ringList = new ArrayList<>(rings.keySet());
        ringCombinations.add(List.of());
        for (String s : ringList) {
            ringCombinations.add(List.of(s));
        }
        for (int i = 0; i < ringList.size(); i++) {
            for (int j = i + 1; j < ringList.size(); j++) {
                ringCombinations.add(List.of(
                        ringList.get(i),
                        ringList.get(j)
                ));
            }
        }

        for (String weapon : weapons.keySet()) {
            for (List<String> rings : ringCombinations) {
                allCombinations.add(new ItemSet(weapon, null, rings));
            }

            for (String armor : armors.keySet()) {
                for (List<String> rings : ringCombinations) {
                    allCombinations.add(new ItemSet(weapon, armor, rings));
                }
            }
        }
        return allCombinations;
    }

    private int simulateFight(Input.Stats firstOpponent, Input.Stats secondOpponent) {
        int firstOpponentDealtDamage = getDealtDamage(firstOpponent, secondOpponent);
        int secondOpponentDealtDamage = getDealtDamage(secondOpponent, firstOpponent);
        int firstOpponentKillRound = divideIntsAndCeil(firstOpponent.hitPoints(), secondOpponentDealtDamage);
        int secondOpponentKillRound = divideIntsAndCeil(secondOpponent.hitPoints(), firstOpponentDealtDamage);
        return firstOpponentKillRound >= secondOpponentKillRound ? -1 : 1;
    }

    private static int getDealtDamage(Input.Stats firstOpponent, Input.Stats secondOpponent) {
        return Math.max(firstOpponent.damage() - secondOpponent.armor(), 1);
    }

    private record ItemStats(int cost, int damage, int armor) {
    }

    private record ItemSet(String weapon, String armor, List<String> rings) {
        Input.Stats getStats() {
            return new Input.Stats(
                    PLAYER_HIT_POINTS,
                    sumStat(this, ItemStats::damage),
                    sumStat(this, ItemStats::armor)
            );
        }

        int getCost() {
            return sumStat(this, ItemStats::cost);
        }
    }

    private static int sumStat(ItemSet itemSet, ToIntFunction<ItemStats> statExtractor) {
        return Stream.of(
                        Stream.of(itemSet.weapon()).map(weapons::get),
                        (itemSet.armor() == null ? Stream.<ItemStats>empty() : Stream.of(itemSet.armor()).map(armors::get)),
                        itemSet.rings().stream().map(rings::get)
                )
                .flatMap(Function.identity())
                .mapToInt(statExtractor)
                .sum();
    }
}