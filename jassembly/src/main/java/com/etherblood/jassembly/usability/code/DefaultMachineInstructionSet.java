package com.etherblood.jassembly.usability.code;

import com.etherblood.jassembly.compile.jassembly.Register;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class DefaultMachineInstructionSet extends SimpleMachineInstructionSet {

    public DefaultMachineInstructionSet(InstructionMapping mapping) {
        super(mapping, instructions(mapping));
    }

    private static Collection<MachineInstruction> instructions(InstructionMapping mapping) {
        Set<MachineInstruction> instructions = new LinkedHashSet<>();
        instructions.add(mapping.noop());
        instructions.add(mapping.readInstruction());
        instructions.add(mapping.terminate());

        for (Register a : Register.values()) {
            instructions.add(mapping.constantZero(a));
            instructions.add(mapping.constantOne(a));
            instructions.add(mapping.constantNegativeOne(a));
        }

        for (Register a : Register.values()) {
            instructions.add(mapping.readConstant(a));
        }

        for (Register a : Register.values()) {
            for (Register b : Register.values()) {
                instructions.add(mapping.write(a, b));
                instructions.add(mapping.read(a, b));
            }
        }

        for (Register a : Register.values()) {
            for (Register b : Register.values()) {
                instructions.add(mapping.move(a, b));
            }
        }

        for (Register a : Register.values()) {
            for (Register b : Register.values()) {
                instructions.add(mapping.any(a, b));
                instructions.add(mapping.complement(a, b));
                instructions.add(mapping.reverse(a, b));
                instructions.add(mapping.negate(a, b));
                instructions.add(mapping.identity(a, b));
                instructions.add(mapping.increment(a, b));
                instructions.add(mapping.decrement(a, b));
            }
        }

        for (Register a : Register.values()) {
            for (Register b : Register.values()) {
                for (Register c : Register.values()) {
                    instructions.add(mapping.add(a, b, c));
                    instructions.add(mapping.subtract(a, b, c));
                    instructions.add(mapping.leftShift(a, b, c));
                    instructions.add(mapping.rightShift(a, b, c));
                    instructions.add(mapping.and(a, b, c));
                    instructions.add(mapping.or(a, b, c));
                    instructions.add(mapping.xor(a, b, c));
                }
            }
        }

        return instructions;
    }
}
