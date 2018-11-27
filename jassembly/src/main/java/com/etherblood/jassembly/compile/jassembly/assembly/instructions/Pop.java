package com.etherblood.jassembly.compile.jassembly.assembly.instructions;

import com.etherblood.jassembly.compile.jassembly.Register;

/**
 *
 * @author Philipp
 */
public class Pop extends JassemblyInstruction {
    private final Register to;

    public Pop(Register to) {
        this.to = to;
    }

    public Register getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "Pop -> " + to;
    }

}
