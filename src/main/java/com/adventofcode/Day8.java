package com.adventofcode;

import com.adventofcode.input.Input;
import com.google.common.base.CharMatcher;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class Day8 {

    private final RepresentationSizes representationSizes;

    public Day8() throws IOException {
        representationSizes = getRepresentationSizes(Input.day8());
    }

    long part1() {
        return representationSizes.file() - representationSizes.inMemory();
    }

    long part2() {
        return representationSizes.codeEncoded() - representationSizes.file();
    }

    private @NotNull RepresentationSizes getRepresentationSizes(List<String> fileLines) {
        return fileLines
                .stream()
                .map(line -> {
                    int fileSize = line.length();
                    int inCodeRepresentation = fileSize + 4;
                    int inMemorySize = fileSize - 2;
                    for (int i = 1; i < fileSize - 1; i++) {
                        if (line.charAt(i) == '\\' && line.charAt(i + 1) == '"') {
                            inCodeRepresentation += 2;
                            inMemorySize--;
                            i++;
                        } else if (line.charAt(i) == '\\' && line.charAt(i + 1) == '\\') {
                            inCodeRepresentation += 2;
                            inMemorySize--;
                            i++;
                        } else if (line.charAt(i) == '\\' && line.charAt(i + 1) == 'x' && hasTwoHexadecimals(line, i + 2)) {
                            inCodeRepresentation += 1;
                            inMemorySize -= 3;
                            i += 3;
                        }
                    }
                    return new RepresentationSizes(inCodeRepresentation, fileSize, inMemorySize);
                })
                .reduce(new RepresentationSizes(0, 0, 0), RepresentationSizes::add);
    }

    private boolean hasTwoHexadecimals(String line, int fromIndex) {
        if (line.length() < fromIndex + 2) return false;
        return CharMatcher.anyOf("0123456789abcdef")
                .matchesAllOf(line.substring(fromIndex, fromIndex + 2).toLowerCase());
    }

    private record RepresentationSizes(int codeEncoded, int file, int inMemory) {
        public RepresentationSizes add(RepresentationSizes other) {
            return new RepresentationSizes(codeEncoded + other.codeEncoded, file + other.file, inMemory + other.inMemory);
        }
    }
}