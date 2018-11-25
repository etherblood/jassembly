package com.etherblood.jassembly.compile.jassembly.machine;

import com.etherblood.jassembly.compile.jassembly.Register;
import com.etherblood.jassembly.usability.code.InstructionMapping;
import com.etherblood.jassembly.usability.code.MachineInstruction;
import com.etherblood.jassembly.usability.code.MachineInstructionSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class Jmachine {

    private final List<Labelled> commands = new ArrayList<>();
    private final List<String> pendingLabels = new ArrayList<>();
    private final InstructionMapping mapping;
    private final MachineInstructionSet instructionSet;

    public Jmachine(InstructionMapping mapping, MachineInstructionSet instructionSet) {
        this.mapping = mapping;
        this.instructionSet = instructionSet;
    }

    public void labelNext(String label) {
        pendingLabels.add(label);
    }
    
    public void constant(String label, Register register) {
        add(simple(mapping.readConstant(register)));
        add(labelLiteral(label));
    }

    public void constant(int value, Register register) {
        add(simple(mapping.readConstant(register)));
        add(literal(value));
    }
    
    public void instruction(MachineInstruction instruction) {
        if(!instructionSet.supports(instruction)) {
            throw new IllegalArgumentException(instruction.toString());
        }
        add(instruction);
    }
    
    public void instructionCode(MachineInstruction instruction) {
        if(!instructionSet.supports(instruction)) {
            throw new IllegalArgumentException(instruction.toString());
        }
        add(new LabelledInstructionCode(instruction));
    }

    private void add(MachineInstruction command) {
        add(simple(command));
    }

    public List<Labelled> getInstructions() {
        return commands;
    }

    private void add(Labelled command) {
        command.getLabels().addAll(pendingLabels);
        pendingLabels.clear();
        commands.add(command);
    }

    private static Labelled simple(MachineInstruction command) {
        return new LabelledInstruction(command);
    }

    private static Labelled literal(int value) {
        return new LabelledLiteral(value);
    }

    private static Labelled labelLiteral(String value) {
        return new LabelledLabel(value);
    }

    public InstructionMapping map() {
        return mapping;
    }
}
