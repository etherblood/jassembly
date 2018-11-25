package com.etherblood.jassembly.compile.jassembly.assembly.expressions;

public class LabelExpression implements CompileTimeExpression {

    private final String label;

    public LabelExpression(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
