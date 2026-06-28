package com.adventofcode;

import com.adventofcode.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Day22 {

    private final Input.Stats bossStats;

    private static final Effect EMPTY_EFFECT = new Effect(0, 0, 0, 0);
    private static final Effect SHIELD_EFFECT = new Effect(6, 7, 0, 0);
    private static final Effect POISON_EFFECT = new Effect(6, 0, 3, 0);
    private static final Effect RECHARGE_EFFECT = new Effect(5, 0, 0, 101);
    private static final Map<Effect, String> effectNames = Map.of(
            EMPTY_EFFECT, "no effect",
            SHIELD_EFFECT, "Shield",
            POISON_EFFECT, "Poison",
            RECHARGE_EFFECT, "Recharge"
    );
    private static final Map<String, Spell> spells = Map.of(
            "Magic Missile", new Spell("Magic Missile", 53, 4, 0, EMPTY_EFFECT),
            "Drain", new Spell("Drain", 73, 2, 2, EMPTY_EFFECT),
            "Shield", new Spell("Shield", 113, 0, 0, SHIELD_EFFECT),
            "Poison", new Spell("Poison", 173, 0, 0, POISON_EFFECT),
            "Recharge", new Spell("Recharge", 229, 0, 0, RECHARGE_EFFECT)
    );

    public Day22() throws IOException {
        bossStats = Input.day22();
    }

    long part1() {
        return checkAllGamesVariants(false);
    }

    long part2() {
        return checkAllGamesVariants(true);
    }

    private int checkAllGamesVariants(boolean looseOneHPOnPlayerTurn) {
        final Set<GameStateNode> allNodes = new HashSet<>();
        final GameStateNode winNode = new GameStateNode(null, List.of(), false, new AtomicInteger(0));
        final GameStateNode looseNode = new GameStateNode(null, List.of(), false, new AtomicInteger(0));
        final GameState initialGameState = new GameState(bossStats, new Input.Stats(50, 0, 0), 500, Map.of());
        final GameStateNode initialNode = new GameStateNode(initialGameState, new ArrayList<>(), true, new AtomicInteger(0));
        allNodes.add(winNode);
        allNodes.add(looseNode);
        allNodes.add(initialNode);
        Set<GameStateNode> nodesForNextRound = new HashSet<>();
        nodesForNextRound.add(initialNode);
        do {
            Set<GameStateNode> workingNodes = new HashSet<>(nodesForNextRound);
            nodesForNextRound.clear();
            workingNodes.forEach(node -> {
                if (node == winNode || node == looseNode) return;
                if (node.gameState.gameOver()) {
                    node.children().clear();
                    if (node.gameState.playerWon()) {
                        if (winNode.manaCost().intValue() == 0 || node.manaCost().intValue() < winNode.manaCost().intValue())
                            winNode.manaCost().set(node.manaCost().intValue());
                        node.children().add(winNode);
                    } else {
                        node.children().add(looseNode);
                    }
                } else if (node.playerTurn) {
                    List<String> possibleSpells = node.gameState.possibleSpells();
                    if (possibleSpells.isEmpty()) {
                        node.children().add(looseNode);
                    } else {
                        possibleSpells.forEach(spellName -> {
                            int manaCost = spells.get(spellName).manaCost();
                            GameStateNode newNode = new GameStateNode(node.gameState.performTurn(spellName, looseOneHPOnPlayerTurn, false), new ArrayList<>(), false, new AtomicInteger(node.manaCost().intValue() + manaCost));
                            if (allNodes.contains(newNode)) return;
                            allNodes.add(newNode);
                            nodesForNextRound.add(newNode);
                            node.children().add(newNode);
                        });
                    }
                } else {
                    GameStateNode newNode = new GameStateNode(node.gameState.performTurn(null, looseOneHPOnPlayerTurn, false), new ArrayList<>(), true, new AtomicInteger(node.manaCost().intValue()));
                    if (allNodes.contains(newNode)) return;
                    allNodes.add(newNode);
                    nodesForNextRound.add(newNode);
                    node.children().add(newNode);
                }
            });
        } while (!nodesForNextRound.isEmpty());
        return winNode.manaCost().intValue();
    }

    private static GameState firstGameSimulation() {
        GameState gameState = new GameState(
                new Input.Stats(13, 8, 0),
                new Input.Stats(10, 0, 0),
                250,
                Map.of()
        );
        gameState = gameState.performTurn("Poison", false, true);
        gameState = gameState.performTurn(null, false, true);
        gameState = gameState.performTurn("Magic Missile", false, true);
        return gameState.performTurn(null, false, true);
    }

    private static GameState secondGameSimulation() {
        GameState gameState = new GameState(
                new Input.Stats(14, 8, 0),
                new Input.Stats(10, 0, 0),
                250,
                Map.of()
        );
        gameState = gameState.performTurn("Recharge", false, true);
        gameState = gameState.performTurn(null, false, true);
        gameState = gameState.performTurn("Shield", false, true);
        gameState = gameState.performTurn(null, false, true);
        gameState = gameState.performTurn("Drain", false, true);
        gameState = gameState.performTurn(null, false, true);
        gameState = gameState.performTurn("Poison", false, true);
        gameState = gameState.performTurn(null, false, true);
        gameState = gameState.performTurn("Magic Missile", false, true);
        return gameState.performTurn(null, false, true);
    }

    private record GameStateNode(GameState gameState, List<GameStateNode> children, boolean playerTurn,
                                 AtomicInteger manaCost) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            GameStateNode that = (GameStateNode) o;
            return playerTurn == that.playerTurn && Objects.equals(gameState, that.gameState) && Objects.equals(children, that.children);
        }

        @Override
        public int hashCode() {
            return Objects.hash(gameState, children, playerTurn);
        }
    }

    private record Effect(int turns, int armorBonus, int damage, int manaBonus) {
        public String debugString(int currentTurns) {
            if (manaBonus > 0)
                return effectNames.get(this) + " provides " + manaBonus + " mana; its timer is now " + currentTurns + ".";
            else if (damage > 0)
                return effectNames.get(this) + " deals " + damage + " damage; its timer is now " + currentTurns + ".";
            return effectNames.get(this) + "'s  timer is now " + currentTurns + ".";
        }
    }

    private record Spell(String name, int manaCost, int damage, int healing, Effect effect) {
        public String debugString() {
            if (damage > 0 && healing > 0) {
                return ", dealing " + damage + " damage and healing " + healing + " hit points.";
            }
            if (damage > 0) {
                return ", dealing " + damage + " damage.";
            }
            return "";
        }
    }

    private record GameState(
            Input.Stats bossStats,
            Input.Stats playerStats,
            int playerMana,
            Map<Effect, Integer> activeEffects
    ) {
        boolean gameOver() {
            return playerWon() || bossWon();
        }

        boolean playerWon() {
            return bossStats.hitPoints() <= 0 && playerStats.hitPoints() > 0;
        }

        boolean bossWon() {
            return bossStats.hitPoints() > 0 && playerStats.hitPoints() <= 0;
        }

        public List<String> possibleSpells() {
            return spells.entrySet().stream()
                    .filter(entry -> activeEffects.getOrDefault(entry.getValue().effect(), 0) <= 1)
                    .filter(entry -> {
                        int availableMana = playerMana + activeEffects.keySet().stream().mapToInt(Effect::manaBonus).sum();
                        return entry.getValue().manaCost() <= availableMana;
                    })
                    .map(Map.Entry::getKey)
                    .toList();
        }

        private GameState performTurn(String spellName, boolean looseOneHPOnPlayerTurn, boolean debug) {
            if (spellName != null) {
                List<String> possibleSpells = possibleSpells();
                if (possibleSpells.isEmpty()) {
                    if (debug) {
                        System.out.println("The player can't cast any spell. This kills the player, and the boss wins.");
                    }
                    return new GameState(
                            bossStats,
                            new Input.Stats(0, playerStats.damage(), playerStats.armor()),
                            playerMana,
                            activeEffects);
                }
                if (!possibleSpells.contains(spellName)) {
                    throw new IllegalStateException("Spell: " + spellName + " can not be cast");
                }
            }
            if (bossStats.hitPoints() <= 0) return this;
            if (playerStats.hitPoints() <= 0) return this;
            int bossHitPoints = bossStats.hitPoints();
            int playerHitPoints = playerStats.hitPoints();
            if (spellName != null && looseOneHPOnPlayerTurn) {
                playerHitPoints--;
                if (playerHitPoints <= 0) {
                    if (debug) {
                        System.out.println("This kills the player, and the boss wins.");
                    }
                    return new GameState(
                            bossStats,
                            new Input.Stats(0, playerStats.damage(), playerStats.armor()),
                            playerMana,
                            activeEffects);
                }
            }
            int playerMana = playerMana();
            Map<Effect, Integer> activeEffects = new HashMap<>(this.activeEffects);
            int playerArmor = activeEffects.containsKey(SHIELD_EFFECT) ? SHIELD_EFFECT.armorBonus : 0;
            if (debug) {
                if (spellName != null) {
                    System.out.println("-- Player turn --");
                } else {
                    System.out.println("-- Boss turn --");
                }
                System.out.printf("- Player has %d hit points, %d armor, %d mana%n", playerHitPoints, playerArmor, playerMana);
                System.out.printf("- Boss has %d hit points%n", bossHitPoints);
            }
            for (Map.Entry<Effect, Integer> effect : activeEffects.entrySet()) {
                if (debug) {
                    System.out.println(effect.getKey().debugString(effect.getValue() - 1));
                }
                effect.setValue(effect.getValue() - 1);
                playerMana += effect.getKey().manaBonus();
                bossHitPoints -= effect.getKey().damage();
            }
            activeEffects.entrySet().removeIf(entry -> {
                boolean shouldRemove = entry.getValue() <= 0;
                if (shouldRemove && debug) {
                    System.out.println(effectNames.get(entry.getKey()) + " wears off.");
                }
                return shouldRemove;
            });
            if (bossHitPoints <= 0) {
                if (debug) {
                    System.out.println("This kills the boss, and the player wins.");
                }
                return new GameState(
                        new Input.Stats(0, bossStats().damage(), 0),
                        new Input.Stats(playerHitPoints, 0, 0),
                        playerMana,
                        activeEffects
                );
            }
            if (spellName != null) {
                Spell spell = spells.get(spellName);
                if (debug) {
                    System.out.println("Player casts " + spellName + spell.debugString());
                }
                playerMana -= spell.manaCost();
                bossHitPoints -= spell.damage();
                playerHitPoints += spell.healing();
                if (spell.effect() != EMPTY_EFFECT) {
                    activeEffects.put(spell.effect(), spell.effect().turns());
                }
                if (bossHitPoints <= 0) {
                    if (debug) {
                        System.out.println("This kills the boss, and the player wins.");
                    }
                    return new GameState(
                            new Input.Stats(0, bossStats().damage(), 0),
                            new Input.Stats(playerHitPoints, 0, 0),
                            playerMana,
                            activeEffects
                    );
                }
            } else {
                int damageDealt = Math.max(1, bossStats.damage() - playerArmor);
                if (debug) {
                    System.out.println("Boss attacks for " + damageDealt + " damage.");
                }
                playerHitPoints -= damageDealt;
                if (playerHitPoints <= 0) {
                    playerHitPoints = 0;
                    if (debug) {
                        System.out.println("This kills the player, and the boss wins.");
                    }
                }
            }
            if (debug) {
                System.out.println();
            }
            return new GameState(
                    new Input.Stats(bossHitPoints, bossStats().damage(), 0),
                    new Input.Stats(playerHitPoints, 0, 0),
                    playerMana,
                    activeEffects
            );
        }
    }
}