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
import com.etherblood.circuit.compile.ast.statement.IfElseStatement;
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
        Token identifier = tokens.pop();
        assertTokenType(identifier, TokenType.IDENTIFIER);
        consume(tokens, TokenType.OPEN_PAREN, TokenType.CLOSE_PAREN, TokenType.OPEN_BRACE);
        List<Statement> statements = new ArrayList<>();
        while (tokens.peek().getType() != TokenType.CLOSE_BRACE) {
            statements.add(parseStatement(tokens));
        }
        consume(tokens, TokenType.CLOSE_BRACE);
        return new FunctionDeclaration(identifier.getValue(), statements.toArray(new Statement[statements.size()]));
    }

    private Statement parseStatement(ConsumableIterator<Token> tokens) {
        TokenType type = tokens.peek().getType();
        switch (type) {
            case IDENTIFIER: {
                String variable = tokens.pop().getValue();
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
                Token token = tokens.pop();
                assertTokenType(token, TokenType.IDENTIFIER);
                String variable = token.getValue();
                Expression expression;
                if (tokens.peek().getType() == TokenType.OP_ASSIGN) {
                    consume(tokens, TokenType.OP_ASSIGN);
                    expression = parseExpression(tokens);
                } else {
                    expression = null;
                }
                consume(tokens, TokenType.SEMICOLON);
                return new DeclareStatement(variable, expression);
            }
            case KEYWORD_IF: {
                consume(tokens, TokenType.KEYWORD_IF, TokenType.OPEN_PAREN);
                Expression expression = parseExpression(tokens);
                consume(tokens, TokenType.CLOSE_PAREN);
                Statement ifStatement = parseStatement(tokens);
                consume(tokens, TokenType.SEMICOLON);
                Token token = tokens.peek();
                Statement elseStatement;
                if (token.getType() == TokenType.KEYWORD_ELSE) {
                    consume(tokens, TokenType.KEYWORD_ELSE);
                    elseStatement = parseStatement(tokens);
                    consume(tokens, TokenType.SEMICOLON);
                } else {
                    elseStatement = null;
                }
                return new IfElseStatement(expression, ifStatement, elseStatement);
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
        Token token = tokens.peek();
        BinaryOperator operator = null;
        switch (token.getType()) {
            case OP_OR:
                tokens.pop();
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
        Token token = tokens.peek();
        BinaryOperator operator = null;
        switch (token.getType()) {
            case OP_AND:
                tokens.pop();
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
        Token token = tokens.peek();
        BinaryOperator operator = null;
        switch (token.getType()) {
            case OP_EQUAL:
                tokens.pop();
                operator = BinaryOperator.EQUAL;
                break;
            case OP_NOTEQUAL:
                tokens.pop();
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
        Token token = tokens.peek();
        BinaryOperator operator = null;
        switch (token.getType()) {
            case OP_GREATER:
                tokens.pop();
                operator = BinaryOperator.GREATER_THAN;
                break;
            case OP_GREATEROREQUAL:
                tokens.pop();
                operator = BinaryOperator.GREATER_OR_EQUAL;
                break;
            case OP_LESS:
                tokens.pop();
                operator = BinaryOperator.LESS_THAN;
                break;
            case OP_LESSOREQUAL:
                tokens.pop();
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
        Token token = tokens.peek();
        BinaryOperator operator = null;
        switch (token.getType()) {
            case OP_PLUS:
                tokens.pop();
                operator = BinaryOperator.ADD;
                break;
            case OP_MINUS:
                tokens.pop();
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
        Token token = tokens.peek();
        BinaryOperator operator = null;
        switch (token.getType()) {
            case OP_MULTIPLY:
                tokens.pop();
                operator = BinaryOperator.MULT;
                break;
            case OP_DIVIDE:
                tokens.pop();
                operator = BinaryOperator.DIV;
                break;
            case OP_REMAINDER:
                tokens.pop();
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
        Token token = tokens.pop();
        switch (token.getType()) {
            case IDENTIFIER:
                return new VariableExpression(token.getValue());
            case LITERAL_INT:
                return new ConstantExpression(Integer.valueOf(token.getValue()));
            case LITERAL_BOOL:
                boolean value = Boolean.valueOf(token.getValue());
                return new ConstantExpression(value ? ~0 : 0);
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
            assertTokenType(tokens.pop(), type);
        }
    }

    private void assertTokenType(Token token, TokenType type) throws IllegalArgumentException {
        if (token.getType() != type) {
            throw new IllegalArgumentException("Unexpected token " + token + ", expected token of type " + type);
        }
    }

}
