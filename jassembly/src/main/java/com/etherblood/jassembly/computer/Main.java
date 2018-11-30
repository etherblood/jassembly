package com.etherblood.jassembly.computer;

import com.etherblood.jassembly.compile.SimpleCompiler;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.ExitCode;
import com.etherblood.jassembly.core.Engine;
import com.etherblood.jassembly.usability.code.DefaultMachineInstructionSet;
import com.etherblood.jassembly.usability.code.MachineInstructionSet;
import com.etherblood.jassembly.usability.code.MachineInstructions;
import com.etherblood.jassembly.usability.monitoring.ProgramStats;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        String sourceFile = "3x5.txt";
        String sampleCode = readFile(sourceFile);
        MachineInstructions machineInstructions = new MachineInstructions();
        MachineInstructionSet instructionSet = new DefaultMachineInstructionSet(machineInstructions);
        System.out.println("instructions: " + instructionSet.instructionCount());
        System.out.println();
        List<Integer> program = new SimpleCompiler().compile(sampleCode, instructionSet);

        int width = 16;
        int ram = 512;
        Computer computer = new Computer(width, program, ram, instructionSet);
        System.out.println("Computer built from " + computer.countGates() + " nands.");
        System.out.println();
        ProgramStats stats = new ProgramStats();
        Engine engine = new Engine();
        computer.printState();
        while (!isTerminal(Math.toIntExact(computer.instruction.getSignals().getAsLong()), instructionSet)) {
            stats.getCycles().add(computer.advanceCycle(engine));
            computer.printState();
        }
        System.out.println();
        System.out.println("terminated with exit code: " + ExitCode.values()[Math.toIntExact(computer.cx.getSignals().getAsLong())]);
        System.out.println();
        System.out.println("Statistics:");
        System.out.println("duration: " + stats.duration());
        System.out.println("cycles: " + stats.getCycles().size());
        System.out.println("ticks: " + stats.ticks());
        System.out.println("gate updates: " + stats.updatedGates());
    }

    private static boolean isTerminal(int instructionCode, MachineInstructionSet instructionSet) {
        return instructionCode == instructionSet.codeByInstruction(instructionSet.map().terminate());
    }

    private static String readFile(String sourceFile) throws IOException, URISyntaxException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        String sampleCode = new String(Files.readAllBytes(new File(classloader.getResource(sourceFile).toURI()).toPath()));
        return sampleCode;
    }

}
