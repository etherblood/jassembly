package com.etherblood.circuit.compile;

import com.etherblood.circuit.compile.ast.FunctionDeclaration;
import com.etherblood.circuit.compile.ast.Program;
import com.etherblood.circuit.compile.ast.statement.ReturnStatement;
import com.etherblood.circuit.compile.ast.expression.BinaryOperationExpression;
import com.etherblood.circuit.compile.ast.expression.BinaryOperator;
import com.etherblood.circuit.compile.ast.expression.ConstantExpression;
import com.etherblood.circuit.compile.ast.expression.Expression;
import com.etherblood.circuit.compile.ast.expression.UnaryOperationExpression;
import com.etherblood.circuit.compile.ast.expression.UnaryOperator;
import com.etherblood.circuit.compile.ast.expression.VariableExpression;
import com.etherblood.circuit.compile.ast.statement.AssignStatement;
import com.etherblood.circuit.compile.ast.statement.DeclareStatement;
import com.etherblood.circuit.compile.ast.statement.ExpressionStatement;
import com.etherblood.circuit.compile.ast.statement.Statement;
import com.etherblood.circuit.compile.tokens.Token;
import com.etherblood.circuit.compile.tokens.TokenType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        List<Statement> statements = new ArrayList<>();
        while (tokens.get().getType() != TokenType.CLOSE_BRACE) {
            statements.add(parseStatement(tokens));
        }
        consume(tokens, TokenType.CLOSE_BRACE);
        return new FunctionDeclaration(identifier.getValue(), statements.toArray(new Statement[statements.size()]));
    }

    private Statement parseStatement(ConsumableIterator<Token> tokens) {
        TokenType type = tokens.get().getType();
        switch (type) {
            case IDENTIFIER: {
                String variable = tokens.consume().getValue();
                consume(tokens, TokenType.OP_ASSIGN);
                Expression expression = parseExpression(tokens);
                return new AssignStatement(variable, expression);
            }
            case KEYWORD_RETURN: {
                consume(tokens, TokenType.KEYWORD_RETURN);
                Expression expression = parseExpression(tokens);
                consume(tokens, TokenType.SEMICOLON);
                return new ReturnStatement(expression);
            }
            case KEYWORD_INT: {
                consume(tokens, TokenType.KEYWORD_INT);
                Token token = tokens.consume();
                assertTokenType(token, TokenType.IDENTIFIER);
                String variable = token.getValue();
                consume(tokens, TokenType.OP_ASSIGN);
                Expression expression = parseExpression(tokens);
                consume(tokens, TokenType.SEMICOLON);
                return new DeclareStatement(variable, expression);
            }
            default: {
                Expression expression = parseExpression(tokens);
                consume(tokens, TokenType.SEMICOLON);
                return new ExpressionStatement(expression);
            }
        }
    }

    private Expression parseExpression(ConsumableIterator<Token> tokens) {
        return parseOr(tokens);
    }

    private Expression parseOr(ConsumableIterator<Token> tokens) {
        Expression a = parseAnd(tokens);
        Token token = tokens.get();
        BinaryOperator operator = null;
        switch (token.getType()) {
            case OP_OR:
                tokens.consume();
                operator = BinaryOperator.OR;
                break;
        }
        if (operator != null) {
            Expression b = parseAnd(tokens);
            return new BinaryOperationExpression(a, operator, b);
        }
        return a;
    }

    private Expression parseAnd(ConsumableIterator<Token> tokens) {
        Expression a = parseEquality(tokens);
        Token token = tokens.get();
        BinaryOperator operator = null;
        switch (token.getType()) {
            case OP_AND:
                tokens.consume();
                operator = BinaryOperator.AND;
                break;
        }
        if (operator != null) {
            Expression b = parseEquality(tokens);
            return new BinaryOperationExpression(a, operator, b);
        }
        return a;
    }

    private Expression parseEquality(ConsumableIterator<Token> tokens) {
        Expression a = parseRelational(tokens);
        Token token = tokens.get();
        BinaryOperator operator = null;
        switch (token.getType()) {
            case OP_EQUAL:
                tokens.consume();
                operator = BinaryOperator.EQUAL;
                break;
            case OP_NOTEQUAL:
                tokens.consume();
                operator = BinaryOperator.NOT_EQUAL;
                break;
        }
        if (operator != null) {
            Expression b = parseRelational(tokens);
            return new BinaryOperationExpression(a, operator, b);
        }
        return a;
    }

    private Expression parseRelational(ConsumableIterator<Token> tokens) {
        Expression a = parseAdditive(tokens);
        Token token = tokens.get();
        BinaryOperator operator = null;
        switch (token.getType()) {
            case OP_GREATER:
                tokens.consume();
                operator = BinaryOperator.GREATER_THAN;
                break;
            case OP_GREATEROREQUAL:
                tokens.consume();
                operator = BinaryOperator.GREATER_OR_EQUAL;
                break;
            case OP_LESS:
                tokens.consume();
                operator = BinaryOperator.LESS_THAN;
                break;
            case OP_LESSOREQUAL:
                tokens.consume();
                operator = BinaryOperator.LESS_OR_EQUAL;
                break;
        }
        if (operator != null) {
            Expression b = parseAdditive(tokens);
            return new BinaryOperationExpression(a, operator, b);
        }
        return a;
    }

    private Expression parseAdditive(ConsumableIterator<Token> tokens) {
        Expression a = parseTerm(tokens);
        Token token = tokens.get();
        BinaryOperator operator = null;
        switch (token.getType()) {
            case OP_PLUS:
                tokens.consume();
                operator = BinaryOperator.ADD;
                break;
            case OP_MINUS:
                tokens.consume();
                operator = BinaryOperator.SUB;
                break;
        }
        if (operator != null) {
            Expression b = parseTerm(tokens);
            return new BinaryOperationExpression(a, operator, b);
        }
        return a;
    }

    private Expression parseTerm(ConsumableIterator<Token> tokens) {
        Expression a = parseFactor(tokens);
        Token token = tokens.get();
        BinaryOperator operator = null;
        switch (token.getType()) {
            case OP_MULTIPLY:
                tokens.consume();
                operator = BinaryOperator.MULT;
                break;
            case OP_DIVIDE:
                tokens.consume();
                operator = BinaryOperator.DIV;
                break;
            case OP_REMAINDER:
                tokens.consume();
                operator = BinaryOperator.REMAINDER;
                break;
        }
        if (operator != null) {
            Expression b = parseFactor(tokens);
            return new BinaryOperationExpression(a, operator, b);
        }
        return a;
    }

    private Expression parseFactor(ConsumableIterator<Token> tokens) {
        Token token = tokens.consume();
        switch (token.getType()) {
            case IDENTIFIER:
                return new VariableExpression(token.getValue());
            case LITERAL_INT:
                return new ConstantExpression(Integer.valueOf(token.getValue()));
            case OP_COMPLEMENT: {
                Expression inner = parseFactor(tokens);
                return new UnaryOperationExpression(UnaryOperator.COMPLEMENT, inner);
            }
            case OP_MINUS: {
                Expression inner = parseFactor(tokens);
                return new UnaryOperationExpression(UnaryOperator.NEGATE, inner);
            }
            case OPEN_PAREN:
                Expression expression = parseExpression(tokens);
                consume(tokens, TokenType.CLOSE_PAREN);
                return expression;
            default:
                throw new AssertionError(token);
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
