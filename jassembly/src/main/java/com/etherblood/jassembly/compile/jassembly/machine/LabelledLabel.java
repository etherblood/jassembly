package com.etherblood.jassembly.compile.jassembly.machine;

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
    public String toString() {
        return label + " " + getLabels();
    }
}
