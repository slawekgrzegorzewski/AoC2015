package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class Day3 {
    private final List<Claim> claims;


    public Day3() throws IOException {
        this.claims = Input.day3();
    }

    long part1() {
        Set<Coordinate> overlapping = new HashSet<>();
        for (int i = 0; i < claims.size(); i++) {
            Claim claim1 = claims.get(i);
            for (int j = i + 1; j < claims.size(); j++) {
                Claim claim2 = claims.get(j);
                claim1.findIntersection(claim2)
                        .map(Intersection::area)
                        .ifPresent(overlapping::addAll);
            }
        }
        return overlapping.size();
    }

    long part2() {
        Set<Claim> a = new HashSet<>();
        ArrayList<Claim> claims = new ArrayList<>(this.claims);
        for (int i = 0; i < claims.size(); i++) {
            Claim claim1 = claims.get(i);
            for (int j = i + 1; j < claims.size(); j++) {
                Claim claim2 = claims.get(j);
                if (claim1.findIntersection(claim2).isPresent()) {
                    a.add(claim1);
                    a.add(claim2);
                }
            }
        }
        claims.removeAll(a);
        return claims.getFirst().id();
    }

    public record Coordinate(int x, int y) {
    }

    public record Claim(int id, Coordinate topLeftCorner, int width, int height) {
        private static final Pattern PATTERN = Pattern.compile("#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)");

        public Claim(String line) {
            var matcher = PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid line: " + line);
            }
            this(Integer.parseInt(matcher.group(1)),
                    new Coordinate(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3))),
                    Integer.parseInt(matcher.group(4)),
                    Integer.parseInt(matcher.group(5)));
        }

        public Coordinate bottomRightCorner() {
            return new Coordinate(topLeftCorner.x + width, topLeftCorner.y + height);
        }

        public Optional<Intersection> findIntersection(Claim other) {
            int fromX = Math.max(topLeftCorner().x, other.topLeftCorner().x);
            int toX = Math.min(bottomRightCorner().x, other.bottomRightCorner().x);
            int fromY = Math.max(topLeftCorner().y, other.topLeftCorner().y);
            int toY = Math.min(bottomRightCorner().y, other.bottomRightCorner().y);
            return toX > fromX && toY > fromY ? Optional.of(new Intersection(fromX, toX, fromY, toY)) : Optional.empty();
        }
    }

    public record Intersection(int fromX, int toX, int fromY, int toY) {
        public Set<Coordinate> area() {
            Set<Coordinate> area = new HashSet<>();
            for (int x = fromX; x < toX; x++) {
                for (int y = fromY; y < toY; y++) {
                    area.add(new Coordinate(x, y));
                }
            }
            return area;
        }
    }

}
