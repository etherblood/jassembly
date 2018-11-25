package com.etherblood.jassembly.compile.jassembly.assembly.instructions;

public class Terminate extends JassemblyInstruction {
    
    private final ExitCode exitCode;

    public Terminate(ExitCode exitCode) {
        this.exitCode = exitCode;
    }

    public ExitCode getExitCode() {
        return exitCode;
    }

    @Override
    public String toString() {
        return "Terminate(" + exitCode + ')';
    }
}
