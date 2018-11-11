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
    public int toCode(JassemblyContext context) {
        return context.resolveLabel(label);
    }

    @Override
    public String toString() {
        return label + " " + getLabels();
    }
}
