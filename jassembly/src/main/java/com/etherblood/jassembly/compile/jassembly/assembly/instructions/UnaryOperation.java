package com.etherblood.jassembly.compile.jassembly.assembly.instructions;

import com.etherblood.jassembly.compile.jassembly.Register;
import com.etherblood.jassembly.compile.jassembly.UnaryOperator;
import com.etherblood.jassembly.compile.jassembly.assembly.expressions.JassemblyExpression;

/**
 *
 * @author Philipp
 */
public class UnaryOperation extends JassemblyInstruction {

    private final JassemblyExpression a;
    private final Register dest;
    private final UnaryOperator operator;

    public UnaryOperation(JassemblyExpression a, UnaryOperator operator, Register dest) {
        this.a = a;
        this.dest = dest;
        this.operator = operator;
    }

    public JassemblyExpression getA() {
        return a;
    }

    public Register getDest() {
        return dest;
    }

    public UnaryOperator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return operator + "(" + a + ") -> " + dest;
    }

}
