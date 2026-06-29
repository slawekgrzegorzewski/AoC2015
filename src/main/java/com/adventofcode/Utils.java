package com.adventofcode;

import com.adventofcode.y2015.input.Input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Utils {
    public static List<String> getInputFromFile(String resourceName) throws IOException {
        try (InputStreamReader in = new InputStreamReader(Objects.requireNonNull(Input.class.getResourceAsStream(resourceName))); BufferedReader reader = new BufferedReader(in)) {
            return reader.lines().collect(Collectors.toList());
        }
    }
}
