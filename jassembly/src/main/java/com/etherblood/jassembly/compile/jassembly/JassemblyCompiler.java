package com.etherblood.jassembly.compile.jassembly;

import com.etherblood.jassembly.usability.code.Instruction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class JassemblyCompiler {

    private boolean removeDeadCode = false;
    private static final List<Instruction> TERMINAL_COMMANDS = Arrays.asList(Instruction.TERMINATE, Instruction.MOV_AX_PC);

    public List<Integer> toProgram(List<Labelled> commands) {
        if (removeDeadCode) {
            commands = removeDeadCode(commands);
        }

        JassemblyContext context = context(commands);
        List<Integer> program = new ArrayList<>();
        for (Labelled command : commands) {
            program.add(command.toCode(context));
        }
        return program;
    }

    private List<Labelled> removeDeadCode(List<Labelled> commands) {
        List<Labelled> result = new ArrayList<>();
        boolean isDead = false;
        for (Labelled command : commands) {
            if (!command.getLabels().isEmpty()) {
                isDead = false;
            }
            if (!isDead) {
                result.add(command);
                if (command instanceof LabelledInstruction) {
                    LabelledInstruction simple = (LabelledInstruction) command;
                    isDead = TERMINAL_COMMANDS.contains(simple.getCommand());
                }
            }
        }
        return result;
    }

    private JassemblyContext context(List<Labelled> commands) {
        return (String label) -> {
            for (int i = 0; i < commands.size(); i++) {
                Labelled command = commands.get(i);
                if (command.getLabels().contains(label)) {
                    return i;
                }
            }
            throw new IllegalStateException(label + " not found.");
        };
    }

    public JassemblyCompiler removeDeadCode(boolean value) {
        removeDeadCode = value;
        return this;
    }
}
