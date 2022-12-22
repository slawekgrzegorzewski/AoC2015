package com.adventofcode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

public record Box(long l, long w, long h) {
    private final static Pattern PATTERN = Pattern.compile("([0-9]+)x([0-9]+)x([0-9]+)");

    public static Box parse(String value) {
        Matcher matcher = PATTERN.matcher(value);
        if (!matcher.find()) throw new RuntimeException();
        return new Box(
                Long.parseLong(matcher.group(1)),
                Long.parseLong(matcher.group(2)),
                Long.parseLong(matcher.group(3))
        );
    }

    public long getPaperArea() {
        long firstFaceArea = l * w;
        long secondFaceArea = w * h;
        long thirdFaceArea = l * h;
        return 2 * (firstFaceArea + secondFaceArea + thirdFaceArea)
                + LongStream.of(firstFaceArea, secondFaceArea, thirdFaceArea).min().orElseThrow();
    }

    public long getRibbonLength() {
        long firstFacePerimeter = 2 * (l + w);
        long secondFacePerimeter = 2 * (w + h);
        long thirdFacePerimeter = 2 * (l + h);
        return l * w * h
                + LongStream.of(firstFacePerimeter, secondFacePerimeter, thirdFacePerimeter).min().orElseThrow();
    }
}
