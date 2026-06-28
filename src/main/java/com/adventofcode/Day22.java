package com.adventofcode;

import com.adventofcode.input.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Day22 {

    private final Input.Stats bossStats;

    private static final Effect EMPTY_EFFECT = new Effect(0, 0, 0, 0);
    private static final Effect SHIELD_EFFECT = new Effect(6, 7, 0, 0);
    private static final Effect POISON_EFFECT = new Effect(6, 0, 3, 0);
    private static final Effect RECHARGE_EFFECT = new Effect(5, 0, 0, 101);
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
        while (!nodesForNextRound.isEmpty()) {
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
                            GameStateNode newNode = new GameStateNode(node.gameState.performTurn(spellName, looseOneHPOnPlayerTurn), new ArrayList<>(), false, new AtomicInteger(node.manaCost().intValue() + manaCost));
                            if (allNodes.contains(newNode)) return;
                            allNodes.add(newNode);
                            nodesForNextRound.add(newNode);
                            node.children().add(newNode);
                        });
                    }
                } else {
                    GameStateNode newNode = new GameStateNode(node.gameState.performTurn(null, looseOneHPOnPlayerTurn), new ArrayList<>(), true, new AtomicInteger(node.manaCost().intValue()));
                    if (allNodes.contains(newNode)) return;
                    allNodes.add(newNode);
                    nodesForNextRound.add(newNode);
                    node.children().add(newNode);
                }
            });
        }
        return winNode.manaCost().intValue();
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
    }

    private record Spell(String name, int manaCost, int damage, int healing, Effect effect) {
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

        private GameState performTurn(String spellName, boolean looseOneHPOnPlayerTurn) {
            if (spellName != null) {
                List<String> possibleSpells = possibleSpells();
                if (possibleSpells.isEmpty()) {
                    return playerLose();
                }
                if (!possibleSpells.contains(spellName)) {
                    throw new IllegalStateException("Spell: " + spellName + " can not be cast");
                }
            }
            if (gameOver()) return this;

            int bossHitPoints = bossStats.hitPoints();
            int playerHitPoints = playerStats.hitPoints();
            int playerMana = playerMana();
            Map<Effect, Integer> activeEffects = new HashMap<>(this.activeEffects);
            int playerArmor = activeEffects.containsKey(SHIELD_EFFECT) ? SHIELD_EFFECT.armorBonus : 0;

            //hard mode
            if (looseOneHPOnPlayerTurn && spellName != null) {
                playerHitPoints--;
                if (playerHitPoints <= 0) {
                    return playerLose();
                }
            }

            //applying effects
            for (Map.Entry<Effect, Integer> effect : activeEffects.entrySet()) {
                effect.setValue(effect.getValue() - 1);
                playerMana += effect.getKey().manaBonus();
                bossHitPoints -= effect.getKey().damage();
            }
            activeEffects.entrySet().removeIf(entry -> entry.getValue() <= 0);
            if (bossHitPoints <= 0) {
                return playerWins(playerHitPoints, playerMana, activeEffects);
            }

            if (spellName != null) {
                //casting spell
                Spell spell = spells.get(spellName);
                playerMana -= spell.manaCost();
                bossHitPoints -= spell.damage();
                playerHitPoints += spell.healing();
                if (spell.effect() != EMPTY_EFFECT) {
                    activeEffects.put(spell.effect(), spell.effect().turns());
                }
            } else {
                //boss attacking
                playerHitPoints -= Math.max(1, bossStats.damage() - playerArmor);
            }
            return nextState(bossHitPoints, playerHitPoints, playerMana, activeEffects);
        }

        private @NonNull GameState nextState(int bossHitPoints, int playerHitPoints, int playerMana, Map<Effect, Integer> activeEffects) {
            return new GameState(
                    new Input.Stats(bossHitPoints, bossStats.damage(), bossStats.armor()),
                    new Input.Stats(playerHitPoints, playerStats.damage(), playerStats.armor()),
                    playerMana,
                    activeEffects
            );
        }

        private @NonNull GameState playerWins(int playerHitPoints, int playerMana, Map<Effect, Integer> activeEffects) {
            return nextState(0, playerHitPoints, playerMana, activeEffects);
        }

        private @NonNull GameState playerLose() {
            return nextState(bossStats.hitPoints(), 0, playerMana, activeEffects);
        }
    }
}