package com.etherblood.circuit.compile.jassembly;

import java.util.List;
import java.util.Objects;

public class LabelLiteralCommand extends JassemblyCommand {

    private final String label;
    private final int offset;

    public LabelLiteralCommand(String label) {
        this(label, 0);
    }

    public LabelLiteralCommand(String label, int offset) {
        this.label = Objects.requireNonNull(label);
        this.offset = offset;
    }

    public String getLabel() {
        return label;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public int toCode(List<JassemblyCommand> contextCommands) {
        for (int i = 0; i < contextCommands.size(); i++) {
            JassemblyCommand command = contextCommands.get(i);
            if (command.getLabels().contains(label)) {
                return i + offset;
            }
        }
        throw new IllegalStateException();
    }
}
