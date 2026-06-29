package com.adventofcode.y2015;

import com.adventofcode.y2015.input.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.List;

public class Day23 {

    private final List<String> input;

    public Day23() throws IOException {
        input = Input.day23();
    }

    long part1() {
        return execute(new ProgramState(0, 0, 0)).b;
    }

    long part2() {
        return execute(new ProgramState(1, 0, 0)).b;
    }

    private @NonNull ProgramState execute(ProgramState state) {
        while (state.instructionPointer >= 0 && state.instructionPointer < input.size()) {
            Instruction instruction = Instruction.parse(input.get(state.instructionPointer));
            if (instruction == null) break;
            state = instruction.execute(state);
        }
        return state;
    }

    private record ProgramState(int a, int b, int instructionPointer) {
        public int getRegistryValue(String registryName) {
            if (registryName.equals("a")) return a;
            if (registryName.equals("b")) return b;
            throw new IllegalArgumentException("Invalid registry");
        }

        public ProgramState setRegistryValue(String registryName, int value) {
            int a = this.a, b = this.b;
            if (registryName.equals("a")) a = value;
            else if (registryName.equals("b")) b = value;
            else throw new IllegalArgumentException("Invalid registry");
            return new ProgramState(a, b, instructionPointer);
        }

        public ProgramState moveInstructionPointer(int offset) {
            return new ProgramState(a, b, instructionPointer + offset);
        }
    }

    private static abstract class Instruction {
        static Instruction parse(String instruction) {
            if (instruction.startsWith("hlf"))
                return new HLF(instruction.replace("hlf", ""));
            if (instruction.startsWith("tpl"))
                return new TPL(instruction.replace("tpl", ""));
            if (instruction.startsWith("inc"))
                return new INC(instruction.replace("inc", ""));
            if (instruction.startsWith("jio"))
                return new JIO(instruction.replace("jio", ""));
            if (instruction.startsWith("jie"))
                return new JIE(instruction.replace("jie", ""));
            if (instruction.startsWith("jmp"))
                return new JMP(instruction.replace("jmp", ""));
            return null;
        }

        public abstract ProgramState execute(ProgramState state);
    }

    private static abstract class ArithmeticInstruction extends Instruction {
        final String registryName;

        public ArithmeticInstruction(String arguments) {
            this.registryName = arguments.trim();
        }
    }

    private static class TPL extends ArithmeticInstruction {

        public TPL(String arguments) {
            super(arguments);
        }

        @Override
        public ProgramState execute(ProgramState state) {
            return state.setRegistryValue(registryName, state.getRegistryValue(registryName) * 3)
                    .moveInstructionPointer(1);
        }
    }

    private static class INC extends ArithmeticInstruction {

        public INC(String arguments) {
            super(arguments);
        }

        @Override
        public ProgramState execute(ProgramState state) {
            return state.setRegistryValue(registryName, state.getRegistryValue(registryName) + 1)
                    .moveInstructionPointer(1);
        }
    }

    private static class HLF extends ArithmeticInstruction {

        public HLF(String arguments) {
            super(arguments);
        }

        @Override
        public ProgramState execute(ProgramState state) {
            return state.setRegistryValue(registryName, state.getRegistryValue(registryName) / 2)
                    .moveInstructionPointer(1);
        }
    }

    private static abstract class JumpInstruction extends Instruction {
        final String registryName;
        final int offset;

        public JumpInstruction(String arguments) {
            if (arguments.contains(",")) {
                String[] parts = arguments.trim().split(",");
                this.registryName = parts[0].trim();
                this.offset = Integer.parseInt(parts[1].trim());
            } else {
                this.registryName = "";
                this.offset = Integer.parseInt(arguments.trim());
            }
        }
    }

    private static class JIO extends JumpInstruction {

        public JIO(String arguments) {
            super(arguments);
        }

        @Override
        public ProgramState execute(ProgramState state) {
            if (state.getRegistryValue(registryName) == 1)
                return state.moveInstructionPointer(offset);
            return state.moveInstructionPointer(1);
        }
    }

    private static class JIE extends JumpInstruction {

        public JIE(String arguments) {
            super(arguments);
        }

        @Override
        public ProgramState execute(ProgramState state) {
            if (state.getRegistryValue(registryName) % 2 == 0)
                return state.moveInstructionPointer(offset);
            return state.moveInstructionPointer(1);
        }
    }

    private static class JMP extends JumpInstruction {
        public JMP(String arguments) {
            super(arguments);
        }

        @Override
        public ProgramState execute(ProgramState state) {
            return state.moveInstructionPointer(offset);
        }
    }
}