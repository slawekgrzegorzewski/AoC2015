package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;
import com.google.common.base.Splitter;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day24 {
    public static final Comparator<Group> DEFENDER_PICK_ORDER = Comparator.comparingLong(Group::effectivePower)
            .thenComparingLong(Group::initiative)
            .reversed();
    public static final Comparator<Group> ATTACK_ORDER = Comparator.comparingLong(Group::initiative).reversed();
    private final Day24.ImmuneSystem immuneSystem;

    public Day24() throws IOException {
        this(Input.day24());
    }

    public Day24(Day24.ImmuneSystem immuneSystem) throws IOException {
        this.immuneSystem = immuneSystem;
    }

    long part1() {
        HashSet<Group> immuneSystemGroup = new HashSet<>();
        HashSet<Group> infectionGroup = new HashSet<>();
        prepareBattle(immuneSystemGroup, infectionGroup, 0L);
        battle(infectionGroup, immuneSystemGroup);
        return Stream.concat(immuneSystemGroup.stream(), infectionGroup.stream())
                .mapToLong(group -> group.units)
                .sum();
    }

    long part2() {
        long boost = 0;
        HashSet<Group> immuneSystemGroup = new HashSet<>();
        HashSet<Group> infectionGroup = new HashSet<>();
        do {
            boost = boost == 0 ? 1 : boost * 2;
            prepareBattle(immuneSystemGroup, infectionGroup, boost);
            battle(infectionGroup, immuneSystemGroup);
        } while (!battleIsWon(immuneSystemGroup, infectionGroup));
        long from = boost / 2;
        long lastWinScore = 0;
        while (from + 1 != boost) {
            long middle = from + (boost - from) / 2;
            prepareBattle(immuneSystemGroup, infectionGroup, middle);
            battle(infectionGroup, immuneSystemGroup);
            if (battleIsWon(immuneSystemGroup, infectionGroup)) {
                boost = middle;
                lastWinScore = immuneSystemGroup.stream()
                        .mapToLong(group -> group.units)
                        .sum();
            } else {
                from = middle;
            }
        }
        return lastWinScore;
    }

    private void prepareBattle(HashSet<Group> immuneSystemGroup, HashSet<Group> infectionGroup, long boost) {
        immuneSystemGroup.clear();
        for (Group group : immuneSystem.immuneSystem()) {
            immuneSystemGroup.add(new Group(group));
        }
        for (Group group1 : immuneSystemGroup) {
            group1.boost(boost);
        }
        infectionGroup.clear();
        for (Group group : immuneSystem.infection()) {
            infectionGroup.add(new Group(group));
        }
    }

    private static boolean battleIsWon(HashSet<Group> immuneSystemGroup, HashSet<Group> infectionGroup) {
        return !immuneSystemGroup.isEmpty() && infectionGroup.isEmpty();
    }

    private static void battle(HashSet<Group> infectionGroup, HashSet<Group> immuneSystemGroup) {
        PriorityQueue<Group> enemyPickOrder = new PriorityQueue<>(DEFENDER_PICK_ORDER);
        PriorityQueue<Group> attackOrder = new PriorityQueue<>(ATTACK_ORDER);
        long unitsKilled = Long.MAX_VALUE;
        while (!infectionGroup.isEmpty() && !immuneSystemGroup.isEmpty() && unitsKilled > 0) {
            unitsKilled = 0;
            Map<Group, Group> toAttack = new HashMap<>();
            enemyPickOrder.addAll(immuneSystemGroup);
            enemyPickOrder.addAll(infectionGroup);
            while (!enemyPickOrder.isEmpty()) {
                Group attacker = enemyPickOrder.poll();
                HashSet<Group> enemyArmy = immuneSystemGroup.contains(attacker) ? infectionGroup : immuneSystemGroup;
                enemyArmy.stream()
                        .filter(Predicate.not(toAttack::containsValue))
                        .filter(group -> attacker.effectivePowerAgainst(group) > 0)
                        .max(Comparator.comparingLong(attacker::effectivePowerAgainst)
                                .thenComparingLong(Group::effectivePower)
                                .thenComparing(Group::initiative))
                        .ifPresent(enemyGroup -> toAttack.put(attacker, enemyGroup));

            }
            attackOrder.addAll(immuneSystemGroup);
            attackOrder.addAll(infectionGroup);
            while (!attackOrder.isEmpty()) {
                Group attacker = attackOrder.poll();
                Group defender = toAttack.get(attacker);
                if (defender == null) continue;
                unitsKilled += defender.takeDamage(attacker.effectivePowerAgainst(defender));
                if (defender.units() == 0) {
                    immuneSystemGroup.remove(defender);
                    infectionGroup.remove(defender);
                }
            }
        }
    }

    public record ImmuneSystem(Set<Group> immuneSystem, Set<Group> infection) {

    }

    public static final class Group {
        public static final Pattern parser = Pattern.compile("(.*) units each with (\\d+) hit points (\\(.*\\) )?with an attack that does (\\d+) (\\w+) damage at initiative (\\d+)");
        private final String description;
        private long units;
        private final long hitPoints;
        private final EnumSet<AttackType> weaknesses;
        private final EnumSet<AttackType> immunities;
        private final AttackType attackType;
        private long damage;
        private final long initiative;

        public Group(String description, long units, long hitPoints, EnumSet<AttackType> weaknesses, EnumSet<AttackType> immunities,
                     AttackType attackType, long damage, long initiative) {
            this.description = description;
            this.units = units;
            this.hitPoints = hitPoints;
            this.weaknesses = weaknesses;
            this.immunities = immunities;
            this.attackType = attackType;
            this.damage = damage;
            this.initiative = initiative;
        }

        public Group(Group group) {
            this(
                    group.description,
                    group.units,
                    group.hitPoints,
                    EnumSet.copyOf(group.weaknesses),
                    EnumSet.copyOf(group.immunities),
                    group.attackType,
                    group.damage,
                    group.initiative);
        }

        public static Group parse(String description, String value) {
            Matcher matcher = parser.matcher(value);
            if (!matcher.find())
                throw new IllegalArgumentException("Invalid input");
            EnumSet<AttackType>[] weaknessesAndImmunities = parseWeaknessesAndImmunities(matcher.group(3));
            return new Group(description,
                    Long.parseLong(matcher.group(1)),
                    Long.parseLong(matcher.group(2)),
                    weaknessesAndImmunities[0],
                    weaknessesAndImmunities[1],
                    AttackType.valueOf(matcher.group(5).toUpperCase()),
                    Long.parseLong(matcher.group(4)),
                    Long.parseLong(matcher.group(6)));
        }

        private static EnumSet<AttackType>[] parseWeaknessesAndImmunities(String value) {
            Set<AttackType> weaknesses = new HashSet<>();
            Set<AttackType> immunities = new HashSet<>();
            String[] split = value == null ? new String[]{} : value.substring(1, value.length() - 2).split("; ");
            if (split.length > 0) {
                (split[0].startsWith("weak to ") ? weaknesses : immunities)
                        .addAll(toWeaknessesOrImmunitiesSet(split[0]));
            }
            if (split.length > 1) {
                (split[1].startsWith("weak to ") ? weaknesses : immunities)
                        .addAll(toWeaknessesOrImmunitiesSet(split[1]));
            }
            return new EnumSet[]{
                    weaknesses.isEmpty() ? EnumSet.noneOf(AttackType.class) : EnumSet.copyOf(weaknesses),
                    immunities.isEmpty() ? EnumSet.noneOf(AttackType.class) : EnumSet.copyOf(immunities)
            };
        }

        private static Set<AttackType> toWeaknessesOrImmunitiesSet(String value) {
            return Splitter.on(", ")
                    .trimResults()
                    .omitEmptyStrings()
                    .splitToStream(value
                            .replace("weak to ", "")
                            .replace("immune to ", ""))
                    .map(String::toUpperCase)
                    .map(AttackType::valueOf)
                    .collect(Collectors.toSet());
        }

        public long effectivePower() {
            return units * damage;
        }

        public boolean isImmuneTo(AttackType attackType) {
            return immunities.contains(attackType);
        }

        public boolean isImmuneToAttackFrom(Group attacker) {
            return isImmuneTo(attacker.attackType());
        }

        public boolean isWeakTo(AttackType attackType) {
            return weaknesses.contains(attackType);
        }

        public boolean isWeakToAttackFrom(Group attacker) {
            return isWeakTo(attacker.attackType());
        }

        public long effectivePowerAgainst(Group enemy) {
            return enemy.isImmuneToAttackFrom(this) ? 0 : (enemy.isWeakToAttackFrom(this) ? 2L : 1L) * effectivePower();
        }

        public long takeDamage(long effectiveDamage) {
            long unitsBefore = units;
            this.units -= Math.divideExact(effectiveDamage, hitPoints);
            if (units <= 0) units = 0;
            return unitsBefore - units;
        }

        public long units() {
            return units;
        }

        public AttackType attackType() {
            return attackType;
        }

        public long initiative() {
            return initiative;
        }

        public void boost(long boost) {
            this.damage += boost;
        }

        @Override
        public String toString() {
            return String.format("%s - %d units each with %d hit points (weak to %s; immune to %s) with an attack that does %d %s damage at initiative %d",
                    description, units, hitPoints, weaknesses.stream().map(AttackType::name).collect(Collectors.joining(", ")),
                    immunities.stream().map(AttackType::name).collect(Collectors.joining(", ")), damage, attackType.name(), initiative);
        }
    }

    public enum AttackType {
        BLUDGEONING, RADIATION, COLD, SLASHING, FIRE
    }
}
