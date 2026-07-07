package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day20 {
    private static final long MAX_IP = 4294967295L;
    private final List<Range> blackList;

    public Day20() throws IOException {
        this.blackList = Input.day20();
    }

    long part1() {
        List<Range> uniqueRanges = getUniqueRanges(new ArrayList<>(blackList));
        uniqueRanges.sort(Comparator.comparingLong(Range::from));
        long firstPossibleMin = MAX_IP;
        for (Range uniqueRange : uniqueRanges) {
            if (firstPossibleMin == MAX_IP) firstPossibleMin = uniqueRange.to() + 1;
            else {
                if (firstPossibleMin < uniqueRange.from()) return firstPossibleMin;
                else firstPossibleMin = uniqueRange.to() + 1;
            }
        }
        throw new RuntimeException("No valid IP found");
    }

    long part2() {
        List<Range> uniqueRanges = getUniqueRanges(new ArrayList<>(blackList));
        uniqueRanges.sort(Comparator.comparingLong(Range::from));
        long allowedIPs = uniqueRanges.getFirst().from();
        for (int i = 1; i < uniqueRanges.size(); i++) {
            if (uniqueRanges.get(i).from() <= uniqueRanges.get(i - 1).to() - 1)
                continue;
            allowedIPs += uniqueRanges.get(i).from() - uniqueRanges.get(i - 1).to() - 1;
        }
        return allowedIPs + MAX_IP - uniqueRanges.getLast().to();
    }

    private @NonNull List<Range> getUniqueRanges(List<Range> ranges) {
        ranges.sort(Comparator.comparingLong(Range::from));
        List<Range> uniqueRanges = new ArrayList<>();
        while (!ranges.isEmpty()) {
            Range range = ranges.getFirst();
            List<Range> merged = new ArrayList<>();
            merged.add(range);
            for (int i = 1; i < ranges.size(); i++) {
                Range other = ranges.get(i);
                if (other.overlaps(range)) {
                    merged.add(other);
                    range = range.merge(other);
                }
            }
            uniqueRanges.add(range);
            ranges.removeAll(merged);
        }
        return uniqueRanges;
    }

    public record Range(long from, long to) {
        public static Range parse(String line) {
            String[] split = line.split("-");
            return new Range(Long.parseLong(split[0]), Long.parseLong(split[1]));
        }

        public boolean overlaps(Range other) {
            return this.from <= other.to && this.to >= other.from;
        }

        public Range merge(Range other) {
            return new Range(Math.min(this.from, other.from), Math.max(this.to, other.to));
        }
    }
}
