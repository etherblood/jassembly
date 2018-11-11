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
    private static final List<Instruction> TERMINAL_COMMANDS = Arrays.asList(Instruction.TERMINATE, Instruction.JUMP);

    public List<Integer> toProgram(List<JassemblyCommand> commands) {
        if (removeDeadCode) {
            commands = removeDeadCode(commands);
        }

        JassemblyContext context = context(commands);
        List<Integer> program = new ArrayList<>();
        for (JassemblyCommand command : commands) {
            program.add(command.toCode(context));
        }
        return program;
    }

    private List<JassemblyCommand> removeDeadCode(Iterable<JassemblyCommand> commands) {
        List<JassemblyCommand> result = new ArrayList<>();
        boolean isDead = false;
        for (JassemblyCommand command : commands) {
            if (!command.getLabels().isEmpty()) {
                isDead = false;
            }
            if (!isDead) {
                result.add(command);
                if (command instanceof SimpleCommand) {
                    SimpleCommand simple = (SimpleCommand) command;
                    isDead = TERMINAL_COMMANDS.contains(simple.getCommand());
                }
            }
        }
        return result;
    }

    private JassemblyContext context(List<JassemblyCommand> commands) {
        return (String label) -> {
            for (int i = 0; i < commands.size(); i++) {
                JassemblyCommand command = commands.get(i);
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
