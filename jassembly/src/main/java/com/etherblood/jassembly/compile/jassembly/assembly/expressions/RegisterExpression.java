package com.etherblood.jassembly.compile.jassembly.assembly.expressions;

import com.etherblood.jassembly.compile.jassembly.Register;

public class RegisterExpression implements JassemblyExpression {

    private final Register register;

    public RegisterExpression(Register register) {
        this.register = register;
    }

    public Register getRegister() {
        return register;
    }

    @Override
    public String toString() {
        return register.name();
    }
}
