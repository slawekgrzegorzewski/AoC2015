package com.adventofcode;

import com.adventofcode.input.Input;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.ToLongFunction;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Day12 {

    private final String jsonDocument;

    public Day12() throws IOException {
        jsonDocument = Input.day12();
    }

    long part1() {
        Object o = new Gson().fromJson(jsonDocument, Object.class);
        if (o instanceof ArrayList)
            return calculateArrayList((ArrayList) o, false);
        else if (o instanceof LinkedTreeMap)
            return calculateLinkedTreeMap((LinkedTreeMap) o, false);
        throw new IllegalStateException("Unexpected type: " + o.getClass());
    }

    long part2() {
        Object o = new Gson().fromJson(jsonDocument, Object.class);
        if (o instanceof ArrayList)
            return calculateArrayList((ArrayList) o, true);
        else if (o instanceof LinkedTreeMap)
            return calculateLinkedTreeMap((LinkedTreeMap) o, true);
        throw new IllegalStateException("Unexpected type: " + o.getClass());
    }

    private long calculateArrayList(ArrayList arrayList, boolean ignoreReds) {
        return arrayList.stream()
                .mapToLong(mapObjectToLong(ignoreReds))
                .sum();
    }

    private long calculateLinkedTreeMap(LinkedTreeMap linkedTreeMap, boolean ignoreReds) {
        if (ignoreReds && objectContainsRedValue(linkedTreeMap))
            return 0L;
        return ((Set<Map.Entry>) linkedTreeMap.entrySet()).stream()
                .map(Map.Entry::getValue)
                .mapToLong(mapObjectToLong(ignoreReds))
                .sum();
    }

    private static boolean objectContainsRedValue(LinkedTreeMap linkedTreeMap) {
        return ((Collection<Object>) linkedTreeMap.values())
                .stream()
                .filter(String.class::isInstance)
                .anyMatch("red"::equals);
    }

    private @NotNull ToLongFunction<Object> mapObjectToLong(boolean ignoreReds) {
        return element -> switch (element) {
            case LinkedTreeMap linkedTreeMap -> calculateLinkedTreeMap(linkedTreeMap, ignoreReds);
            case ArrayList arrayList -> calculateArrayList(arrayList, ignoreReds);
            case Double d -> d.longValue();
            case String _ -> 0L;
            default -> throw new IllegalStateException("Unexpected value: " + element);
        };
    }
}