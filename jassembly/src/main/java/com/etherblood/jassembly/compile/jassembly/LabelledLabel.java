package com.etherblood.jassembly.compile.jassembly;

import com.etherblood.jassembly.compile.jassembly.JassemblyContext;
import java.util.List;
import java.util.Objects;

public class LabelledLabel extends Labelled {

    private final String label;

    public LabelledLabel(String label) {
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
