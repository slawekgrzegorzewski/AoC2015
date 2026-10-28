package com.adventofcode.y2019;

import com.adventofcode.y2019.input.Input;
import com.adventofcode.y2019.intcode.IntcodeComputer;
import com.adventofcode.y2019.intcode.commands.Memory;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.stream.Stream;

import static com.adventofcode.y2019.Day15.CellType.WALL;
import static java.util.function.Predicate.not;

public class Day15 {
    private final List<Long> program;
    private final Map<Coordinate, Integer> distances;

    public Day15() throws IOException {
        this.program = Input.day15();
        this.distances = findDistances();
    }

    long part1() {
        return distances.get(new Coordinate(0, 0));
    }

    long part2() {
        return distances
                .values()
                .stream()
                .max(Integer::compare)
                .orElseThrow();
    }

    private @NonNull Map<Coordinate, Integer> findDistances() {
        Explorer explorer = new Explorer();
        executeProgram(new Memory<>(program, 0L), explorer::nextMove, explorer::currentCellType);
        Coordinate oxygenCell = explorer.map.entrySet().stream().filter(entry -> entry.getValue().type == CellType.OXYGEN_SYSTEM).findFirst().orElseThrow().getKey();
        return shortestDistances(explorer.map, oxygenCell);
    }

    public static class Explorer {
        private final Map<Coordinate, Cell> map = new HashMap<>();
        private Coordinate current = new Coordinate(0, 0);

        public Explorer() {
            Cell startCell = new Cell();
            startCell.type = CellType.EMPTY;
            startCell.toDiscover = new ArrayList<>(List.of(current.north(), current.south(), current.east(), current.west()));
            map.put(current, startCell);
        }

        public long nextMove() {
            Cell cell = map.get(current);
            cell.toDiscover.removeIf(map::containsKey);
            Coordinate nextMove = cell.toDiscover.isEmpty() ? null : cell.toDiscover.removeFirst();
            if (nextMove == null) {
                if (cell.arrivedFrom != null) {
                    Direction oppositeDirection = cell.arrivedFrom.opposite();
                    current = current.move(oppositeDirection);
                    return oppositeDirection.code();
                } else {
                    return 0;
                }
            } else {
                Direction direction = current.direction(nextMove);
                Cell nextCell = new Cell();
                nextCell.arrivedFrom = direction;
                map.put(nextMove, nextCell);
                current = nextMove;
                return direction.code();
            }
        }

        public void currentCellType(long output) {
            Cell cell = map.get(current);
            cell.type = switch ((int) output) {
                case 0 -> WALL;
                case 1 -> CellType.EMPTY;
                case 2 -> CellType.OXYGEN_SYSTEM;
                default -> throw new IllegalStateException("Unexpected value: " + output);
            };
            switch (cell.type) {
                case WALL:
                    cell.toDiscover = List.of();
                    current = current.move(cell.arrivedFrom.opposite());
                    break;
                case EMPTY:
                case OXYGEN_SYSTEM:
                    cell.toDiscover =
                            new ArrayList<>(
                                    Stream.of(current.north(), current.south(), current.east(), current.west())
                                            .filter(not(map::containsKey))
                                            .toList());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + output);
            }
        }
    }

    public record Coordinate(int x, int y) {
        public Coordinate north() {
            return new Coordinate(x, y + 1);
        }

        public Coordinate south() {
            return new Coordinate(x, y - 1);
        }

        public Coordinate east() {
            return new Coordinate(x + 1, y);
        }

        public Coordinate west() {
            return new Coordinate(x - 1, y);
        }

        private Coordinate move(Direction direction) {
            return switch (direction) {
                case EAST -> east();
                case WEST -> west();
                case NORTH -> north();
                case SOUTH -> south();
            };
        }

        public Direction direction(Coordinate nextMove) {
            return switch (nextMove) {
                case Coordinate c when c.equals(north()) -> Direction.NORTH;
                case Coordinate c when c.equals(south()) -> Direction.SOUTH;
                case Coordinate c when c.equals(east()) -> Direction.EAST;
                case Coordinate c when c.equals(west()) -> Direction.WEST;
                default -> throw new IllegalStateException("Unexpected value: " + nextMove);
            };
        }
    }

    public enum CellType {
        WALL, EMPTY, OXYGEN_SYSTEM
    }

    public enum Direction {
        EAST(4), WEST(3), NORTH(1), SOUTH(2);
        private final int code;

        Direction(int code) {
            this.code = code;
        }

        public Direction opposite() {
            return switch (this) {
                case EAST -> WEST;
                case WEST -> EAST;
                case NORTH -> SOUTH;
                case SOUTH -> NORTH;
            };
        }

        public int code() {
            return code;
        }
    }

    public static class Cell {
        public Direction arrivedFrom;
        public CellType type;
        public List<Coordinate> toDiscover = new ArrayList<>();
    }

    private void executeProgram(Memory<Long> memory, LongSupplier input, LongConsumer output) {
        try {
            new IntcodeComputer(false).execute(memory, input, output);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<Coordinate, Integer> shortestDistances(Map<Coordinate, Cell> map, Coordinate start) {
        Map<Coordinate, Integer> distances = new HashMap<>();
        Deque<Coordinate> queue = new ArrayDeque<>();
        distances.put(start, 0);
        queue.addLast(start);

        while (!queue.isEmpty()) {
            Coordinate current = queue.removeFirst();
            Cell cell = map.get(current);
            if (cell == null || cell.type == null || cell.type == WALL) {
                continue;
            }

            int distance = distances.get(current) + 1;
            for (Direction direction : Direction.values()) {
                Coordinate neighbor = current.move(direction);
                if (distances.containsKey(neighbor)) {
                    continue;
                }
                Cell neighborCell = map.get(neighbor);
                if (neighborCell == null || neighborCell.type == null || neighborCell.type == WALL) {
                    continue;
                }
                distances.put(neighbor, distance);
                queue.addLast(neighbor);
            }
        }
        return distances;
    }
}
