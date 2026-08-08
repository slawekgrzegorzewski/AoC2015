package com.adventofcode.utils;

import com.adventofcode.y2015.input.Input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
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

    public static class ByteArrayWithSize {
        byte[] array;
        int size;

        public ByteArrayWithSize(int initialCapacity) {
            this.array = new byte[initialCapacity];
            this.size = 0;
        }

        public void add(byte value) {
            if (size == array.length) {
                byte[] newArray = new byte[array.length * 2];
                System.arraycopy(array, 0, newArray, 0, array.length);
                array = newArray;
            }
            array[size++] = value;
        }

        public byte get(int index) {
            return array[index];
        }

        public int size() {
            return size;
        }
    }

    public static class LoopFinder<T, K> {
        private final Supplier<T> nextValueProvider;
        private final Function<T, K> keyExtractor;
        private final int expectedFullLoops;

        public LoopFinder(Supplier<T> nextValueProvider, Function<T, K> keyExtractor, int expectedFullLoops) {
            this.nextValueProvider = nextValueProvider;
            this.keyExtractor = keyExtractor;
            this.expectedFullLoops = expectedFullLoops;
        }

        public Optional<LoopInfo<K>> findLoop() {
            List<K> list = new ArrayList<>();
            Set<K> unique = new HashSet<>();
            int index = -1;
            int lastUniqueSize = 0;
            int stopGrowingIndex = -1;
            while (true) {
                index++;
                T value = nextValueProvider.get();
                K key = keyExtractor.apply(value);
                list.add(key);
                unique.add(key);
                if (lastUniqueSize == unique.size()) {
                    if (stopGrowingIndex == -1) {
                        stopGrowingIndex = index;
                    }
                    if (list.size() - stopGrowingIndex >= expectedFullLoops * (stopGrowingIndex - list.indexOf(list.get(stopGrowingIndex)))) {
                        int loopStart = list.indexOf(list.get(stopGrowingIndex));
                        int loopSize = stopGrowingIndex - loopStart;
                        if (verifyLoop(list, loopStart, loopSize)) {
                            return Optional.of(new LoopInfo<>(loopStart, loopSize, list));
                        } else {
                            return Optional.empty();
                        }
                    }
                } else {
                    stopGrowingIndex = -1;
                    lastUniqueSize = unique.size();
                }
            }
        }

        private boolean verifyLoop(List<K> list, int loopStart, int loopSize) {
            for (int i = loopStart; i < list.size(); i++) {
                if (!list.get(i).equals(list.get(loopStart + (i - loopStart) % loopSize))) {
                    return false;
                }
            }
            return true;
        }

        public record LoopInfo<K>(int loopStart, int loopSize, List<K> analyzedList) {
        }
    }

    public static final class MinHeap {
        private long[] data = new long[4096];
        private int size;

        public void push(long value) {
            if (size == data.length) data = Arrays.copyOf(data, size * 2);
            int i = size++;
            data[i] = value;
            while (i > 0) {
                int parent = (i - 1) / 2;
                if (data[parent] <= data[i]) break;
                swap(parent, i);
                i = parent;
            }
        }

        public long pop() {
            long min = data[0];
            data[0] = data[--size];
            int i = 0;
            while (true) {
                int left = (i * 2) + 1;
                int right = left + 1;
                int smallest = i;
                if (left < size && data[left] < data[smallest]) smallest = left;
                if (right < size && data[right] < data[smallest]) smallest = right;
                if (smallest == i) break;
                swap(smallest, i);
                i = smallest;
            }
            return min;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        private void swap(int a, int b) {
            long tmp = data[a];
            data[a] = data[b];
            data[b] = tmp;
        }
    }

    public static class Permutations {

        public static List<int[]> generate(int[] numbers) {
            List<int[]> result = new ArrayList<>();
            generate(numbers, 0, result);
            return result;
        }

        private static void generate(int[] numbers, int index, List<int[]> result) {
            if (index == numbers.length) {
                result.add(numbers.clone());
                return;
            }

            for (int i = index; i < numbers.length; i++) {
                swap(numbers, index, i);
                generate(numbers, index + 1, result);
                swap(numbers, index, i);
            }
        }

        public static void iterate(int[] numbers, int index, Consumer<int[]> consumer) {
            if (index == numbers.length) {
                consumer.accept(numbers);
                return;
            }

            for (int i = index; i < numbers.length; i++) {
                swap(numbers, index, i);
                iterate(numbers, index + 1, consumer);
                swap(numbers, index, i);
            }
        }

        private static void swap(int[] numbers, int i, int j) {
            int temp = numbers[i];
            numbers[i] = numbers[j];
            numbers[j] = temp;
        }
    }
}
