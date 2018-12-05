package com.etherblood.jassembly.compile;

import com.etherblood.jassembly.compile.jassembly.language.parsing.Lexer;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import com.etherblood.jassembly.compile.jassembly.language.parsing.tokens.Token;
import com.etherblood.jassembly.compile.jassembly.language.parsing.tokens.TokenType;

/**
 *
 * @author Philipp
 */
public class LexerTest {

    public LexerTest() {
    }

    @Test
    public void lex_0() {
        String sampleCode = "uint main() {\n"
                + "    return 2;\n"
                + "}";
        List<Token> tokens = new Lexer().tokenify(sampleCode);
        assertEquals(Arrays.asList(
                new Token(TokenType.KEYWORD_TYPE, "uint"),
                new Token(TokenType.IDENTIFIER, "main"),
                new Token(TokenType.OPEN_PAREN, "("),
                new Token(TokenType.CLOSE_PAREN, ")"),
                new Token(TokenType.OPEN_BRACE, "{"),
                new Token(TokenType.KEYWORD_RETURN, "return"),
                new Token(TokenType.LITERAL_UINT, "2"),
                new Token(TokenType.SEMICOLON, ";"),
                new Token(TokenType.CLOSE_BRACE, "}")
        ), tokens);
    }

}
