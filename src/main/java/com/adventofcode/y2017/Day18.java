package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Day18 {
    private final List<String> program;


    public Day18() throws IOException {
        this.program = Input.day18();
    }

    long part1() {
        Map<String, Long> registers = new HashMap<>();
        int pointer = 0;
        long sound = 0;
        while (pointer < program.size() && pointer >= 0) {
            String instruction = program.get(pointer);
            if (instruction.startsWith("set ")) {
                String[] args = instruction.replace("set ", "").split(" ");
                registers.compute(args[0], (_, _) -> getValue(registers, args[1]));
            } else if (instruction.startsWith("mul ")) {
                String[] args = instruction.replace("mul ", "").split(" ");
                registers.compute(args[0], (_, v) -> (v == null ? 0 : v) * getValue(registers, args[1]));
            } else if (instruction.startsWith("add ")) {
                String[] args = instruction.replace("add ", "").split(" ");
                registers.compute(args[0], (_, v) -> (v == null ? 0 : v) + getValue(registers, args[1]));
            } else if (instruction.startsWith("mod ")) {
                String[] args = instruction.replace("mod ", "").split(" ");
                registers.compute(args[0], (_, v) -> (v == null ? 0 : v) % getValue(registers, args[1]));
            } else if (instruction.startsWith("snd ")) {
                String[] args = instruction.replace("snd ", "").split(" ");
                sound = getValue(registers, args[0]);
            } else if (instruction.startsWith("rcv ")) {
                String[] args = instruction.replace("rcv ", "").split(" ");
                if (getValue(registers, args[0]) > 0) {
                    return sound;
                }
            } else if (instruction.startsWith("jgz ")) {
                String[] args = instruction.replace("jgz ", "").split(" ");
                if (getValue(registers, args[0]) > 0)
                    pointer += (int) (getValue(registers, args[1]) - 1);
            } else throw new RuntimeException();
            pointer++;
        }
        throw new RuntimeException();
    }

    private static long getValue(Map<String, Long> registers, String argument) {
        return (argument.charAt(0) >= 'a' && argument.charAt(0) <= 'z')
                ? registers.computeIfAbsent(argument, _ -> 0L)
                : Integer.parseInt(argument);
    }

    long part2() throws InterruptedException {
        BlockingQueue<Long> queue1 = new LinkedBlockingQueue<>();
        BlockingQueue<Long> queue2 = new LinkedBlockingQueue<>();

        A a = new A(0, queue1, queue2);
        A b = new A(1, queue2, queue1);
        Thread program1 = new Thread(a);
        Thread program2 = new Thread(b);
        program1.start();
        program2.start();
        program1.join(100000);
        program2.join(100000);
        return b.getValue();
    }

    public class A implements Runnable {
        final int programId;
        final BlockingQueue<Long> input;
        final BlockingQueue<Long> output;
        volatile int value;

        public A(int programId, BlockingQueue<Long> input, BlockingQueue<Long> output) {
            this.programId = programId;
            this.input = input;
            this.output = output;
        }


        @Override
        public void run() {
            value = execute(programId, program, input, output, null, null, new HashMap<>());
        }

        public int getValue() {
            return value;
        }
    }

    public static int execute(long programId, List<String> program, BlockingQueue<Long> input, BlockingQueue<Long> output, Consumer<DebugInfo> debugBeforeCommand, Consumer<DebugInfo> debugAfterCommand, Map<String, Long> registers) {
        try {
            int send = 0;
            registers.put("p", programId);
            int pointer = 0;
            while (pointer < program.size() && pointer >= 0) {
                DebugInfo beforDebugInfo = new DebugInfo(program, pointer, registers);
                Optional.ofNullable(debugBeforeCommand).ifPresent(c -> c.accept(beforDebugInfo));
                String instruction = program.get(pointer);
                if (instruction.startsWith("set ")) {
                    String[] args = instruction.replace("set ", "").split(" ");
                    registers.compute(args[0], (_, _) -> getValue(registers, args[1]));
                } else if (instruction.startsWith("mul ")) {
                    String[] args = instruction.replace("mul ", "").split(" ");
                    registers.compute(args[0], (_, v) -> (v == null ? 0 : v) * getValue(registers, args[1]));
                } else if (instruction.startsWith("add ")) {
                    String[] args = instruction.replace("add ", "").split(" ");
                    registers.compute(args[0], (_, v) -> (v == null ? 0 : v) + getValue(registers, args[1]));
                }  else if (instruction.startsWith("sub ")) {
                    String[] args = instruction.replace("sub ", "").split(" ");
                    registers.compute(args[0], (_, v) -> (v == null ? 0 : v) - getValue(registers, args[1]));
                } else if (instruction.startsWith("mod ")) {
                    String[] args = instruction.replace("mod ", "").split(" ");
                    registers.compute(args[0], (_, v) -> (v == null ? 0 : v) % getValue(registers, args[1]));
                } else if (instruction.startsWith("snd ")) {
                    String[] args = instruction.replace("snd ", "").split(" ");
                    output.put(getValue(registers, args[0]));
                    send++;
                } else if (instruction.startsWith("rcv ")) {
                    String[] args = instruction.replace("rcv ", "").split(" ");
                    Long value = input.poll(10, TimeUnit.MILLISECONDS);
                    if (value == null) return send;
                    registers.put(args[0], value);
                } else if (instruction.startsWith("jgz ")) {
                    String[] args = instruction.replace("jgz ", "").split(" ");
                    if (getValue(registers, args[0]) > 0)
                        pointer += (int) (getValue(registers, args[1]) - 1);
                }  else if (instruction.startsWith("jnz ")) {
                    String[] args = instruction.replace("jnz ", "").split(" ");
                    if (getValue(registers, args[0]) != 0)
                        pointer += (int) (getValue(registers, args[1]) - 1);
                } else throw new RuntimeException(instruction);
                pointer++;
                DebugInfo afterDebugInfo = new DebugInfo(program, pointer, registers);
                Optional.ofNullable(debugAfterCommand).ifPresent(c -> c.accept(afterDebugInfo));
            }
            return send;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public record DebugInfo(List<String> program, int currentPointer, Map<String, Long> registers) {
    }
}
