package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7 {

    private static final WorkerStats IDLE = new WorkerStats("", 0);

    String part1() throws IOException {
        List<String> stepsExecuted = new ArrayList<>();
        Map<String, List<String>> remainingSteps = Input.day7();
        while (!remainingSteps.isEmpty()) {
            getTaskForNWorkers(remainingSteps, 1)
                    .forEach(elementToExecute -> {
                        remainingSteps.remove(elementToExecute.getKey());
                        remainingSteps.values().forEach(list -> list.remove(elementToExecute.getKey()));
                        stepsExecuted.add(elementToExecute.getKey());
                    });
        }
        return String.join("", stepsExecuted);

    }

    long part2() throws IOException {
        Map<Integer, WorkerStats> workers = getWorkers(5);

        int elapsedTime = 0;
        int skipTime = 0;

        Map<String, List<String>> remainingSteps = Input.day7();
        while (!remainingSteps.isEmpty()) {
            for (Integer workerId : workers.keySet()) {
                WorkerStats workerStats = workers.get(workerId);
                if (workerStats == IDLE) {
                    continue;
                }
                int skipTimeFinal = skipTime;
                workers.computeIfPresent(workerId, (_, worker) -> progressTime(worker, skipTimeFinal, remainingSteps));
            }
            List<Map.Entry<Integer, WorkerStats>> idleWorkers = workers.entrySet().stream()
                    .filter(entry -> entry.getValue() == IDLE)
                    .collect(Collectors.toCollection(ArrayList::new));
            getTaskForNWorkers(remainingSteps, idleWorkers.size())
                    .forEach(elementToExecute -> {
                        Map.Entry<Integer, WorkerStats> worker = idleWorkers.removeFirst();
                        remainingSteps.remove(elementToExecute.getKey());
                        workers.put(worker.getKey(), new WorkerStats(elementToExecute.getKey(), 60 + (elementToExecute.getKey().charAt(0) - 'A' + 1)));
                    });
            elapsedTime += skipTime;
            skipTime = workers.values().stream().filter(Predicate.not(IDLE::equals)).mapToInt(WorkerStats::timeLeft).min().orElse(0);
        }
        return elapsedTime + workers.values().stream().filter(Predicate.not(IDLE::equals)).mapToInt(WorkerStats::timeLeft).max().orElse(0);
    }

    private static WorkerStats progressTime(WorkerStats worker, int skipTimeFinal, Map<String, List<String>> remainingSteps) {
        int timeLeft = worker.timeLeft() - skipTimeFinal;
        if (timeLeft == 0) {
            remainingSteps.values().forEach(list -> list.remove(worker.task()));
            return IDLE;
        }
        if (timeLeft < 0)
            throw new IllegalStateException("Worker time left is negative");
        return new WorkerStats(worker.task(), timeLeft);
    }

    private static Map<Integer, WorkerStats> getWorkers(int numberOfWorkers) {
        Map<Integer, WorkerStats> workers = new HashMap<>();
        for (int i = 0; i < numberOfWorkers; i++) {
            workers.put(i, IDLE);
        }
        return workers;
    }

    private record WorkerStats(String task, int timeLeft) {

    }

    private static Stream<Map.Entry<String, List<String>>> getTaskForNWorkers(Map<String, List<String>> remainingSteps, int workersCount) {
        return remainingSteps.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isEmpty())
                .sorted(Map.Entry.comparingByKey())
                .limit(workersCount);
    }
}
