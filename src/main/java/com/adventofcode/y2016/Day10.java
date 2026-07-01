package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day10 {
    private final List<String> input;

    public Day10() throws IOException {
        this.input = Input.day10();
    }

    long part1() {
        Result result = execute(new ArrayList<>(input));
        return result.botsWithFullChipsHistory().entrySet().stream()
                .filter(entry -> entry.getValue().size() == 2)
                .filter(entry -> entry.getValue().contains(17))
                .filter(entry -> entry.getValue().contains(61))
                .map(Map.Entry::getKey)
                .findAny()
                .orElseThrow();
    }


    long part2() {
        Result result = execute(new ArrayList<>(input));
        return getOnlyChipValue(result.outputs().get(0))
                * getOnlyChipValue(result.outputs().get(1))
                * getOnlyChipValue(result.outputs().get(2));
    }

    private static long getOnlyChipValue(Set<Integer> integers) {
        return integers.stream().findAny().orElseThrow();
    }

    private Result execute(List<String> commands) {
        Pattern botToLowBotAndHighBotPattern = Pattern.compile("bot (\\d+) gives low to bot (\\d+) and high to bot (\\d+)");
        Pattern botToLowOutputAndHighBotPattern = Pattern.compile("bot (\\d+) gives low to output (\\d+) and high to bot (\\d+)");
        Pattern botToLowOutputAndHighOutputPattern = Pattern.compile("bot (\\d+) gives low to output (\\d+) and high to output (\\d+)");
        Pattern valueToBotPattern = Pattern.compile("value (\\d+) goes to bot (\\d+)");

        Map<Integer, Bot> bots = new HashMap<>();
        Map<Integer, List<Integer>> botsWithFullChipsHistory = new HashMap<>();
        Map<Integer, Set<Integer>> outputs = new HashMap<>();

        while (!commands.isEmpty()) {
            List<String> commandsProcessed = new ArrayList<>();
            for (String line : commands) {
                Matcher botToLowBotAndHighBotMatcher = botToLowBotAndHighBotPattern.matcher(line);
                Matcher botToLowOutputAndHighBotMatcher = botToLowOutputAndHighBotPattern.matcher(line);
                Matcher botToLowOutputAndHighOutputMatcher = botToLowOutputAndHighOutputPattern.matcher(line);
                Matcher valueToBotMatcher = valueToBotPattern.matcher(line);
                if (botToLowBotAndHighBotMatcher.find()) {
                    int botId = Integer.parseInt(botToLowBotAndHighBotMatcher.group(1));
                    int lowBotId = Integer.parseInt(botToLowBotAndHighBotMatcher.group(2));
                    int highBotId = Integer.parseInt(botToLowBotAndHighBotMatcher.group(3));
                    Bot bot = bots.computeIfAbsent(botId, id -> new Bot(id, new HashSet<>()));
                    if (bot.chips().size() == 2) {
                        Bot botLow = bots.computeIfAbsent(lowBotId, id -> new Bot(id, new HashSet<>()));
                        Bot botHigh = bots.computeIfAbsent(highBotId, id -> new Bot(id, new HashSet<>()));
                        botLow.add(bots.get(botId).low());
                        botHigh.add(bots.get(botId).high());
                        storeInHistoryIfComplete(botsWithFullChipsHistory, bot, botLow, botHigh);
                        bot.clear();
                        commandsProcessed.add(line);
                    }
                } else if (botToLowOutputAndHighBotMatcher.find()) {
                    int botId = Integer.parseInt(botToLowOutputAndHighBotMatcher.group(1));
                    int lowOutputId = Integer.parseInt(botToLowOutputAndHighBotMatcher.group(2));
                    int highBotId = Integer.parseInt(botToLowOutputAndHighBotMatcher.group(3));
                    Bot bot = bots.computeIfAbsent(botId, id -> new Bot(id, new HashSet<>()));
                    if (bot.chips().size() == 2) {
                        outputs.computeIfAbsent(lowOutputId, id -> new HashSet<>()).add(bots.get(botId).low());
                        Bot botHigh = bots.computeIfAbsent(highBotId, id -> new Bot(id, new HashSet<>()));
                        botHigh.add(bots.get(botId).high());
                        storeInHistoryIfComplete(botsWithFullChipsHistory, bot, botHigh);
                        bot.clear();
                        commandsProcessed.add(line);
                    }
                } else if (botToLowOutputAndHighOutputMatcher.find()) {
                    int botId = Integer.parseInt(botToLowOutputAndHighOutputMatcher.group(1));
                    int lowOutputId = Integer.parseInt(botToLowOutputAndHighOutputMatcher.group(2));
                    int highOutputId = Integer.parseInt(botToLowOutputAndHighOutputMatcher.group(3));
                    Bot bot = bots.computeIfAbsent(botId, id -> new Bot(id, new HashSet<>()));
                    if (bot.chips().size() == 2) {
                        storeInHistoryIfComplete(botsWithFullChipsHistory, bot);
                        outputs.computeIfAbsent(lowOutputId, id -> new HashSet<>()).add(bots.get(botId).low());
                        outputs.computeIfAbsent(highOutputId, id -> new HashSet<>()).add(bots.get(botId).high());
                        bot.clear();
                    }
                } else if (valueToBotMatcher.find()) {
                    int value = Integer.parseInt(valueToBotMatcher.group(1));
                    int botId = Integer.parseInt(valueToBotMatcher.group(2));
                    Bot bot = bots.computeIfAbsent(botId, id -> new Bot(id, new HashSet<>()));
                    bot.add(value);
                    storeInHistoryIfComplete(botsWithFullChipsHistory, bot);
                    commandsProcessed.add(line);
                }
            }
            if (commandsProcessed.isEmpty()) break;
            commands.removeAll(commandsProcessed);
            commandsProcessed.clear();
        }
        return new Result(bots, botsWithFullChipsHistory, outputs);
    }

    private static void storeInHistoryIfComplete(Map<Integer, List<Integer>> botsWithFullChipsHistory, Bot... bots) {
        for (Bot bot : bots) {
            if (bot.chips().size() == 2) {
                botsWithFullChipsHistory.put(bot.id(), List.copyOf(bot.chips()));
            }
        }
    }

    private record Result(Map<Integer, Bot> bots,
                          Map<Integer, List<Integer>> botsWithFullChipsHistory,
                          Map<Integer, Set<Integer>> outputs) {
    }

    private record Bot(int id, Set<Integer> chips) {

        void add(int chip) {
            chips.add(chip);
            if (chips.size() > 2) {
                throw new RuntimeException("Bot " + id + " has more than 2 chips");
            }
        }

        int low() {
            if (chips.size() > 2) {
                throw new RuntimeException("Bot " + id + " has more than 2 chips");
            }
            return chips.stream().min(Integer::compareTo).orElseThrow();
        }

        int high() {
            if (chips.size() > 2) {
                throw new RuntimeException("Bot " + id + " has more than 2 chips");
            }
            return chips.stream().max(Integer::compareTo).orElseThrow();
        }

        public void clear() {
            chips.clear();
        }

        public boolean testForPart1() {
            return chips.size() == 2 && low() == 17 && high() == 61;
        }
    }
}
