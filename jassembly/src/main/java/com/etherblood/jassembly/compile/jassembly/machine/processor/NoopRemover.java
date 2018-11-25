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
public class NoopRemover {

    private final Set<MachineInstruction> noops;

    public NoopRemover(InstructionMapping mapping) {
        noops = new HashSet<>();
        noops.add(mapping.noop());
        for (Register register : Register.values()) {
            noops.add(mapping.move(register, register));
        }
        for (Register register : Register.values()) {
            noops.add(mapping.read(register, Register.NONE));
        }
        for (Register register : Register.values()) {
            noops.add(mapping.any(register, Register.NONE));
            noops.add(mapping.complement(register, Register.NONE));
            noops.add(mapping.negate(register, Register.NONE));
            noops.add(mapping.increment(register, Register.NONE));
            noops.add(mapping.decrement(register, Register.NONE));
            noops.add(mapping.reverse(register, Register.NONE));
            noops.add(mapping.identity(register, Register.NONE));
        }
        for (Register a : Register.values()) {
            for (Register b : Register.values()) {
                noops.add(mapping.add(a, b, Register.NONE));
                noops.add(mapping.subtract(a, b, Register.NONE));
                noops.add(mapping.leftShift(a, b, Register.NONE));
                noops.add(mapping.rightShift(a, b, Register.NONE));
                noops.add(mapping.and(a, b, Register.NONE));
                noops.add(mapping.or(a, b, Register.NONE));
                noops.add(mapping.xor(a, b, Register.NONE));
            }
        }
        for (Register a : Register.values()) {
            noops.add(mapping.add(Register.NONE, a, a));
            noops.add(mapping.add(a, Register.NONE, a));
            noops.add(mapping.subtract(a, Register.NONE, a));
            noops.add(mapping.leftShift(a, Register.NONE, a));
            noops.add(mapping.rightShift(a, Register.NONE, a));
            noops.add(mapping.xor(a, Register.NONE, a));
            noops.add(mapping.xor(Register.NONE, a, a));
            noops.add(mapping.or(Register.NONE, a, a));
            noops.add(mapping.or(a, Register.NONE, a));
            noops.add(mapping.or(a, a, a));
            noops.add(mapping.and(a, a, a));

        }
        noops.remove(mapping.terminate());
    }

    public List<Labelled> removeNoops(List<Labelled> instructions) {
        List<Labelled> result = new ArrayList<>();
        List<String> pendingLabels = new ArrayList<>();
        for (Labelled instruction : instructions) {
            if (instruction instanceof LabelledInstruction) {
                LabelledInstruction lblInstruction = (LabelledInstruction) instruction;
                if (noops.contains(lblInstruction.getCommand())) {
                    pendingLabels.addAll(instruction.getLabels());
                    continue;
                }
            }
            instruction.getLabels().addAll(pendingLabels);
            pendingLabels.clear();
            result.add(instruction);
        }
        if (!pendingLabels.isEmpty()) {
            throw new IllegalStateException("label points to end of program.");
        }
        System.out.println("noop removal: " + instructions.size() + " -> " + result.size());
        return result;
    }
}
