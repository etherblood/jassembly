package com.etherblood.jassembly.usability.code;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class SimpleMachineInstructionSet implements MachineInstructionSet {

    private final InstructionMapping mapping;
    private final List<MachineInstruction> instructions = new ArrayList<>();

    public SimpleMachineInstructionSet(InstructionMapping mapping, Collection<MachineInstruction> instructions) {
        this.mapping = mapping;
        this.instructions.addAll(instructions);
    }

    @Override
    public int instructionCount() {
        return instructions.size();
    }

    @Override
    public MachineInstruction instructionByCode(int code) {
        return instructions.get(code);
    }

    @Override
    public int codeByInstruction(MachineInstruction instruction) {
        int index = instructions.indexOf(instruction);
        if(index == -1) {
            throw new IllegalArgumentException(instruction.toString());
        }
        return index;
    }

    @Override
    public boolean supports(MachineInstruction instruction) {
        return instructions.contains(instruction);
    }

    @Override
    public boolean supports(int code) {
        return 0 <= code && code < instructions.size();
    }

    @Override
    public InstructionMapping map() {
        return mapping;
    }
}
