package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class Day3 {
    private final List<List<Integer>> triangles;

    public Day3() throws IOException {
        this.triangles = Input.day3();
    }

    long part1() {
        return triangles.stream().map(Triangle::new).filter(Triangle::isValid).count();
    }

    long part2() {
        long validTriangles = 0;
        for (int i = 0; i < triangles.size(); i += 3) {
            validTriangles += Stream.of(
                            new Triangle(triangles.get(i).get(0), triangles.get(i + 1).get(0), triangles.get(i + 2).get(0)),
                            new Triangle(triangles.get(i).get(1), triangles.get(i + 1).get(1), triangles.get(i + 2).get(1)),
                            new Triangle(triangles.get(i).get(2), triangles.get(i + 1).get(2), triangles.get(i + 2).get(2))
                    )
                    .filter(Triangle::isValid)
                    .count();
        }
        return validTriangles;
    }

    public record Triangle(int a, int b, int c) {
        public Triangle(List<Integer> sides) {
            if (sides.size() != 3) throw new IllegalArgumentException("Invalid triangle");
            this(sides.getFirst(), sides.get(1), sides.getLast());
        }

        public boolean isValid() {
            return a + b > c && a + c > b && b + c > a;
        }
    }
}
