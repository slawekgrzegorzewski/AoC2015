package com.adventofcode;

import com.adventofcode.input.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day19 {

    private final Input.FusionPlantInput input;

    public Day19() throws IOException {
        input = Input.day19();
    }

    long part1() {
        Set<List<Integer>> results = new HashSet<>();
        for (Map.Entry<Integer, List<List<Integer>>> replacementOfSingleMolecule : input.replacements().entrySet()) {
            for (List<Integer> replacement : replacementOfSingleMolecule.getValue()) {
                for (int i = 0; i < input.moleculeToProduce().size(); i++) {
                    if (input.moleculeToProduce().get(i).equals(replacementOfSingleMolecule.getKey())) {
                        List<Integer> moleculeToProduceCopy = replaceInMolecule(i, replacement, input.moleculeToProduce());
                        results.add(moleculeToProduceCopy);
                    }
                }
            }
        }
        return results.size();
    }

    private @NonNull List<Integer> reduceMolecule(int atIndex, int elementsToRemove, Integer replacement, List<Integer> workingMolcecule) {
        List<Integer> newMolecule = new ArrayList<>(workingMolcecule.size() - elementsToRemove + 1);
        newMolecule.addAll(workingMolcecule.subList(0, atIndex));
        newMolecule.add(replacement);
        newMolecule.addAll(workingMolcecule.subList(atIndex + elementsToRemove, workingMolcecule.size()));
//        System.out.println("workingMolcecule = " + moleculeString(workingMolcecule));
//        System.out.println("replacement = " + moleculeString(List.of(replacement)));
//        System.out.println("atIndex = " + atIndex);
//        System.out.println("newMolecule = " + moleculeString(newMolecule));
        return newMolecule;
    }

    private @NonNull List<Integer> replaceInMolecule(int moleculeIndex, List<Integer> replacement, List<Integer> workingMolcecule) {
        List<Integer> moleculeToProduceCopy = new ArrayList<>(workingMolcecule);
        moleculeToProduceCopy.set(moleculeIndex, replacement.getFirst());
        if (replacement.size() > 1)
            moleculeToProduceCopy.addAll(moleculeIndex + 1, replacement.subList(1, replacement.size()));
        return moleculeToProduceCopy;
    }

    long part2() {
        Map<List<Integer>, Integer> moleculeAndWaysToReduceToIt = new HashMap<>();
        moleculeAndWaysToReduceToIt.put(input.moleculeToProduce(), 1);


        boolean hasSomethingToProcess = true;
        while (hasSomethingToProcess) {
            Map<List<Integer>, Integer> newMoleculeAndWaysToReduceToIt = new HashMap<>();
            hasSomethingToProcess = false;

            for (Map.Entry<List<Integer>, Integer> moleculeWays : moleculeAndWaysToReduceToIt.entrySet()) {
                if (moleculeWays.getKey().size() == 1 && moleculeWays.getKey().getFirst().equals(input.basicMolecules().get("e"))) {
                    newMoleculeAndWaysToReduceToIt.put(moleculeWays.getKey(), moleculeWays.getValue());
                    continue;
                }
                for (Map.Entry<Integer, List<List<Integer>>> fromMoleculeListOfReplacements : input.replacements().entrySet()) {
                    for (List<Integer> replacement : fromMoleculeListOfReplacements.getValue()) {
                        List<Integer> moleculesIndexes = findMoleculesIndexes(moleculeWays.getKey(), replacement);
//                        System.out.println("\treplacement: " + moleculeString(replacement));
                        if (!moleculesIndexes.isEmpty()) {
                            for (Integer foundAtIndex : moleculesIndexes) {
                                newMoleculeAndWaysToReduceToIt.merge(reduceMolecule(foundAtIndex, replacement.size(), fromMoleculeListOfReplacements.getKey(), moleculeWays.getKey()), moleculeWays.getValue(), Integer::sum);
                            }
                            hasSomethingToProcess = true;
//                            System.out.println("\t\tFound indexes: " + moleculesIndexes);
                        } else {
//                            System.out.println("\t\tNot found");
                        }
                    }
                }
            }
            moleculeAndWaysToReduceToIt = newMoleculeAndWaysToReduceToIt;
        }
        return moleculeAndWaysToReduceToIt.entrySet().stream().filter(e -> e.getValue() > 0).mapToLong(Map.Entry::getValue).sum();
    }

    private String moleculeString(List<Integer> workingMolecule) {
        return workingMolecule.stream().map(input.basicMoleculesIndexes()::get).collect(Collectors.joining());
    }

    private List<Integer> findMoleculesIndexes(List<Integer> workingMolecule, List<Integer> replacement) {
        List<Integer> indexes = new ArrayList<>();
        OptionalInt found = OptionalInt.of(0);
        while ((found = findMoleculeStartIndex(workingMolecule, replacement, found.getAsInt())).isPresent()) {
            indexes.add(found.getAsInt());
            found = OptionalInt.of(found.getAsInt() + 1);
        }
        return indexes;
    }

    private OptionalInt findMoleculeStartIndex(List<Integer> workingMolecule, List<Integer> replacement, int startingFromIndex) {
        for (int i = startingFromIndex; i < workingMolecule.size() - replacement.size(); i++) {
            if (workingMolecule.get(i).equals(replacement.getFirst())) {
                boolean contains = true;
                for (int j = 1; j < replacement.size(); j++) {
                    if (!workingMolecule.get(i + j).equals(replacement.get(j))) {
                        contains = false;
                        break;
                    }
                }
                if (contains) return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

//    private long a(List<Integer> workingMolecule) {
//        if (workingMolecule.size() > input.moleculeToProduce().size())
//            return 0;
//        if (moleculesAreTheSame(workingMolecule, input.moleculeToProduce())) {
//            return 1;
//        }
//        if (workingMolecule.size() == input.moleculeToProduce().size())
//            return 0;
//        long result = 0;
//        for (int i = 0; i < workingMolecule.size(); i++) {
//            if(!compareToIndex(input.moleculeToProduce(), workingMolecule, i - 1))break;
//            if (!input.replacements().containsKey(workingMolecule.get(i))) continue;
//            for (List<Integer> replacement : input.replacements().get(workingMolecule.get(i))) {
//                result += a(replaceInMolecule(i, replacement, workingMolecule));
//            }
//        }
//        return result;
//    }


    private boolean compareToIndex(List<Integer> firstMolecule, List<Integer> secondMolecule, int toIndex) {
        for (int i = 0; i <= toIndex; i++) {
            if (!firstMolecule.get(i).equals(secondMolecule.get(i))) return false;
        }
        return true;
    }

    private boolean moleculesAreTheSame(List<Integer> workingMolecule, List<Integer> moleculeToProduce) {
        if (workingMolecule.size() != input.moleculeToProduce().size()) {
            return false;
        }
        for (int i = 0; i < workingMolecule.size(); i++) {
            if (!workingMolecule.get(i).equals(moleculeToProduce.get(i))) return false;
        }
        return true;
    }
}