package com.etherblood.jassembly.compile.jassembly.assembly.instructions;

import com.etherblood.jassembly.compile.jassembly.assembly.expressions.JassemblyExpression;

/**
 *
 * @author Philipp
 */
public class Write extends JassemblyInstruction {

    private final JassemblyExpression from;
    private final JassemblyExpression to;

    public Write(JassemblyExpression from, JassemblyExpression to) {
        this.from = from;
        this.to = to;
    }

    public JassemblyExpression getFrom() {
        return from;
    }

    public JassemblyExpression getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "Write(" + to + ") = " + from;
    }
}
