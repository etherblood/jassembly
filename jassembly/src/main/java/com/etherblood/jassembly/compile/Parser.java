package com.etherblood.jassembly.compile;

import com.etherblood.jassembly.compile.ast.FunctionDeclaration;
import com.etherblood.jassembly.compile.ast.Program;
import com.etherblood.jassembly.compile.ast.statement.block.Block;
import com.etherblood.jassembly.compile.ast.statement.block.BlockItem;
import com.etherblood.jassembly.compile.ast.statement.ReturnStatement;
import com.etherblood.jassembly.compile.ast.expression.BinaryOperationExpression;
import com.etherblood.jassembly.compile.ast.expression.BinaryOperator;
import com.etherblood.jassembly.compile.ast.expression.ConstantExpression;
import com.etherblood.jassembly.compile.ast.expression.Expression;
import com.etherblood.jassembly.compile.ast.expression.FunctionCallExpression;
import com.etherblood.jassembly.compile.ast.expression.UnaryOperationExpression;
import com.etherblood.jassembly.compile.ast.expression.UnaryOperator;
import com.etherblood.jassembly.compile.ast.expression.VariableExpression;
import com.etherblood.jassembly.compile.ast.statement.AssignStatement;
import com.etherblood.jassembly.compile.ast.statement.BreakStatement;
import com.etherblood.jassembly.compile.ast.statement.block.VariableDeclaration;
import com.etherblood.jassembly.compile.ast.statement.ExpressionStatement;
import com.etherblood.jassembly.compile.ast.statement.IfElseStatement;
import com.etherblood.jassembly.compile.ast.statement.Statement;
import com.etherblood.jassembly.compile.ast.statement.WhileStatement;
import com.etherblood.jassembly.compile.tokens.Token;
import com.etherblood.jassembly.compile.tokens.TokenType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Philipp
 */
public class Parser {
    
    private static final BinaryOperator[][] BINARY_OPERATOR_PRECEDENCE = {
        {BinaryOperator.LAZY_OR},
        {BinaryOperator.LAZY_AND},
        {BinaryOperator.OR},
        {BinaryOperator.XOR},
        {BinaryOperator.AND},
        {BinaryOperator.EQUAL, BinaryOperator.NOT_EQUAL},
        {BinaryOperator.LESS_THAN, BinaryOperator.LESS_OR_EQUAL, BinaryOperator.GREATER_THAN, BinaryOperator.GREATER_OR_EQUAL},
        {BinaryOperator.LSHIFT, BinaryOperator.RSHIFT},
        {BinaryOperator.ADD, BinaryOperator.SUB},
        {BinaryOperator.MULT, BinaryOperator.DIV, BinaryOperator.REMAINDER}};
    private static final Map<TokenType, BinaryOperator> TOKEN_TO_BINARY_OPERATOR = new EnumMap<>(TokenType.class);
    
    static {
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_AND, BinaryOperator.AND);
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_DIVIDE, BinaryOperator.DIV);
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_EQUAL, BinaryOperator.EQUAL);
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_GREATER, BinaryOperator.GREATER_THAN);
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_GREATEROREQUAL, BinaryOperator.GREATER_OR_EQUAL);
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_LAZY_AND, BinaryOperator.LAZY_AND);
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_LAZY_OR, BinaryOperator.LAZY_OR);
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_LESS, BinaryOperator.LESS_THAN);
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_LESSOREQUAL, BinaryOperator.LESS_OR_EQUAL);
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_LSHIFT, BinaryOperator.LSHIFT);
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_MINUS, BinaryOperator.SUB);
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_MULTIPLY, BinaryOperator.MULT);
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_NOTEQUAL, BinaryOperator.NOT_EQUAL);
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_OR, BinaryOperator.OR);
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_PLUS, BinaryOperator.ADD);
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_REMAINDER, BinaryOperator.REMAINDER);
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_RSHIFT, BinaryOperator.RSHIFT);
        TOKEN_TO_BINARY_OPERATOR.put(TokenType.OP_XOR, BinaryOperator.XOR);
    }

    public Program parseProgram(Iterator<Token> tokens) {
        return parseProgram(new ConsumableIterator<>(tokens));
    }

    private Program parseProgram(ConsumableIterator<Token> tokens) {
        List<FunctionDeclaration> functions = new ArrayList<>();
        while (tokens.peek() != null) {
            functions.add(parseFunctionDeclaration(tokens));
        }
        return new Program(functions.toArray(new FunctionDeclaration[functions.size()]));
    }

    private FunctionDeclaration parseFunctionDeclaration(ConsumableIterator<Token> tokens) {
        consume(tokens, TokenType.KEYWORD_TYPE);
        Token identifier = tokens.pop();
        assertTokenType(identifier, TokenType.IDENTIFIER);
        consume(tokens, TokenType.OPEN_PAREN);
        List<String> parameters = new ArrayList<>();
        boolean comma = false;
        while (tokens.peek().getType() != TokenType.CLOSE_PAREN) {
            if (comma) {
                consume(tokens, TokenType.COMMA);
            }
            comma = true;
            consume(tokens, TokenType.KEYWORD_TYPE);
            Token parameter = tokens.pop();
            assertTokenType(parameter, TokenType.IDENTIFIER);
            parameters.add(parameter.getValue());
        }
        consume(tokens, TokenType.CLOSE_PAREN);
        Block block = parseBlock(tokens);
        return new FunctionDeclaration(identifier.getValue(), block, parameters.toArray(new String[parameters.size()]));
    }

    private Block parseBlock(ConsumableIterator<Token> tokens) {
        consume(tokens, TokenType.OPEN_BRACE);
        List<BlockItem> items = new ArrayList<>();
        while (tokens.peek().getType() != TokenType.CLOSE_BRACE) {
            items.add(parseBlockItem(tokens));
        }
        consume(tokens, TokenType.CLOSE_BRACE);
        return new Block(items.toArray(new BlockItem[items.size()]));
    }

    private BlockItem parseBlockItem(ConsumableIterator<Token> tokens) {
        TokenType type = tokens.peek().getType();
        if (type == TokenType.KEYWORD_TYPE) {
            consume(tokens, TokenType.KEYWORD_TYPE);
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
            return new VariableDeclaration(variable, expression);
        }
        return parseStatement(tokens);
    }

    private Statement parseStatement(ConsumableIterator<Token> tokens) {
        TokenType type = tokens.peek().getType();
        switch (type) {
            case IDENTIFIER: {
                String variable = tokens.pop().getValue();
                consume(tokens, TokenType.OP_ASSIGN);
                Expression expression = parseExpression(tokens);
                consume(tokens, TokenType.SEMICOLON);
                return new AssignStatement(variable, expression);
            }
            case KEYWORD_RETURN: {
                consume(tokens, TokenType.KEYWORD_RETURN);
                Expression expression = parseExpression(tokens);
                consume(tokens, TokenType.SEMICOLON);
                return new ReturnStatement(expression);
            }
            case KEYWORD_IF: {
                consume(tokens, TokenType.KEYWORD_IF, TokenType.OPEN_PAREN);
                Expression expression = parseExpression(tokens);
                consume(tokens, TokenType.CLOSE_PAREN);
                Statement ifStatement = parseStatement(tokens);
                Token token = tokens.peek();
                Statement elseStatement;
                if (token.getType() == TokenType.KEYWORD_ELSE) {
                    consume(tokens, TokenType.KEYWORD_ELSE);
                    elseStatement = parseStatement(tokens);
                } else {
                    elseStatement = null;
                }
                return new IfElseStatement(expression, ifStatement, elseStatement);
            }
            case OPEN_BRACE:
                return parseBlock(tokens);
            case KEYWORD_WHILE: {
                consume(tokens, TokenType.KEYWORD_WHILE, TokenType.OPEN_PAREN);
                Expression expression = parseExpression(tokens);
                consume(tokens, TokenType.CLOSE_PAREN);
                Statement body = parseStatement(tokens);
                return new WhileStatement(expression, body);
            }
            case KEYWORD_BREAK:
                consume(tokens, TokenType.KEYWORD_BREAK, TokenType.SEMICOLON);
                return new BreakStatement();
            case KEYWORD_CONTINUE:
                consume(tokens, TokenType.KEYWORD_CONTINUE, TokenType.SEMICOLON);
                return new BreakStatement();
            default: {
                Expression expression = parseExpression(tokens);
                consume(tokens, TokenType.SEMICOLON);
                return new ExpressionStatement(expression);
            }
        }
    }

    private Expression parseExpression(ConsumableIterator<Token> tokens) {
        return parseExpression(0, tokens);
    }

    private Expression parseExpression(int precedence, ConsumableIterator<Token> tokens) {
        if(precedence >= BINARY_OPERATOR_PRECEDENCE.length) {
            return parseFactor(tokens);
        }
        Expression a = parseExpression(precedence + 1, tokens);
        TokenType type = tokens.peek().getType();
        BinaryOperator operator = TOKEN_TO_BINARY_OPERATOR.get(type);
        if(Arrays.asList(BINARY_OPERATOR_PRECEDENCE[precedence]).contains(operator)) {
            consume(tokens, type);
            Expression b = parseExpression(precedence + 1, tokens);
            return new BinaryOperationExpression(a, operator, b);
        }
        return a;
    }

//    private Expression parseOr(ConsumableIterator<Token> tokens) {
//        Expression a = parseAnd(tokens);
//        Token token = tokens.peek();
//        BinaryOperator operator = null;
//        switch (token.getType()) {
//            case OP_OR:
//                tokens.pop();
//                operator = BinaryOperator.OR;
//                break;
//        }
//        if (operator != null) {
//            Expression b = parseAnd(tokens);
//            return new BinaryOperationExpression(a, operator, b);
//        }
//        return a;
//    }
//
//    private Expression parseAnd(ConsumableIterator<Token> tokens) {
//        Expression a = parseEquality(tokens);
//        Token token = tokens.peek();
//        BinaryOperator operator = null;
//        switch (token.getType()) {
//            case OP_AND:
//                tokens.pop();
//                operator = BinaryOperator.AND;
//                break;
//        }
//        if (operator != null) {
//            Expression b = parseEquality(tokens);
//            return new BinaryOperationExpression(a, operator, b);
//        }
//        return a;
//    }
//
//    private Expression parseEquality(ConsumableIterator<Token> tokens) {
//        Expression a = parseRelational(tokens);
//        Token token = tokens.peek();
//        BinaryOperator operator = null;
//        switch (token.getType()) {
//            case OP_EQUAL:
//                tokens.pop();
//                operator = BinaryOperator.EQUAL;
//                break;
//            case OP_NOTEQUAL:
//                tokens.pop();
//                operator = BinaryOperator.NOT_EQUAL;
//                break;
//        }
//        if (operator != null) {
//            Expression b = parseRelational(tokens);
//            return new BinaryOperationExpression(a, operator, b);
//        }
//        return a;
//    }
//
//    private Expression parseRelational(ConsumableIterator<Token> tokens) {
//        Expression a = parseAdditive(tokens);
//        Token token = tokens.peek();
//        BinaryOperator operator = null;
//        switch (token.getType()) {
//            case OP_GREATER:
//                tokens.pop();
//                operator = BinaryOperator.GREATER_THAN;
//                break;
//            case OP_GREATEROREQUAL:
//                tokens.pop();
//                operator = BinaryOperator.GREATER_OR_EQUAL;
//                break;
//            case OP_LESS:
//                tokens.pop();
//                operator = BinaryOperator.LESS_THAN;
//                break;
//            case OP_LESSOREQUAL:
//                tokens.pop();
//                operator = BinaryOperator.LESS_OR_EQUAL;
//                break;
//        }
//        if (operator != null) {
//            Expression b = parseAdditive(tokens);
//            return new BinaryOperationExpression(a, operator, b);
//        }
//        return a;
//    }
//
//    private Expression parseAdditive(ConsumableIterator<Token> tokens) {
//        Expression a = parseTerm(tokens);
//        Token token = tokens.peek();
//        BinaryOperator operator = null;
//        switch (token.getType()) {
//            case OP_PLUS:
//                tokens.pop();
//                operator = BinaryOperator.ADD;
//                break;
//            case OP_MINUS:
//                tokens.pop();
//                operator = BinaryOperator.SUB;
//                break;
//        }
//        if (operator != null) {
//            Expression b = parseTerm(tokens);
//            return new BinaryOperationExpression(a, operator, b);
//        }
//        return a;
//    }
//
//    private Expression parseTerm(ConsumableIterator<Token> tokens) {
//        Expression a = parseFactor(tokens);
//        Token token = tokens.peek();
//        BinaryOperator operator = null;
//        switch (token.getType()) {
//            case OP_MULTIPLY:
//                tokens.pop();
//                operator = BinaryOperator.MULT;
//                break;
//            case OP_DIVIDE:
//                tokens.pop();
//                operator = BinaryOperator.DIV;
//                break;
//            case OP_REMAINDER:
//                tokens.pop();
//                operator = BinaryOperator.REMAINDER;
//                break;
//        }
//        if (operator != null) {
//            Expression b = parseFactor(tokens);
//            return new BinaryOperationExpression(a, operator, b);
//        }
//        return a;
//    }

    private Expression parseFactor(ConsumableIterator<Token> tokens) {
        Token token = tokens.pop();
        switch (token.getType()) {
            case IDENTIFIER:
                if (tokens.peek().getType() == TokenType.OPEN_PAREN) {
                    consume(tokens, TokenType.OPEN_PAREN);
                    List<Expression> arguments = new ArrayList<>();
                    boolean comma = false;
                    while (tokens.peek().getType() != TokenType.CLOSE_PAREN) {
                        if (comma) {
                            consume(tokens, TokenType.COMMA);
                        }
                        comma = true;
                        arguments.add(parseExpression(tokens));
                    }
                    consume(tokens, TokenType.CLOSE_PAREN);
                    return new FunctionCallExpression(token.getValue(), arguments.toArray(new Expression[arguments.size()]));
                }
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