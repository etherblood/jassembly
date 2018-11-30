package com.etherblood.jassembly.usability.code;

import com.etherblood.jassembly.compile.jassembly.Register;

/**
 *
 * @author Philipp
 */
public interface InstructionMapping {

    MachineInstruction add(Register a, Register b, Register c);

    MachineInstruction and(Register a, Register b, Register c);

    MachineInstruction any(Register a, Register c);

    MachineInstruction complement(Register a, Register c);

    MachineInstruction constantFalse(Register c);

    MachineInstruction constantNegativeOne(Register c);

    MachineInstruction constantOne(Register c);

    MachineInstruction constantSignBit(Register c);

    MachineInstruction constantTrue(Register c);

    MachineInstruction constantZero(Register c);

    MachineInstruction decrement(Register a, Register c);

    MachineInstruction execute(Register a);

    MachineInstruction identity(Register a, Register c);

    MachineInstruction increment(Register a, Register c);

    MachineInstruction jump(Register a);

    MachineInstruction leftShift(Register a, Register b, Register c);

    MachineInstruction move(Register a, Register c);

    MachineInstruction negate(Register a, Register c);
    
    MachineInstruction noop();

    MachineInstruction or(Register a, Register b, Register c);

    MachineInstruction read(Register a, Register c);

    MachineInstruction readConstant(Register c);

    MachineInstruction readInstruction();

    MachineInstruction reverse(Register a, Register c);

    MachineInstruction rightShift(Register a, Register b, Register c);

    MachineInstruction subtract(Register a, Register b, Register c);

    MachineInstruction terminate();

    MachineInstruction write(Register a, Register b);

    MachineInstruction xor(Register a, Register b, Register c);

    boolean writesTo(MachineInstruction instruction, Register register);

    boolean readsFrom(MachineInstruction instruction, Register register);
}
