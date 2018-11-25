package com.etherblood.jassembly.compile.jassembly.assembly.instructions;

import com.etherblood.jassembly.compile.jassembly.assembly.expressions.JassemblyExpression;


public class ConditionalJump extends JassemblyInstruction {

    private final JassemblyExpression condition, jumpAddress;

    public ConditionalJump(JassemblyExpression condition, JassemblyExpression jumpAddress) {
        this.condition = condition;
        this.jumpAddress = jumpAddress;
    }

    public JassemblyExpression getCondition() {
        return condition;
    }

    public JassemblyExpression getJumpAddress() {
        return jumpAddress;
    }

    @Override
    public String toString() {
        return "ConditionalJump(" + condition + ") -> " + jumpAddress;
    }

}
