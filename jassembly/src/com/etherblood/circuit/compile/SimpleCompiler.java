package com.etherblood.circuit.compile;

import com.etherblood.circuit.compile.ast.Program;
import com.etherblood.circuit.compile.tokens.Token;
import com.etherblood.circuit.usability.codes.programs.SimpleCommandConsumer;
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
        SimpleCommandConsumer consumer = new SimpleCommandConsumer();
        generator.generateCode(ast, consumer);
        return consumer.getList();
    }
}
