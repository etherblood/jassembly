package com.etherblood.jassembly.compile.jassembly.machine.processor;

import com.etherblood.jassembly.compile.jassembly.Register;
import com.etherblood.jassembly.compile.jassembly.machine.Labelled;
import com.etherblood.jassembly.compile.jassembly.machine.LabelledInstruction;
import com.etherblood.jassembly.compile.jassembly.machine.LabelledLiteral;
import com.etherblood.jassembly.usability.code.InstructionMapping;
import com.etherblood.jassembly.usability.code.MachineInstruction;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Philipp
 */
public class ConstantInliner {

    private final Map<Integer, Map<Register, MachineInstruction>> constantInstructions;
    private final Map<MachineInstruction, Register> readInstructions;

    public ConstantInliner(InstructionMapping mapping) {
        constantInstructions = new HashMap<>();
        for (Register register : Register.values()) {
            constantInstructions.computeIfAbsent(0, x -> new EnumMap<>(Register.class)).put(register, mapping.constantZero(register));
            constantInstructions.computeIfAbsent(1, x -> new EnumMap<>(Register.class)).put(register, mapping.constantOne(register));
            constantInstructions.computeIfAbsent(-1, x -> new EnumMap<>(Register.class)).put(register, mapping.constantNegativeOne(register));
        }
        readInstructions = new HashMap<>();
        for (Register register : Register.values()) {
            readInstructions.put(mapping.readConstant(register), register);
        }
    }

    public List<Labelled> inlineConstants(List<Labelled> instructions) {
        List<Labelled> result = new ArrayList<>();
        int i;
        for (i = 1; i < instructions.size(); i++) {
            Labelled instruction = instructions.get(i);
            Labelled prev = instructions.get(i - 1);
            Map<Register, MachineInstruction> map = null;
            if (instruction instanceof LabelledLiteral) {
                LabelledLiteral literal = (LabelledLiteral) instruction;
                map = constantInstructions.get(literal.getValue());
            }

            if (map != null && prev instanceof LabelledInstruction) {
                LabelledInstruction lblInstruction = (LabelledInstruction) prev;
                Register reg = readInstructions.get(lblInstruction.getCommand());
                if (reg != null) {
                    if (!instruction.getLabels().isEmpty()) {
                        throw new UnsupportedOperationException("Label on literals not supported.");
                    }
                    LabelledInstruction newInstruction = new LabelledInstruction(map.get(reg));
                    newInstruction.getLabels().addAll(lblInstruction.getLabels());
                    result.add(newInstruction);
                    i++;
                    continue;
                }
            }
            result.add(instructions.get(i - 1));
        }
        if (i == instructions.size()) {
            result.add(instructions.get(instructions.size() - 1));
        }
        System.out.println("constant inlining: " + instructions.size() + " -> " + result.size());
        return result;
    }
}
