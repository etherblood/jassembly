package com.etherblood.jassembly.compile.jassembly.machine;

import com.etherblood.jassembly.usability.code.MachineInstruction;

public class LabelledInstructionCode extends Labelled {

    private final MachineInstruction instruction;

    public LabelledInstructionCode(MachineInstruction instruction) {
        this.instruction = instruction;
    }

    public MachineInstruction getInstruction() {
        return instruction;
    }

    @Override
    public String toString() {
        return instruction.toString() + " " + getLabels();
    }
}
