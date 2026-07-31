package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day25 {
    private final List<Day25.Point> points;


    public Day25() throws IOException {
        this.points = Input.day25();
    }

    long part1() {
        List<List<Point>> constellations = new ArrayList<>();
        ArrayList<Point> points = new ArrayList<>(this.points);
        while (!points.isEmpty()) {
            List<Point> constellation = new ArrayList<>();
            constellations.add(constellation);
            Point point = points.removeFirst();
            constellation.add(point);
            List<Point> pointsInConstellation;
            do {
                pointsInConstellation = points.stream()
                        .filter(p -> constellation.stream().anyMatch(c -> c.manhattanDistance(p) <= 3))
                        .toList();
                constellation.addAll(pointsInConstellation);
                points.removeAll(pointsInConstellation);
            } while (!pointsInConstellation.isEmpty());
        }
        return constellations.size();
    }

    long part2() {
        return 0L;
    }

    public record Point(int x, int y, int z, int t) {
        public static Point parse(String value) {
            String[] split = value.split(",");
            return new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
        }

        public int manhattanDistance(Point other) {
            return Math.abs(x - other.x) + Math.abs(y - other.y) + Math.abs(z - other.z) + Math.abs(t - other.t);
        }
    }
}
