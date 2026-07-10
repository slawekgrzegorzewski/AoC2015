package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day20 {
    private static final MathContext mc = new MathContext(4, RoundingMode.HALF_UP);
    private final List<Day20.Particle> particles;

    public Day20() throws IOException {
        this.particles = Input.day20();
    }

    long part1() {
        Comparator<ParticlePositionEquation> comparator = Comparator.<ParticlePositionEquation, BigDecimal>comparing(pe -> pe.equation.a)
                .thenComparing(pe -> pe.equation.b)
                .thenComparing(pe -> pe.equation.c);
        ParticlePositionEquation minParticleEquation = particles.stream()
                .map(particle -> {
                    ParticleCoordinatesEquations particleCoordinatesEquations = mapToPositionEquations(particle);
                    return new ParticlePositionEquation(
                            particle,
                            particleCoordinatesEquations.equations().getFirst().negateIfANegative()
                                    .add(particleCoordinatesEquations.equations().get(1).negateIfANegative())
                                    .add(particleCoordinatesEquations.equations().getLast().negateIfANegative()));
                })
                .min(comparator)
                .orElseThrow();
        System.out.println(minParticleEquation);
        return particles.indexOf(minParticleEquation.particle);
    }

    long part2() {
        List<ParticleCoordinatesEquations> particleEquations = particles.stream()
                .map(Day20::mapToPositionEquations)
                .toList();
        Map<Integer, Map<Triple, List<Particle>>> intersections = new HashMap<>();
        for (int i = 0; i < particleEquations.size(); i++) {
            ParticleCoordinatesEquations firstParticleEquations = particleEquations.get(i);
            for (int j = i + 1; j < particleEquations.size(); j++) {
                ParticleCoordinatesEquations secondParticleEquations = particleEquations.get(j);
                List<Integer> intersections1 = findIntersections(firstParticleEquations.equations(), secondParticleEquations.equations());
                for (int intersection : intersections1) {
                    Triple triple1 = calculate(intersection, firstParticleEquations);
                    Triple triple2 = calculate(intersection, secondParticleEquations);
                    if (triple1.equals(triple2)) {
                        List<Particle> equations = intersections
                                .computeIfAbsent(intersection, _ -> new HashMap<>())
                                .computeIfAbsent(triple1, _ -> new ArrayList<>());
                        equations.add(firstParticleEquations.particle);
                        equations.add(secondParticleEquations.particle);
                    } else {
                        throw new IllegalStateException("Particles intersect at the same time but have different positions");
                    }
                }
            }
        }
        List<Particle> allParticles = particleEquations.stream()
                .map(ParticleCoordinatesEquations::particle)
                .collect(Collectors.toCollection(ArrayList::new));
        intersections
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getValue().values())
                .forEach(particlesList -> {
                    particlesList
                            .forEach(particles1 -> particles1.removeIf(Predicate.not(allParticles::contains)));
                    particlesList.stream()
                            .filter(list -> list.size() > 1)
                            .forEach(list -> list.forEach(allParticles::remove));
                });
        return allParticles.size();
    }

    private Triple calculate(int intersection, ParticleCoordinatesEquations firstParticleEquations) {
        return new Triple(
                calculate(intersection, firstParticleEquations.equations().getFirst()),
                calculate(intersection, firstParticleEquations.equations().get(1)),
                calculate(intersection, firstParticleEquations.equations().getLast())
        );
    }

    private int calculate(int intersection, QuadraticEquation quadraticEquation) {
        return quadraticEquation.a.multiply(BigDecimal.valueOf((long) intersection * intersection))
                .add(quadraticEquation.b.multiply(BigDecimal.valueOf(intersection)))
                .add(quadraticEquation.c)
                .intValueExact();
    }

    private List<Integer> findIntersections(List<QuadraticEquation> firstPositionEquations, List<QuadraticEquation> sectionPositionEquations) {
        List<Integer> x = solve(firstPositionEquations.get(0).subtract(sectionPositionEquations.get(0)));
        List<Integer> y = solve(firstPositionEquations.get(1).subtract(sectionPositionEquations.get(1)));
        List<Integer> z = solve(firstPositionEquations.get(2).subtract(sectionPositionEquations.get(2)));
        List<List<Integer>> solutions = Stream.of(x, y, z)
                .filter(list -> !list.contains(-1))
                .toList();
        List<Integer> result = solutions.getFirst();
        for (int i = 1; i < solutions.size(); i++) {
            result.retainAll(solutions.get(i));
        }
        return result.stream().filter(i -> i >= 1).collect(Collectors.toCollection(ArrayList::new));
    }

    private List<Integer> solve(QuadraticEquation equation) {
        List<BigDecimal> solutions = new ArrayList<>();
        BigDecimal delta = equation.b.multiply(equation.b).subtract(BigDecimal.valueOf(4).multiply(equation.a).multiply(equation.c));
        BigDecimal aDoubled = BigDecimal.valueOf(2).multiply(equation.a);
        BigDecimal bNegated = equation.b.negate();
        if (equation.a.signum() == 0) {
            if (equation.b.signum() != 0)
                solutions.add(equation.c.negate().divide(equation.b, mc));
            else {
                if (equation.c.signum() == 0) {
                    ArrayList<Integer> result = new ArrayList<>();
                    result.add(-1);
                    return result;
                }
            }
        } else if (delta.signum() > 0) {
            BigDecimal deltaSqrt = delta.sqrt(mc);
            solutions.add(bNegated.subtract(deltaSqrt).divide(aDoubled, mc));
            solutions.add(bNegated.add(deltaSqrt).divide(aDoubled, mc));
        } else if (delta.signum() == 0) {
            solutions.add(bNegated.divide(aDoubled, mc));
        }
        return solutions.stream()
                .filter(bigDecimal -> bigDecimal.signum() != 0)
                .filter(s -> s.stripTrailingZeros().scale() <= 0)
                .map(BigDecimal::intValueExact)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public record Triple(long x, long y, long z) {
        public static Triple parse(String line) {
            String[] parts = line.substring(3, line.length() - 1).split(",");
            return new Triple(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])
            );
        }
    }

    public record Particle(Triple position, Triple velocity, Triple acceleration) {
        public static Particle parse(String line) {
            String[] parts = line.split(", ");
            return new Particle(
                    Triple.parse(parts[0]),
                    Triple.parse(parts[1]),
                    Triple.parse(parts[2]));
        }
    }

    public record QuadraticEquation(BigDecimal a, BigDecimal b, BigDecimal c) {
        public QuadraticEquation add(QuadraticEquation other) {
            return new QuadraticEquation(a.add(other.a), b.add(other.b), c.add(other.c));
        }

        public QuadraticEquation subtract(QuadraticEquation other) {
            return this.add(other.negate());
        }

        public QuadraticEquation negate() {
            return new QuadraticEquation(a.negate(), b.negate(), c.negate());
        }

        public QuadraticEquation negateIfANegative() {
            if (this.a.signum() >= 0) return this;
            return new QuadraticEquation(a.negate(), b.negate(), c.negate());
        }

        public String getString() {
            return String.format("%sx^2 + %sx + %s", a, b, c);
        }
    }

    public record ParticlePositionEquation(Particle particle, QuadraticEquation equation) {
    }

    public record ParticleCoordinatesEquations(Particle particle, List<QuadraticEquation> equations) {
    }

    private static ParticleCoordinatesEquations mapToPositionEquations(Particle particle) {
        BigDecimal halfOfAccelerationX = BigDecimal.valueOf(particle.acceleration.x)
                .setScale(mc.getPrecision(), mc.getRoundingMode())
                .divide(BigDecimal.valueOf(2), mc.getRoundingMode());
        BigDecimal halfOfAccelerationY = BigDecimal.valueOf(particle.acceleration.y)
                .setScale(mc.getPrecision(), mc.getRoundingMode())
                .divide(BigDecimal.valueOf(2), mc.getRoundingMode());
        BigDecimal halfOfAccelerationZ = BigDecimal.valueOf(particle.acceleration.z)
                .setScale(mc.getPrecision(), mc.getRoundingMode())
                .divide(BigDecimal.valueOf(2), mc.getRoundingMode());
        QuadraticEquation equation1 = new QuadraticEquation(
                halfOfAccelerationX,
                halfOfAccelerationX.add(BigDecimal.valueOf(particle.velocity.x)),
                BigDecimal.valueOf(particle.position.x)
        );
        QuadraticEquation equation2 = new QuadraticEquation(
                halfOfAccelerationY,
                halfOfAccelerationY.add(BigDecimal.valueOf(particle.velocity.y)),
                BigDecimal.valueOf(particle.position.y)
        );
        QuadraticEquation equation3 = new QuadraticEquation(
                halfOfAccelerationZ,
                halfOfAccelerationZ.add(BigDecimal.valueOf(particle.velocity.z)),
                BigDecimal.valueOf(particle.position.z)
        );
        return new ParticleCoordinatesEquations(particle, List.of(equation1, equation2, equation3));
    }
}
