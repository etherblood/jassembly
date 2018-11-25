package com.etherblood.jassembly.compile;

import com.etherblood.jassembly.computer.Computer;
import com.etherblood.jassembly.core.Engine;
import com.etherblood.jassembly.usability.code.DefaultMachineInstructionSet;
import com.etherblood.jassembly.usability.code.Instruction;
import com.etherblood.jassembly.usability.code.MachineInstructionSet;
import com.etherblood.jassembly.usability.code.MachineInstructions;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Philipp
 */
public class TestPrograms {

    private final SimpleCompiler compiler = new SimpleCompiler();
    private final MachineInstructions mapping = new MachineInstructions();
    private final MachineInstructionSet instructionSet = new DefaultMachineInstructionSet(mapping);

    @Test
    public void _12x9() {
        List<Integer> program = compiler.compile(loadFile("12x9.txt"), instructionSet);
        long result = compute(program, 16, 200);
        assertEquals(108, result);
    }

    @Test
    public void _3x5() {
        List<Integer> program = compiler.compile(loadFile("3x5.txt"), instructionSet);
        long result = compute(program, 16, 200);
        assertEquals(15, result);
    }

    @Test
    public void _128x2000() {
        List<Integer> program = compiler.compile(loadFile("128x2000.txt"), instructionSet);
        long result = compute(program, 16, 200);
        assertEquals(59392, result);
    }

    @Test
    public void loop() {
        List<Integer> program = compiler.compile(loadFile("loop.txt"), instructionSet);
        long result = compute(program, 16, 200);
        assertEquals(0, result);
    }

    @Test
    public void fibonacci() {
        List<Integer> program = compiler.compile(loadFile("fibonacci.txt"), instructionSet);
        long result = compute(program, 16, 200);
        assertEquals(55, result);
    }

    @Test
    public void relational() {
        List<Integer> program = compiler.compile(loadFile("relational.txt"), instructionSet);
        long result = compute(program, 16, 200);
        assertEquals(1, result);
    }

    private long compute(List<Integer> program, int width, int stack) {
        Computer computer = new Computer(width, program, program.size() + stack, instructionSet);
        Engine engine = new Engine();
        while (!isTerminal(Math.toIntExact(computer.instruction.getSignals().getAsLong()), instructionSet)) {
            computer.advanceCycle(engine);
        }
        return computer.ax.getSignals().getAsLong();
    }

    private static String loadFile(String filename) {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            return new String(Files.readAllBytes(new File(classloader.getResource(filename).toURI()).toPath()));
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static boolean isTerminal(int instructionCode, MachineInstructionSet instructionSet) {
        return instructionCode == instructionSet.codeByInstruction(instructionSet.map().terminate());
    }
}
