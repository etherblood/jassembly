package com.etherblood.jassembly.compile.jassembly.assembly.instructions;

import com.etherblood.jassembly.compile.jassembly.Register;
import com.etherblood.jassembly.compile.jassembly.assembly.expressions.JassemblyExpression;

/**
 *
 * @author Philipp
 */
public class Read extends JassemblyInstruction {

    private final JassemblyExpression from;
    private final Register to;

    public Read(JassemblyExpression from, Register to) {
        this.from = from;
        this.to = to;
    }

    public JassemblyExpression getFrom() {
        return from;
    }

    public Register getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "Read(" + from + ") -> " + to;
    }
}
