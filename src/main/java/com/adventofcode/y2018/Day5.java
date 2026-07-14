package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;

public class Day5 {

    public Day5() throws IOException {
    }

    long part1() throws IOException {
        return reactAndGetFinalLength(Input.day5(null));
    }

    long part2() throws IOException {
        int minLength = Integer.MAX_VALUE;
        for (char c : "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()) {
            Particle firstParticle = Input.day5(c);
            minLength = Math.min(reactAndGetFinalLength(firstParticle), minLength);
        }
        return minLength;
    }

    private static int reactAndGetFinalLength(Particle firstParticle) throws IOException {
        Particle currentParticle = firstParticle;
        while (currentParticle.next != null) {
            boolean opposite = Character.toLowerCase(currentParticle.particle) == Character.toLowerCase(currentParticle.next.particle)
                    && currentParticle.particle != currentParticle.next.particle;
            if (opposite) {
                if (currentParticle.previous == null) {
                    firstParticle = currentParticle.next.next;
                    firstParticle.previous = null;
                    currentParticle.next.next = null;
                    currentParticle.next.previous = null;
                    currentParticle.next = null;
                    currentParticle.previous = null;
                    currentParticle = firstParticle;
                } else {
                    Particle previous = currentParticle.previous;
                    Particle next = currentParticle.next.next;
                    previous.next = next;
                    if (next != null) next.previous = previous;
                    currentParticle.next.next = null;
                    currentParticle.next.previous = null;
                    currentParticle.next = null;
                    currentParticle.previous = null;
                    currentParticle = previous;
                }
            } else {
                currentParticle = currentParticle.next;
            }
        }
        return findLength(firstParticle);
    }

    private static int findLength(Particle particle) {
        int count = 0;
        while (particle != null) {
            count++;
            particle = particle.next;
        }
        return count;
    }

    public static class Particle {
        public char particle;
        public Particle previous;
        public Particle next;

        public Particle(char particle) {
            this.particle = particle;
        }
    }
}
