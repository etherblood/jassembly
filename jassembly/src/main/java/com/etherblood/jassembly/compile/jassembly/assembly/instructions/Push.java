package com.etherblood.jassembly.compile.jassembly.assembly.instructions;

import com.etherblood.jassembly.compile.jassembly.assembly.expressions.JassemblyExpression;

/**
 *
 * @author Philipp
 */
public class Push extends JassemblyInstruction {

    private final JassemblyExpression from;

    public Push(JassemblyExpression from) {
        this.from = from;
    }

    public JassemblyExpression getFrom() {
        return from;
    }

    @Override
    public String toString() {
        return "Push " + from;
    }
}
