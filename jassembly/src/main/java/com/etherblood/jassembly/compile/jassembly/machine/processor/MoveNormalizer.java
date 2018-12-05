package com.etherblood.jassembly.compile.jassembly.machine.processor;

import com.etherblood.jassembly.compile.jassembly.Register;
import com.etherblood.jassembly.compile.jassembly.machine.Labelled;
import com.etherblood.jassembly.compile.jassembly.machine.LabelledInstruction;
import com.etherblood.jassembly.usability.code.InstructionMapping;
import com.etherblood.jassembly.usability.code.MachineInstruction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 * @author Philipp
 */
public class MoveNormalizer {

    private final Map<MachineInstruction, Supplier<MachineInstruction>> moveReplacements;

    public MoveNormalizer(InstructionMapping mapping) {
        moveReplacements = new HashMap<>();
        for (Register a : Register.values()) {
            for (Register b : Register.values()) {
                Supplier<MachineInstruction> move = () -> mapping.move(a, b);
                moveReplacements.put(mapping.add(Register.NONE, a, b), move);
                moveReplacements.put(mapping.add(a, Register.NONE, b), move);
                moveReplacements.put(mapping.subtract(a, Register.NONE, b), move);
                moveReplacements.put(mapping.leftShift(a, Register.NONE, b), move);
                moveReplacements.put(mapping.rightShift(a, Register.NONE, b), move);
                moveReplacements.put(mapping.xor(a, Register.NONE, b), move);
                moveReplacements.put(mapping.xor(Register.NONE, a, b), move);
                moveReplacements.put(mapping.or(Register.NONE, a, b), move);
                moveReplacements.put(mapping.or(a, Register.NONE, b), move);
                moveReplacements.put(mapping.or(a, a, b), move);
                moveReplacements.put(mapping.and(a, a, b), move);
            }
        }
    }
    
    public List<Labelled> normalizeMoves(List<Labelled> instructions) {
        List<Labelled> result = new ArrayList<>();
        for (Labelled instruction : instructions) {
            if(instruction instanceof LabelledInstruction) {
                LabelledInstruction lbl = (LabelledInstruction) instruction;
                Supplier<MachineInstruction> replacement = moveReplacements.get(lbl.getInstruction());
                if(replacement != null) {
                    LabelledInstruction labelledInstruction = new LabelledInstruction(replacement.get());
                    labelledInstruction.getLabels().addAll(lbl.getLabels());
                    result.add(labelledInstruction);
                    continue;
                }
            }
            result.add(instruction);
        }
        return result;
    }

}
