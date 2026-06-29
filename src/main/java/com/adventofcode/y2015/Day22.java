package com.adventofcode.y2015;

import com.adventofcode.y2015.input.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;

public class Day22 {


    private static final Effect SHIELD_EFFECT = new Effect(6, 7, 0, 0);
    private static final Effect POISON_EFFECT = new Effect(6, 0, 3, 0);
    private static final Effect RECHARGE_EFFECT = new Effect(5, 0, 0, 101);
    private static final List<Spell> SPELLS = List.of(
            new Spell("Magic Missile", 53, 4, 0, null),
            new Spell("Drain", 73, 2, 2, null),
            new Spell("Shield", 113, 0, 0, SHIELD_EFFECT),
            new Spell("Poison", 173, 0, 0, POISON_EFFECT),
            new Spell("Recharge", 229, 0, 0, RECHARGE_EFFECT)
    );

    private final Input.Stats bossStats;

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
        final GameStateNode winNode = new GameStateNode(null, false, 0);
        final GameStateNode loseNode = new GameStateNode(null, false, 0);
        final GameStateNode initialNode = new GameStateNode(
                new GameState(
                        bossStats,
                        new Input.Stats(50, 0, 0),
                        500,
                        Map.of()),
                true,
                0);
        allNodes.add(winNode);
        allNodes.add(loseNode);
        allNodes.add(initialNode);
        Set<GameStateNode> nodesForNextRound = new HashSet<>();
        nodesForNextRound.add(initialNode);
        while (!nodesForNextRound.isEmpty()) {
            Set<GameStateNode> workingNodes = new HashSet<>(nodesForNextRound);
            nodesForNextRound.clear();
            workingNodes.forEach(node -> {
                if (node == winNode || node == loseNode) return;
                if (node.gameState.gameOver()) {
                    if (node.gameState.playerWon()) {
                        if (winNode.manaCost() == 0 || node.manaCost() < winNode.manaCost())
                            winNode.manaCost(node.manaCost());
                    }
                } else if (node.playerTurn) {
                    List<Spell> possibleSpells = node.gameState.possibleSpells();
                    if (!possibleSpells.isEmpty()) {
                        possibleSpells.forEach(spell -> {
                            int manaCost = spell.manaCost();
                            GameStateNode newNode = new GameStateNode(node.gameState.performTurn(spell, looseOneHPOnPlayerTurn), false, node.manaCost() + manaCost);
                            if (allNodes.contains(newNode)) {
                                if (newNode.manaCost() < node.manaCost()) {
                                    allNodes.remove(newNode);
                                    allNodes.add(newNode);
                                }
                                return;
                            }

                            allNodes.add(newNode);
                            nodesForNextRound.add(newNode);
                        });
                    }
                } else {
                    GameStateNode newNode = new GameStateNode(node.gameState.performTurn(null, looseOneHPOnPlayerTurn), true, node.manaCost());
                    if (allNodes.contains(newNode)) return;
                    allNodes.add(newNode);
                    nodesForNextRound.add(newNode);
                }
            });
        }
        return winNode.manaCost();
    }

    private static final class GameStateNode {
        private final GameState gameState;
        private final boolean playerTurn;
        private int manaCost;

        private GameStateNode(GameState gameState, boolean playerTurn, int manaCost) {
            this.gameState = gameState;
            this.playerTurn = playerTurn;
            this.manaCost = manaCost;
        }

        public int manaCost() {
            return manaCost;
        }

        public void manaCost(int manaCost) {
            this.manaCost = manaCost;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            GameStateNode that = (GameStateNode) o;
            return Objects.equals(gameState, that.gameState);
        }

        @Override
        public int hashCode() {
            return Objects.hash(gameState);
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

        public List<Spell> possibleSpells() {
            int availableMana = playerMana + activeEffects.keySet().stream().mapToInt(Effect::manaBonus).sum();
            return SPELLS.stream()
                    .filter(spell -> spell.effect() == null || activeEffects.getOrDefault(spell.effect(), 0) <= 1)
                    .filter(entry -> entry.manaCost() <= availableMana)
                    .toList();
        }

        private GameState performTurn(Spell spell, boolean loseOneHPOnPlayerTurn) {
            if (spell != null) {
                List<Spell> possibleSpells = possibleSpells();
                if (possibleSpells.isEmpty()) {
                    return playerLose();
                }
                if (!possibleSpells.contains(spell)) {
                    throw new IllegalStateException("Spell: " + spell.name() + " can not be cast");
                }
            }
            if (gameOver()) return this;

            int bossHitPoints = bossStats.hitPoints();
            int playerHitPoints = playerStats.hitPoints();
            int playerMana = playerMana();
            Map<Effect, Integer> activeEffects = new HashMap<>(this.activeEffects);
            int playerArmor = activeEffects.containsKey(SHIELD_EFFECT) ? SHIELD_EFFECT.armorBonus : 0;

            //hard mode
            if (loseOneHPOnPlayerTurn && spell != null) {
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

            if (spell != null) {
                //casting spell
                playerMana -= spell.manaCost();
                bossHitPoints -= spell.damage();
                playerHitPoints += spell.healing();
                if (spell.effect() != null) {
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