package com.etherblood.circuit.compile;

import com.etherblood.circuit.compile.ast.factor.Factor;
import com.etherblood.circuit.compile.ast.FunctionDeclaration;
import com.etherblood.circuit.compile.ast.Program;
import com.etherblood.circuit.compile.ast.ReturnStatement;
import com.etherblood.circuit.compile.ast.Term;
import com.etherblood.circuit.compile.ast.UnaryOperator;
import com.etherblood.circuit.compile.ast.expression.Expression;
import com.etherblood.circuit.compile.ast.FactorOperator;
import com.etherblood.circuit.compile.ast.TermOperator;
import com.etherblood.circuit.compile.ast.expression.BinaryExpression;
import com.etherblood.circuit.compile.ast.expression.TermExpression;
import com.etherblood.circuit.compile.ast.factor.ExpressionFactor;
import com.etherblood.circuit.compile.ast.factor.LiteralFactor;
import com.etherblood.circuit.compile.ast.factor.UnaryFactor;
import com.etherblood.circuit.compile.tokens.Token;
import com.etherblood.circuit.compile.tokens.TokenType;
import java.util.Iterator;

/**
 *
 * @author Philipp
 */
public class Parser {

    public Program parseProgram(Iterator<Token> tokens) {
        return parseProgram(new ConsumableIterator<>(tokens));
    }

    private Program parseProgram(ConsumableIterator<Token> tokens) {
        return new Program(parseFunctionDeclaration(tokens));
    }

    private FunctionDeclaration parseFunctionDeclaration(ConsumableIterator<Token> tokens) {
        consume(tokens, TokenType.KEYWORD_INT);
        Token identifier = tokens.consume();
        assertTokenType(identifier, TokenType.IDENTIFIER);
        consume(tokens, TokenType.OPEN_PAREN, TokenType.CLOSE_PAREN, TokenType.OPEN_BRACE);
        ReturnStatement statement = parseStatement(tokens);
        consume(tokens, TokenType.CLOSE_BRACE);
        return new FunctionDeclaration(identifier.getValue(), statement);
    }

    private ReturnStatement parseStatement(ConsumableIterator<Token> tokens) {
        consume(tokens, TokenType.KEYWORD_RETURN);
        Expression expression = parseExpression(tokens);
        consume(tokens, TokenType.SEMICOLON);
        return new ReturnStatement(expression);
    }

    private Expression parseExpression(ConsumableIterator<Token> tokens) {
        Term a = parseTerm(tokens);
        Token token = tokens.get();
        TermOperator operator = null;
        switch (token.getType()) {
            case OP_PLUS:
                tokens.consume();
                operator = TermOperator.ADD;
                break;
            case OP_MINUS:
                tokens.consume();
                operator = TermOperator.SUBTRACT;
                break;
        }
        if (operator != null) {
            Term b = parseTerm(tokens);
            return new BinaryExpression(a, operator, b);
        }
        return new TermExpression(a);
    }

    private Term parseTerm(ConsumableIterator<Token> tokens) {
        Factor a = parseFactor(tokens);
        Token token = tokens.get();
        FactorOperator operator = null;
        switch (token.getType()) {
            case OP_MULTIPLY:
                tokens.consume();
                operator = FactorOperator.MULTIPLY;
                break;
            case OP_DIVIDE:
                tokens.consume();
                operator = FactorOperator.DIVIDE;
                break;
            case OP_REMAINDER:
                tokens.consume();
                operator = FactorOperator.REMAINDER;
                break;
        }
        if (operator != null) {
            Factor b = parseFactor(tokens);
            return new Term(a, operator, b);
        }
        return new Term(a);
    }

    private Factor parseFactor(ConsumableIterator<Token> tokens) {
        Token token = tokens.consume();
        switch (token.getType()) {
            case LITERAL_INT:
                return new LiteralFactor(Integer.valueOf(token.getValue()));
            case OP_COMPLEMENT: {
                Factor inner = parseFactor(tokens);
                return new UnaryFactor(UnaryOperator.COMPLEMENT, inner);
            }
            case OP_MINUS: {
                Factor inner = parseFactor(tokens);
                return new UnaryFactor(UnaryOperator.NEGATE, inner);
            }
            case OPEN_PAREN:
                Expression expression = parseExpression(tokens);
                consume(tokens, TokenType.CLOSE_PAREN);
                return new ExpressionFactor(expression);
            default:
                throw new AssertionError();
        }
    }

    private void consume(ConsumableIterator<Token> tokens, TokenType... types) {
        for (TokenType type : types) {
            assertTokenType(tokens.consume(), type);
        }
    }

    private void assertTokenType(Token token, TokenType type) throws IllegalArgumentException {
        if (token.getType() != type) {
            throw new IllegalArgumentException("Unexpected token " + token + ", expected token of type " + type);
        }
    }

}
