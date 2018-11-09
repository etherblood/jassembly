package com.etherblood.jassembly.compile;

import com.etherblood.jassembly.compile.ast.Program;
import com.etherblood.jassembly.compile.jassembly.Jassembly;
import com.etherblood.jassembly.compile.tokens.Token;
import java.util.List;

/**
 *
 * @author Philipp
 */
public class SimpleCompiler {

    private final Lexer lexer = new Lexer();
    private final Parser parser = new Parser();
    private final CodeGenerator generator = new CodeGenerator();

    public List<Integer> compile(String code) {
        List<Token> tokens = lexer.tokenify(code);
        Program ast = parser.parseProgram(tokens.iterator());
        Jassembly jassembly = new Jassembly();
        generator.generateCode(ast, jassembly);
        return jassembly.toProgram();
    }
}
