package com.etherblood.jassembly.compile.jassembly.machine;

import com.etherblood.jassembly.usability.code.MachineInstructionSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class JmachineCompiler {

    private final MachineInstructionSet instructionSet;

    public JmachineCompiler(MachineInstructionSet instructionSet) {
        this.instructionSet = instructionSet;
    }

    public List<Integer> toProgram(List<Labelled> instructions) {
        JmachineContext context = context(instructions);
        List<Integer> program = new ArrayList<>();
        for (Labelled command : instructions) {
            program.add(labelledToCode(command, context));
        }
        return program;
    }

    private int labelledToCode(Labelled labelled, JmachineContext context) {
        if (labelled instanceof LabelledInstruction) {
            LabelledInstruction instruction = (LabelledInstruction) labelled;
            return instructionSet.codeByInstruction(instruction.getCommand());
        }
        if (labelled instanceof LabelledLiteral) {
            LabelledLiteral literal = (LabelledLiteral) labelled;
            return literal.getValue();
        }
        if (labelled instanceof LabelledLabel) {
            LabelledLabel label = (LabelledLabel) labelled;
            return context.resolveLabel(label.getLabel());
        }
        throw new UnsupportedOperationException(labelled.toString());
    }

    private JmachineContext context(List<Labelled> instructions) {
        return (String label) -> {
            for (int i = 0; i < instructions.size(); i++) {
                Labelled command = instructions.get(i);
                if (command.getLabels().contains(label)) {
                    return i;
                }
            }
            throw new IllegalStateException(label + " not found.");
        };
    }
}
