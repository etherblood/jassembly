package com.etherblood.circuit.compile;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import com.etherblood.circuit.compile.tokens.Token;
import com.etherblood.circuit.compile.tokens.TokenType;

/**
 *
 * @author Philipp
 */
public class LexerTest {

    public LexerTest() {
    }

    @Test
    public void lex_0() {
        String sampleCode = "int main() {\n"
                + "    return 2;\n"
                + "}";
        List<Token> tokens = new Lexer().tokenify(sampleCode);
        assertEquals(Arrays.asList(
                new Token(TokenType.KEYWORD_INT, "int"),
                new Token(TokenType.IDENTIFIER, "main"),
                new Token(TokenType.OPEN_PAREN, "("),
                new Token(TokenType.CLOSE_PAREN, ")"),
                new Token(TokenType.OPEN_BRACE, "{"),
                new Token(TokenType.KEYWORD_RETURN, "return"),
                new Token(TokenType.LITERAL_INT, "2"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.CLOSE_BRACE, "}")
        ), tokens);
    }

}
