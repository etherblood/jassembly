package com.etherblood.jassembly.compile.jassembly.assembly.instructions;

import com.etherblood.jassembly.compile.jassembly.BinaryOperator;
import com.etherblood.jassembly.compile.jassembly.Register;
import com.etherblood.jassembly.compile.jassembly.assembly.expressions.JassemblyExpression;

/**
 *
 * @author Philipp
 */
public class BinaryOperation extends JassemblyInstruction {

    private final JassemblyExpression a, b;
    private final Register dest;
    private final BinaryOperator operator;

    public BinaryOperation(JassemblyExpression a, JassemblyExpression b, BinaryOperator operator, Register dest) {
        this.a = a;
        this.b = b;
        this.dest = dest;
        this.operator = operator;
    }

    public JassemblyExpression getA() {
        return a;
    }

    public JassemblyExpression getB() {
        return b;
    }

    public Register getDest() {
        return dest;
    }

    public BinaryOperator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return operator + "(" + a + ", " + b + ") -> " + dest;
    }

}
