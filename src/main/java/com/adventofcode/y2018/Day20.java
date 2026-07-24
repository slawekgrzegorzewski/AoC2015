package com.adventofcode.y2018;

import com.adventofcode.y2018.input.Input;

import java.io.IOException;
import java.util.*;

public class Day20 {
    public static final Coordinate START_LOCATION = new Coordinate(0, 0);
    private final String regex;

    public Day20() throws IOException {
        this(Input.day20());
    }

    public Day20(String regex) {
        this.regex = regex;
    }

    long part1() {
        return discoverFacility()
                .roomsByCoordinate()
                .values()
                .stream()
                .mapToInt(r -> r.distance)
                .max()
                .orElseThrow();
    }

    long part2() {
        return discoverFacility()
                .roomsByCoordinate()
                .values()
                .stream()
                .mapToInt(r -> r.distance)
                .filter(i -> i >= 1000)
                .count();
    }

    private MapDiscovery discoverFacility() {
        MapDiscovery mapDiscovery = new MapDiscovery(new HashMap<>(), START_LOCATION);
        mapDiscovery.roomsByCoordinate().put(START_LOCATION, new Room(START_LOCATION, new HashSet<>()));
        followRegex(
                regex.replace("^", "").replace("$", ""),
                mapDiscovery,
                START_LOCATION);
        dijkstraOnRooms(mapDiscovery);
        return mapDiscovery;
    }

    private static Coordinate followRegex(String regex, MapDiscovery mapDiscovery, Coordinate startPoint) {
        List<String> branches = branches(regex);
        Coordinate currentPoint = null;
        for (String branch : branches) {
            currentPoint = startPoint;
            List<String> segments = segments(branch);
            for (String segment : segments) {
                if (segment.contains("|") || segment.contains("(")) {
                    currentPoint = followRegex(segment, mapDiscovery, currentPoint);
                } else {
                    currentPoint = follow(segment, mapDiscovery, currentPoint);
                }
            }
        }
        return currentPoint;
    }

    private static Coordinate follow(String path, MapDiscovery mapDiscovery, Coordinate startPoint) {
        char[] pathChars = path.toCharArray();
        Coordinate currentPoint = startPoint;
        Room currentRoom = mapDiscovery.roomsByCoordinate().get(currentPoint);
        for (char pathChar : pathChars) {
            currentPoint = currentPoint.move(pathChar);
            Room nextRoom = mapDiscovery.roomsByCoordinate().computeIfAbsent(
                    currentPoint,
                    k -> new Room(k, new HashSet<>()));
            currentRoom.nextRooms().add(nextRoom);
            nextRoom.nextRooms().add(currentRoom);
            currentRoom = nextRoom;
        }
        return currentPoint;
    }

    private static void dijkstraOnRooms(MapDiscovery mapDiscovery) {
        Set<Room> visited = new HashSet<>();
        List<Room> queue = new ArrayList<>();
        Room startRoom = mapDiscovery.roomsByCoordinate().get(START_LOCATION);
        startRoom.distance = 0;
        queue.add(startRoom);
        while (!queue.isEmpty()) {
            Room room = queue.removeFirst();
            visited.add(room);
            for (Room nextRoom : room.nextRooms()) {
                if (visited.contains(nextRoom)) continue;
                if (nextRoom.distance > room.distance + 1) {
                    nextRoom.distance = room.distance + 1;
                }
                queue.add(nextRoom);
            }
            queue.sort(Comparator.comparing(r -> r.distance));
        }
    }

    public record MapDiscovery(Map<Coordinate, Room> roomsByCoordinate, Coordinate startLocation) {
    }

    public static final class Room {
        private final Coordinate coordinate;
        private final Set<Room> nextRooms;
        private int distance = Integer.MAX_VALUE;

        public Room(Coordinate coordinate, Set<Room> nextRooms) {
            this.coordinate = coordinate;
            this.nextRooms = nextRooms;
        }

        public Coordinate coordinate() {
            return coordinate;
        }

        public Set<Room> nextRooms() {
            return nextRooms;
        }
    }

    public record Coordinate(int x, int y) {
        public Coordinate north() {
            return new Coordinate(x, y - 1);
        }

        public Coordinate south() {
            return new Coordinate(x, y + 1);
        }

        public Coordinate west() {
            return new Coordinate(x - 1, y);
        }

        public Coordinate east() {
            return new Coordinate(x + 1, y);
        }

        public Coordinate move(char direction) {
            return switch (direction) {
                case 'N' -> north();
                case 'S' -> south();
                case 'W' -> west();
                case 'E' -> east();
                default -> throw new RuntimeException();
            };
        }
    }

    private static List<String> branches(String regex) {
        char[] regexCharArray = regex.toCharArray();
        List<String> result = new ArrayList<>();
        int level = 0;
        StringBuilder currentElement = new StringBuilder();
        for (char c : regexCharArray) {
            if (c == '(') {
                level++;
            }
            if (c == ')') {
                level--;
            }
            if (c == '|' && level == 0) {
                result.add(currentElement.toString());
                currentElement = new StringBuilder();
            } else {
                currentElement.append(c);
            }
        }
        result.add(currentElement.toString());
        return result;
    }

    private static List<String> segments(String regex) {
        char[] regexCharArray = regex.toCharArray();
        List<String> result = new ArrayList<>();
        int level = 0;
        StringBuilder currentElement = new StringBuilder();
        for (char c : regexCharArray) {
            if (c == '(') {
                level++;
                if (level == 1) {
                    if (!currentElement.isEmpty()) {
                        result.add(currentElement.toString());
                        currentElement = new StringBuilder();
                    }
                    continue;
                }
            }
            if (c == ')') {
                level--;
                if (level == 0) {
                    result.add(currentElement.toString());
                    currentElement = new StringBuilder();
                    continue;
                }
            }
            currentElement.append(c);
        }
        if (!currentElement.isEmpty()) {
            result.add(currentElement.toString());
        }
        return result;
    }

    private static void print(MapDiscovery mapDiscovery, Set<Coordinate> startPoints) {
        Map<Coordinate, Room> coordinateRoomMap = mapDiscovery.roomsByCoordinate();
        IntSummaryStatistics xStats = coordinateRoomMap.keySet().stream().mapToInt(Coordinate::x).summaryStatistics();
        IntSummaryStatistics yStats = coordinateRoomMap.keySet().stream().mapToInt(Coordinate::y).summaryStatistics();
        char[][] map = new char[(yStats.getMax() - yStats.getMin() + 1) * 2 + 1][(xStats.getMax() - xStats.getMin() + 1) * 2 + 1];
        for (char[] chars : map) {
            Arrays.fill(chars, ' ');
        }
        for (int y = yStats.getMin(); y <= yStats.getMax(); y++) {
            for (int x = xStats.getMin(); x <= xStats.getMax(); x++) {
                Room r = coordinateRoomMap.get(new Coordinate(x, y));
                int roomX = 2 * (x - xStats.getMin()) + 1;
                int roomY = 2 * (y - yStats.getMin()) + 1;
                if (r != null) {
                    map[roomY][roomX] = x == 0 && y == 0 ? 'X' : ((startPoints.contains(r.coordinate()) ? 'S' : '.'));
                    map[roomY + 1][roomX + 1] = '#';
                    map[roomY + 1][roomX - 1] = '#';
                    map[roomY - 1][roomX + 1] = '#';
                    map[roomY - 1][roomX - 1] = '#';
                    map[roomY + 1][roomX] = '?';
                    map[roomY - 1][roomX] = '?';
                    map[roomY][roomX + 1] = '?';
                    map[roomY][roomX - 1] = '?';
                    for (Room nextRoom : r.nextRooms()) {
                        if (nextRoom.coordinate().equals(r.coordinate().north())) {
                            map[roomY - 1][roomX] = '-';
                        }
                        if (nextRoom.coordinate().equals(r.coordinate().south())) {
                            map[roomY + 1][roomX] = '-';
                        }
                        if (nextRoom.coordinate().equals(r.coordinate().west())) {
                            map[roomY][roomX - 1] = '|';
                        }
                        if (nextRoom.coordinate().equals(r.coordinate().east())) {
                            map[roomY][roomX + 1] = '|';
                        }
                    }
                }
            }
        }
        for (char[] chars : map) {
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '?') chars[i] = '#';
            }
        }
        for (char[] chars : map) {
            System.out.println(new String(chars));
        }
    }
}
