package com.etherblood.circuit.compile;

import com.etherblood.circuit.compile.ast.expressions.Constant;
import com.etherblood.circuit.compile.ast.FunctionDeclaration;
import com.etherblood.circuit.compile.ast.Program;
import com.etherblood.circuit.compile.ast.ReturnStatement;
import com.etherblood.circuit.compile.ast.expressions.UnaryOperation;
import com.etherblood.circuit.compile.tokens.Token;
import com.etherblood.circuit.compile.tokens.TokenType;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Philipp
 */
public class ParserTest {

    public ParserTest() {
    }

    @Test
    public void program_0() {
        String sampleCode = "int main() {\n"
                + "    return ~-2;\n"
                + "}";
        List<Token> tokens = new Lexer().tokenify(sampleCode);
        Program program = new Parser().parseProgram(tokens.iterator());
        FunctionDeclaration function = program.getFunction();
        ReturnStatement statement = function.getStatement();
        UnaryOperation invert = (UnaryOperation)statement.getExpression();
        UnaryOperation negate = (UnaryOperation)invert.getExpression();
        Constant constant = (Constant)negate.getExpression();
        assertEquals("main", function.getIdentifier());
        assertEquals(TokenType.COMPLEMENT, invert.getOperator());
        assertEquals(TokenType.NEGATION, negate.getOperator());
        assertEquals(2, constant.getValue());
    }

}
