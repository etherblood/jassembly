package com.etherblood.jassembly.usability.code;

/**
 *
 * @author Philipp
 */
public interface MachineInstructionSet {

    int instructionCount();

    MachineInstruction instructionByCode(int code);

    int codeByInstruction(MachineInstruction instruction);
    
    boolean supports(MachineInstruction instruction);
    
    boolean supports(int code);
    
    InstructionMapping map();
}
