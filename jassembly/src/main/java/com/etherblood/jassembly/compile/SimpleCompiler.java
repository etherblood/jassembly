package com.etherblood.jassembly.compile;

import com.etherblood.jassembly.compile.ast.Program;
import com.etherblood.jassembly.compile.jassembly.assembly.JassemblyCompiler;
import com.etherblood.jassembly.compile.jassembly.assembly.instructions.JassemblyInstruction;
import com.etherblood.jassembly.compile.jassembly.machine.Jmachine;
import com.etherblood.jassembly.compile.jassembly.machine.Labelled;
import com.etherblood.jassembly.compile.jassembly.machine.JmachineCompiler;
import com.etherblood.jassembly.compile.jassembly.machine.processor.ConstantInliner;
import com.etherblood.jassembly.compile.jassembly.machine.processor.DeadCodeRemover;
import com.etherblood.jassembly.compile.jassembly.machine.processor.NoopRemover;
import com.etherblood.jassembly.compile.tokens.Token;
import com.etherblood.jassembly.usability.code.InstructionMapping;
import com.etherblood.jassembly.usability.code.MachineInstructionSet;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class SimpleCompiler {

    private final Lexer lexer = new Lexer();
    private final Parser parser = new Parser();
    private final CodeGenerator generator = new CodeGenerator();

    public List<Integer> compile(String code, MachineInstructionSet instructionSet) {
        InstructionMapping mapping = instructionSet.map();
        List<Token> tokens = lexer.tokenify(code);
        Program ast = parser.parseProgram(tokens.iterator());
        generator.consume(ast);
        List<JassemblyInstruction> assemblyInstructions = generator.getInstructions();
        for (JassemblyInstruction assemblyInstruction : assemblyInstructions) {
            System.out.println(assemblyInstruction);
        }
        System.out.println();
        Jmachine jmachine = new Jmachine(mapping, instructionSet);
        new JassemblyCompiler().compile(assemblyInstructions, jmachine);
        List<Labelled> commands = jmachine.getInstructions();
        for (int i = 0; i < commands.size(); i++) {
            Labelled command = commands.get(i);
            System.out.println(i + ": " + command);
        }
        System.out.println();
        commands = new DeadCodeRemover(mapping).removeDeadCode(commands);
        commands = new ConstantInliner(mapping).inlineConstants(commands);
        commands = new NoopRemover(mapping).removeNoops(commands);
        System.out.println();
        for (int i = 0; i < commands.size(); i++) {
            Labelled command = commands.get(i);
            System.out.println(i + ": " + command);
        }
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        return new JmachineCompiler(instructionSet).toProgram(commands);
    }
}
