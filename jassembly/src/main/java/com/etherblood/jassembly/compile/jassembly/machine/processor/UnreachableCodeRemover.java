package com.etherblood.jassembly.compile.jassembly.machine.processor;

import com.etherblood.jassembly.compile.jassembly.Register;
import com.etherblood.jassembly.compile.jassembly.machine.Labelled;
import com.etherblood.jassembly.compile.jassembly.machine.LabelledInstruction;
import com.etherblood.jassembly.usability.code.InstructionMapping;
import com.etherblood.jassembly.usability.code.MachineInstruction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Philipp
 */
public class UnreachableCodeRemover {

    private final Set<MachineInstruction> terminalCommands;

    public UnreachableCodeRemover(InstructionMapping mapping) {
        terminalCommands = new HashSet<>();
        terminalCommands.add(mapping.terminate());
        for (Register a : Register.values()) {
            terminalCommands.add(mapping.move(a, Register.PC));
        }
        for (Register a : Register.values()) {
            terminalCommands.add(mapping.readInstruction());
            terminalCommands.add(mapping.readConstant(Register.PC));
            terminalCommands.add(mapping.read(a, Register.PC));
        }
        for (Register a : Register.values()) {
            terminalCommands.add(mapping.identity(a, Register.PC));
            terminalCommands.add(mapping.complement(a, Register.PC));
            terminalCommands.add(mapping.negate(a, Register.PC));
            terminalCommands.add(mapping.reverse(a, Register.PC));
            terminalCommands.add(mapping.increment(a, Register.PC));
            terminalCommands.add(mapping.decrement(a, Register.PC));
        }
        for (Register a : Register.values()) {
            for (Register b : Register.values()) {
                terminalCommands.add(mapping.add(a, b, Register.PC));
                terminalCommands.add(mapping.subtract(a, b, Register.PC));
                terminalCommands.add(mapping.or(a, b, Register.PC));
                terminalCommands.add(mapping.xor(a, b, Register.PC));
                terminalCommands.add(mapping.and(a, b, Register.PC));
                terminalCommands.add(mapping.leftShift(a, b, Register.PC));
                terminalCommands.add(mapping.rightShift(a, b, Register.PC));
            }
        }
    }

    public List<Labelled> removeDeadCode(List<Labelled> instructions) {
        List<Labelled> result = new ArrayList<>();
        boolean isDead = false;
        for (Labelled instruction : instructions) {
            if (!instruction.getLabels().isEmpty()) {
                isDead = false;
            }
            if (!isDead) {
                result.add(instruction);
                if (instruction instanceof LabelledInstruction) {
                    LabelledInstruction simple = (LabelledInstruction) instruction;
                    isDead = terminalCommands.contains(simple.getInstruction());
                }
            }
        }
        System.out.println("dead code removal: " + instructions.size() + " -> " + result.size());
        return result;
    }
}
