package com.adventofcode.y2015;

import com.adventofcode.y2015.input.Input;

import java.io.IOException;
import java.util.*;

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
                        List<Integer> moleculeToProduceCopy = new ArrayList<>(input.moleculeToProduce());
                        moleculeToProduceCopy.set(i, replacement.getFirst());
                        if (replacement.size() > 1)
                            moleculeToProduceCopy.addAll(i + 1, replacement.subList(1, replacement.size()));
                        results.add(moleculeToProduceCopy);
                    }
                }
            }
        }
        return results.size();
    }

    long part2() {
        int eMolecule = input.basicMolecules().get("e");
        List<Integer> moleculeToProduce = new ArrayList<>(input.moleculeToProduce());
        List<ReplacementToMolecule> replacements = new ArrayList<>();
        input.replacements()
                .entrySet()
                .stream()
                .flatMap(entry -> entry.getValue().stream().map(v -> new ReplacementToMolecule(v, entry.getKey())))
                .forEach(replacements::add);
        replacements.sort(Comparator.comparingInt(replacement -> -replacement.replacement().size()));
        int steps = 0;
        while (!reducibleToE(moleculeToProduce, input.replacements().get(eMolecule))) {
            for (ReplacementToMolecule replacement : replacements) {
                if (replace(moleculeToProduce, replacement)) {
                    steps++;
                    break;
                }
            }
        }
        return steps + 1;
    }

    private boolean replace(List<Integer> moleculeToProduce, ReplacementToMolecule replacement) {
        OptionalInt index = findIndex(moleculeToProduce, replacement.replacement());
        if (index.isEmpty()) return false;
        replaceInPlace(moleculeToProduce, replacement, index.getAsInt());
        return true;
    }

    private static void replaceInPlace(List<Integer> moleculeToProduce, ReplacementToMolecule replacement, int index) {
        List<Integer> newMolecule = new ArrayList<>(moleculeToProduce.subList(0, index));
        newMolecule.add(replacement.molecule());
        newMolecule.addAll(moleculeToProduce.subList(index + replacement.replacement().size(), moleculeToProduce.size()));
        moleculeToProduce.clear();
        moleculeToProduce.addAll(newMolecule);
    }

    private OptionalInt findIndex(List<Integer> moleculeToProduce, List<Integer> replacement) {
        for (int i = 0; i < moleculeToProduce.size() - replacement.size(); i++) {
            if (moleculesEqual(moleculeToProduce.subList(i, i + replacement.size()), replacement)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    private boolean reducibleToE(List<Integer> moleculeToProduce, List<List<Integer>> eReplacements) {
        return eReplacements.stream().anyMatch(eReplacement -> moleculesEqual(moleculeToProduce, eReplacement));
    }

    private boolean moleculesEqual(List<Integer> molecule1, List<Integer> molecule2) {
        if (molecule1.size() != molecule2.size())
            return false;
        for (int i = 0; i < molecule1.size(); i++) {
            if (!Objects.equals(molecule1.get(i), molecule2.get(i)))
                return false;
        }
        return true;
    }

    private record ReplacementToMolecule(List<Integer> replacement, int molecule) {
    }
}