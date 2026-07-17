package com.adventofcode;

import com.adventofcode.y2015.input.Input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Utils {
    public static List<String> getInputFromFile(String resourceName) throws IOException {
        try (InputStreamReader in = new InputStreamReader(Objects.requireNonNull(Input.class.getResourceAsStream(resourceName))); BufferedReader reader = new BufferedReader(in)) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    public static class BooleanArrayCollector<A> implements Collector<A, boolean[], boolean[]> {

        private final Supplier<boolean[]> supplier;
        private final ToIntFunction<A> indexConverter;
        private final ToBooleanFunction<A> valueProvider;

        public static int convertToAnIndex(String part) {
            int index = 0;
            for (int i = 0; i < part.length(); i++) {
                index = index << 1 | (part.charAt(i) == '#' ? 1 : 0);
            }
            return index;
        }

        public BooleanArrayCollector(Supplier<boolean[]> supplier, ToIntFunction<A> indexConverter, ToBooleanFunction<A> valueProvider) {
            this.supplier = supplier;
            this.indexConverter = indexConverter;
            this.valueProvider = valueProvider;
        }

        @Override
        public Supplier<boolean[]> supplier() {
            return supplier;
        }

        @Override
        public BiConsumer<boolean[], A> accumulator() {
            return (array, parts) -> array[indexConverter.applyAsInt(parts)] = valueProvider.applyAsBool(parts);
        }

        @Override
        public BinaryOperator<boolean[]> combiner() {
            return (_, _) -> {
                throw new UnsupportedOperationException("Not supported yet.");
            };
        }

        @Override
        public Function<boolean[], boolean[]> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Set.of(Collector.Characteristics.IDENTITY_FINISH);
        }
    }

    public interface ToBooleanFunction<T> {
        boolean applyAsBool(T value);
    }
}
