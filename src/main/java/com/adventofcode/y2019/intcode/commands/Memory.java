package com.adventofcode.y2019.intcode.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Memory<T> {

    //    private final List<T> memory;
    private final Map<Long, T> memory;
    private final T defaultValue;

    public Memory(T[] values, T defaultValue) {
        memory = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            memory.put((long) i, values[i]);
        }
        this.defaultValue = defaultValue;
    }

    public Memory(List<T> values, T defaultValue) {
        memory = new HashMap<>();
        for (int i = 0; i < values.size(); i++) {
            memory.put((long) i, values.get(i));
        }
        this.defaultValue = defaultValue;
    }

    public T get(long address) {
        return memory.getOrDefault(address, defaultValue);
    }

    public void set(long address, T value) {
        memory.put(address, value);
    }
}
