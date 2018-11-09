package com.etherblood.jassembly.compile.jassembly;

import java.util.List;
import java.util.Objects;

public class LabelLiteralCommand extends JassemblyCommand {

    private final String label;

    public LabelLiteralCommand(String label) {
        this.label = Objects.requireNonNull(label);
    }

    public String getLabel() {
        return label;
    }

    @Override
    public int toCode(List<JassemblyCommand> contextCommands) {
        for (int i = 0; i < contextCommands.size(); i++) {
            JassemblyCommand command = contextCommands.get(i);
            if (command.getLabels().contains(label)) {
                return i;
            }
        }
        throw new IllegalStateException(label + " not found.");
    }

    @Override
    public String toString() {
        return label + " " + getLabels();
    }
}
