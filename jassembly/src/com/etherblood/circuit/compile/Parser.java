package com.etherblood.circuit.compile;

import com.etherblood.circuit.compile.ast.Constant;
import com.etherblood.circuit.compile.ast.FunctionDeclaration;
import com.etherblood.circuit.compile.ast.Program;
import com.etherblood.circuit.compile.ast.ReturnStatement;
import com.etherblood.circuit.compile.tokens.Token;
import com.etherblood.circuit.compile.tokens.TokenType;
import java.util.Iterator;

/**
 *
 * @author Philipp
 */
public class Parser {

    public Program parseProgram(Iterator<Token> tokens) {
        return new Program(parseFunctionDeclaration(tokens));
    }

    public FunctionDeclaration parseFunctionDeclaration(Iterator<Token> tokens) {
        consume(tokens, TokenType.KEYWORD_INT);
        Token identifier = tokens.next();
        assertTokenType(identifier, TokenType.IDENTIFIER);
        consume(tokens, TokenType.OPEN_PAREN, TokenType.CLOSE_PAREN, TokenType.OPEN_BRACE);
        ReturnStatement statement = parseStatement(tokens);
        consume(tokens, TokenType.CLOSE_BRACE);
        return new FunctionDeclaration(identifier.getValue(), statement);
    }

    public ReturnStatement parseStatement(Iterator<Token> tokens) {
        consume(tokens, TokenType.KEYWORD_RETURN);
        Constant expression = parseExpression(tokens);
        consume(tokens, TokenType.SEMICOLON);
        return new ReturnStatement(expression);
    }

    public Constant parseExpression(Iterator<Token> tokens) {
        Token token = tokens.next();
        assertTokenType(token, TokenType.LITERAL_INT);
        return new Constant(Integer.valueOf(token.getValue()));
    }

    private void consume(Iterator<Token> tokens, TokenType... types) {
        for (TokenType type : types) {
            Token token = tokens.next();
            assertTokenType(token, type);
        }
    }

    private void assertTokenType(Token token, TokenType type) throws IllegalArgumentException {
        if (token.getType() != type) {
            throw new IllegalArgumentException("Unexpected token " + token + ", expected token of type " + type);
        }
    }

}
