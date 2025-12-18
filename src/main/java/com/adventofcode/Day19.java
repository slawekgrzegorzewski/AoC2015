package com.adventofcode;

import com.adventofcode.input.Input;

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
        Set<String> results = new HashSet<>();
//        String molecule = "e";
//        for (Input.Replacement replacement : input.replacements()) {
//            for (int i = 0; i < molecule.length(); i++) {
//                if (molecule.startsWith(replacement.from(), i)) {
//                    results.add(applyReplacementAtIndex(molecule, replacement, i));
//                }
//            }
//        }
        return results.size();
    }
}