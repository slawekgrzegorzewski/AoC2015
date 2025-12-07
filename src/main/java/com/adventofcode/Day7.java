package com.adventofcode;

import com.adventofcode.input.Input;
import com.google.common.base.Splitter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Day7 {

    public static final int SHORT_MASK = 0b00000000000000001111111111111111;

    private final List<String> input;

    public Day7() throws IOException {
        input = Input.day7();
    }

    long part1() {
        Map<String, Wire> wires = createCircuit();
        return wires.get("a").getValue().orElseThrow();
    }

    long part2() {
        Map<String, Wire> wires = createCircuit();
        int i = wires.get("a").getValue().orElseThrow();
        wires = createCircuit();
        wires.get("b").overrideValueSource(new ConstantValueSource(i));
        return wires.get("a").getValue().orElseThrow();
    }

    private @NotNull Map<String, Wire> createCircuit() {
        Map<String, Wire> wires = new HashMap<>();
        input.stream()
                .map(Splitter.on(" -> ").trimResults()::splitToList)
                .forEach(parts -> wires.computeIfAbsent(parts.getLast(), _ -> new Wire())
                        .setValueSource(
                                switch (parts.getFirst()) {
                                    case String s when s.contains("AND") ->
                                            createTwoInputsGate(wires, "AND", parts.getFirst(), ANDGate::new);
                                    case String s when s.contains("OR") ->
                                            createTwoInputsGate(wires, "OR", parts.getFirst(), ORGate::new);
                                    case String s when s.contains("LSHIFT") ->
                                            createTwoInputsGate(wires, "LSHIFT", parts.getFirst(), LShiftGate::new);
                                    case String s when s.contains("RSHIFT") ->
                                            createTwoInputsGate(wires, "RSHIFT", parts.getFirst(), RShiftGate::new);
                                    case String s when s.contains("NOT") ->
                                            createSingleInputGate(wires, "NOT", parts.getFirst(), NOTGate::new);
                                    default ->
                                            createConstant(parts.getFirst(), wires);
                                }));
        return wires;
    }

    private ValueSource createTwoInputsGate(Map<String, Wire> wires,
                                            String operator,
                                            String inputExpression,
                                            BiFunction<ValueSource, ValueSource, ValueSource> gateFactory) {
        List<String> inputParts = Splitter.on(operator)
                .trimResults()
                .splitToList(inputExpression);
        return gateFactory.apply(
                createValueSource(wires, inputParts.getFirst()),
                createValueSource(wires, inputParts.getLast()));
    }


    private ValueSource createSingleInputGate(Map<String, Wire> wires,
                                              String operator,
                                              String inputExpression,
                                              Function<ValueSource, ValueSource> gateFactory) {
        List<String> inputParts = Splitter.on(operator)
                .trimResults()
                .splitToList(inputExpression);
        return gateFactory.apply(createValueSource(wires, inputParts.getLast()));
    }

    private ValueSource createConstant(String leftHandSideExpression, Map<String, Wire> wires) {
        return createValueSource(wires, leftHandSideExpression);
    }

    private ValueSource createValueSource(Map<String, Wire> wires, String expression) {
        if (expression.matches("[0-9]*")) {
            return new ConstantValueSource(Integer.parseInt(expression));
        }
        return wires.computeIfAbsent(expression, _ -> new Wire());
    }

    public interface ValueSource {
        OptionalInt getValue();
    }

    public record ConstantValueSource(int value) implements ValueSource {
        @Override
        public OptionalInt getValue() {
            return OptionalInt.of(value & SHORT_MASK);
        }
    }

    public static class Wire implements ValueSource {
        private ValueSource valueSource;
        private Integer value = null;

        public void setValueSource(ValueSource valueSource) {
            if (this.valueSource != null)
                throw new IllegalStateException("Value source already set");
            this.valueSource = valueSource;
        }

        public void overrideValueSource(ValueSource valueSource) {
            this.valueSource = valueSource;
        }

        public OptionalInt getValue() {
            if (value != null) return OptionalInt.of(value);
            if (valueSource == null) return OptionalInt.empty();
            OptionalInt calculatedValue = valueSource.getValue();
            if (calculatedValue.isEmpty()) return calculatedValue;
            value = calculatedValue.getAsInt() & SHORT_MASK;
            return OptionalInt.of(value);
        }
    }

    public static abstract class SingleInputGate implements ValueSource {
        private final ValueSource input;
        Integer value = null;

        public SingleInputGate(ValueSource input) {
            this.input = input;
        }

        public OptionalInt getValue() {
            if (value != null) return OptionalInt.of(value);
            OptionalInt inputValue = input.getValue();
            if (inputValue.isEmpty()) return inputValue;
            value = evaluate(inputValue.getAsInt()) & SHORT_MASK;
            return OptionalInt.of(value);
        }

        protected abstract int evaluate(int value);

    }

    public static abstract class TwoInputsGate implements ValueSource {
        private final ValueSource input1;
        private final ValueSource input2;
        private Integer value = null;

        public TwoInputsGate(ValueSource input1, ValueSource input2) {
            this.input1 = input1;
            this.input2 = input2;
        }

        public OptionalInt getValue() {
            if (value != null) return OptionalInt.of(value);
            OptionalInt input1Value = input1.getValue();
            OptionalInt input2Value = input2.getValue();
            if (input1Value.isEmpty() || input2Value.isEmpty()) return OptionalInt.empty();
            value = evaluate(input1Value.getAsInt(), input2Value.getAsInt()) & SHORT_MASK;
            return OptionalInt.of(value);
        }

        protected abstract int evaluate(int value1, int value2);
    }

    public static class ANDGate extends TwoInputsGate {
        public ANDGate(ValueSource input1, ValueSource input2) {
            super(input1, input2);
        }

        @Override
        protected int evaluate(int value1, int value2) {
            return value1 & value2 & SHORT_MASK;
        }
    }

    public static class ORGate extends TwoInputsGate {
        public ORGate(ValueSource input1, ValueSource input2) {
            super(input1, input2);
        }

        @Override
        protected int evaluate(int value1, int value2) {
            return (value1 | value2) & SHORT_MASK;
        }
    }

    public static class LShiftGate extends TwoInputsGate {
        public LShiftGate(ValueSource input1, ValueSource input2) {
            super(input1, input2);
        }

        @Override
        protected int evaluate(int value1, int value2) {
            return (value1 << value2) & SHORT_MASK;
        }
    }

    public static class RShiftGate extends TwoInputsGate {

        public RShiftGate(ValueSource input1, ValueSource input2) {
            super(input1, input2);
        }

        @Override
        protected int evaluate(int value1, int value2) {
            return (value1 >> value2) & SHORT_MASK;
        }
    }

    public static class NOTGate extends SingleInputGate {

        public NOTGate(ValueSource input1) {
            super(input1);
        }

        @Override
        protected int evaluate(int value1) {
            return (~value1) & SHORT_MASK;
        }
    }

}


