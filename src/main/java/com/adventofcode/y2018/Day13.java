package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day13 {
    private final TrackAndCarts trackAndCarts;


    public Day13() throws IOException {
        this.trackAndCarts = Input.day13();
    }

    String part1() {
        return trackCollisions(true);
    }

    String part2() {
        return trackCollisions(false);
    }

    private @NonNull String trackCollisions(boolean trackPositionOfFirstCollision) {
        Map<Coordinate, Cart> cartsByPosition = trackAndCarts.carts()
                .stream()
                .collect(
                        Collectors.toMap(
                                Cart::position,
                                Function.identity(),
                                (_, _) -> {
                                    throw new IllegalStateException("Collision");
                                },
                                HashMap::new));
        while (cartsByPosition.size() > 1) {
            List<Cart> cartsInMoveOrder = new LinkedList<>(cartsByPosition.values());
            cartsInMoveOrder.sort(Comparator.comparing(Cart::position, Coordinate.byYThenX()));
            while (!cartsInMoveOrder.isEmpty()) {
                Cart cart = cartsInMoveOrder.removeFirst();
                cartsByPosition.remove(cart.position());
                Coordinate newCartPosition = cart.position().move(cart.direction());
                if (cartsByPosition.containsKey(newCartPosition)) {
                    if (trackPositionOfFirstCollision)
                        return newCartPosition.toString();
                    cartsByPosition.remove(newCartPosition);
                    cartsInMoveOrder.removeIf(c -> c.position().equals(newCartPosition));
                } else {
                    Character trackElement = trackAndCarts.track().get(newCartPosition);
                    cartsByPosition.put(
                            newCartPosition,
                            new Cart(
                                    cart.id(),
                                    newCartPosition,
                                    cart.direction().nextDirection(trackElement, cart.turns()),
                                    cart.turns() + (trackElement == '+' ? 1 : 0)));
                }
            }
        }
        return cartsByPosition.values().iterator().next().position().toString();
    }

    public record TrackAndCarts(Map<Coordinate, Character> track, List<Cart> carts) {
    }

    public record Cart(int id, Coordinate position, Direction direction, int turns) {
    }

    public record Coordinate(int x, int y) {
        public static Comparator<Coordinate> byYThenX() {
            return Comparator.comparingInt(Coordinate::y).thenComparingInt(Coordinate::x);
        }

        public Coordinate move(Direction direction) {
            return switch (direction) {
                case UP -> new Coordinate(x, y - 1);
                case DOWN -> new Coordinate(x, y + 1);
                case LEFT -> new Coordinate(x - 1, y);
                case RIGHT -> new Coordinate(x + 1, y);
            };
        }

        @Override
        public @NonNull String toString() {
            return x + "," + y;
        }
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT;

        public Direction nextDirection(char trackElement, int turnsCount) {
            final int turnOrder = (turnsCount + 1) % 3;
            return switch (this) {
                case UP -> switch (trackElement) {
                    case '/' -> Direction.RIGHT;
                    case '\\' -> Direction.LEFT;
                    case '|' -> this;
                    case '+' -> switch (turnOrder) {
                        case 0 -> Direction.LEFT;
                        case 1 -> this;
                        case 2 -> Direction.RIGHT;
                        default -> throw new IllegalStateException("Unexpected value: " + turnOrder);
                    };
                    default -> throw new IllegalStateException("Unexpected value: " + trackElement);
                };
                case DOWN -> switch (trackElement) {
                    case '/' -> Direction.LEFT;
                    case '\\' -> Direction.RIGHT;
                    case '|' -> this;
                    case '+' -> switch (turnOrder) {
                        case 0 -> Direction.RIGHT;
                        case 1 -> this;
                        case 2 -> Direction.LEFT;
                        default -> throw new IllegalStateException("Unexpected value: " + turnOrder);
                    };
                    default -> throw new IllegalStateException("Unexpected value: " + trackElement);
                };
                case LEFT -> switch (trackElement) {
                    case '/' -> Direction.DOWN;
                    case '\\' -> Direction.UP;
                    case '-' -> this;
                    case '+' -> switch (turnOrder) {
                        case 0 -> Direction.DOWN;
                        case 1 -> this;
                        case 2 -> Direction.UP;
                        default -> throw new IllegalStateException("Unexpected value: " + turnOrder);
                    };
                    default -> throw new IllegalStateException("Unexpected value: " + trackElement);
                };

                case RIGHT -> switch (trackElement) {
                    case '/' -> Direction.UP;
                    case '\\' -> Direction.DOWN;
                    case '-' -> this;
                    case '+' -> switch (turnOrder) {
                        case 0 -> Direction.UP;
                        case 1 -> this;
                        case 2 -> Direction.DOWN;
                        default -> throw new IllegalStateException("Unexpected value: " + turnOrder);
                    };
                    default -> throw new IllegalStateException("Unexpected value: " + trackElement);
                };

            };
        }
    }

}
