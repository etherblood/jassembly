package com.etherblood.circuit.compile;

import com.etherblood.circuit.compile.ast.FunctionDeclaration;
import com.etherblood.circuit.compile.ast.Program;
import com.etherblood.circuit.compile.ast.ReturnStatement;
import com.etherblood.circuit.compile.ast.expression.term.TermOperator;
import com.etherblood.circuit.compile.ast.expression.additive.AdditiveOperator;
import com.etherblood.circuit.compile.ast.expression.additive.BinaryAdditiveExpression;
import com.etherblood.circuit.compile.ast.expression.additive.SimpleAdditiveExpression;
import com.etherblood.circuit.compile.tokens.Token;
import com.etherblood.circuit.compile.tokens.TokenType;
import java.util.Iterator;
import com.etherblood.circuit.compile.ast.expression.additive.AdditiveExpression;
import com.etherblood.circuit.compile.ast.expression.and.AndExpression;
import com.etherblood.circuit.compile.ast.expression.and.AndOperator;
import com.etherblood.circuit.compile.ast.expression.and.BinaryAndExpression;
import com.etherblood.circuit.compile.ast.expression.and.SimpleAndExpression;
import com.etherblood.circuit.compile.ast.expression.equality.BinaryEqualityExpression;
import com.etherblood.circuit.compile.ast.expression.equality.EqualityExpression;
import com.etherblood.circuit.compile.ast.expression.equality.EqualityOperator;
import com.etherblood.circuit.compile.ast.expression.equality.SimpleEqualityExpression;
import com.etherblood.circuit.compile.ast.expression.factor.FactorExpression;
import com.etherblood.circuit.compile.ast.expression.factor.LiteralFactorExpression;
import com.etherblood.circuit.compile.ast.expression.factor.SimpleFactorExpression;
import com.etherblood.circuit.compile.ast.expression.factor.UnaryFactorExpression;
import com.etherblood.circuit.compile.ast.expression.factor.UnaryOperator;
import com.etherblood.circuit.compile.ast.expression.or.BinaryOrExpression;
import com.etherblood.circuit.compile.ast.expression.or.OrExpression;
import com.etherblood.circuit.compile.ast.expression.or.OrOperator;
import com.etherblood.circuit.compile.ast.expression.or.SimpleOrExpression;
import com.etherblood.circuit.compile.ast.expression.relational.BinaryRelationalExpression;
import com.etherblood.circuit.compile.ast.expression.relational.RelationalExpression;
import com.etherblood.circuit.compile.ast.expression.relational.RelationalOperator;
import com.etherblood.circuit.compile.ast.expression.relational.SimpleRelationalExpression;
import com.etherblood.circuit.compile.ast.expression.term.BinaryTermExpression;
import com.etherblood.circuit.compile.ast.expression.term.SimpleTermExpression;
import com.etherblood.circuit.compile.ast.expression.term.TermExpression;

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
        OrExpression expression = parseOr(tokens);
        consume(tokens, TokenType.SEMICOLON);
        return new ReturnStatement(expression);
    }

    private OrExpression parseOr(ConsumableIterator<Token> tokens) {
        AndExpression a = parseAnd(tokens);
        Token token = tokens.get();
        OrOperator operator = null;
        switch (token.getType()) {
            case OP_OR:
                tokens.consume();
                operator = OrOperator.OR;
                break;
        }
        if (operator != null) {
            AndExpression b = parseAnd(tokens);
            return new BinaryOrExpression(a, operator, b);
        }
        return new SimpleOrExpression(a);
    }

    private AndExpression parseAnd(ConsumableIterator<Token> tokens) {
        EqualityExpression a = parseEquality(tokens);
        Token token = tokens.get();
        AndOperator operator = null;
        switch (token.getType()) {
            case OP_AND:
                tokens.consume();
                operator = AndOperator.AND;
                break;
        }
        if (operator != null) {
            EqualityExpression b = parseEquality(tokens);
            return new BinaryAndExpression(a, operator, b);
        }
        return new SimpleAndExpression(a);
    }

    private EqualityExpression parseEquality(ConsumableIterator<Token> tokens) {
        RelationalExpression a = parseRelational(tokens);
        Token token = tokens.get();
        EqualityOperator operator = null;
        switch (token.getType()) {
            case OP_EQUAL:
                tokens.consume();
                operator = EqualityOperator.EQUAL;
                break;
            case OP_NOTEQUAL:
                tokens.consume();
                operator = EqualityOperator.NOT_EQUAL;
                break;
        }
        if (operator != null) {
            RelationalExpression b = parseRelational(tokens);
            return new BinaryEqualityExpression(a, operator, b);
        }
        return new SimpleEqualityExpression(a);
    }

    private RelationalExpression parseRelational(ConsumableIterator<Token> tokens) {
        AdditiveExpression a = parseAdditive(tokens);
        Token token = tokens.get();
        RelationalOperator operator = null;
        switch (token.getType()) {
            case OP_GREATER:
                tokens.consume();
                operator = RelationalOperator.GREATER;
                break;
            case OP_GREATEROREQUAL:
                tokens.consume();
                operator = RelationalOperator.GREATER_OR_EQUAL;
                break;
            case OP_LESS:
                tokens.consume();
                operator = RelationalOperator.LESS;
                break;
            case OP_LESSOREQUAL:
                tokens.consume();
                operator = RelationalOperator.LESS_OR_EQUAL;
                break;
        }
        if (operator != null) {
            AdditiveExpression b = parseAdditive(tokens);
            return new BinaryRelationalExpression(a, operator, b);
        }
        return new SimpleRelationalExpression(a);
    }

    private AdditiveExpression parseAdditive(ConsumableIterator<Token> tokens) {
        TermExpression a = parseTerm(tokens);
        Token token = tokens.get();
        AdditiveOperator operator = null;
        switch (token.getType()) {
            case OP_PLUS:
                tokens.consume();
                operator = AdditiveOperator.ADD;
                break;
            case OP_MINUS:
                tokens.consume();
                operator = AdditiveOperator.SUBTRACT;
                break;
        }
        if (operator != null) {
            TermExpression b = parseTerm(tokens);
            return new BinaryAdditiveExpression(a, operator, b);
        }
        return new SimpleAdditiveExpression(a);
    }

    private TermExpression parseTerm(ConsumableIterator<Token> tokens) {
        FactorExpression a = parseFactor(tokens);
        Token token = tokens.get();
        TermOperator operator = null;
        switch (token.getType()) {
            case OP_MULTIPLY:
                tokens.consume();
                operator = TermOperator.MULTIPLY;
                break;
            case OP_DIVIDE:
                tokens.consume();
                operator = TermOperator.DIVIDE;
                break;
            case OP_REMAINDER:
                tokens.consume();
                operator = TermOperator.REMAINDER;
                break;
        }
        if (operator != null) {
            FactorExpression b = parseFactor(tokens);
            return new BinaryTermExpression(a, operator, b);
        }
        return new SimpleTermExpression(a);
    }

    private FactorExpression parseFactor(ConsumableIterator<Token> tokens) {
        Token token = tokens.consume();
        switch (token.getType()) {
            case LITERAL_INT:
                return new LiteralFactorExpression(Integer.valueOf(token.getValue()));
            case OP_COMPLEMENT: {
                FactorExpression inner = parseFactor(tokens);
                return new UnaryFactorExpression(UnaryOperator.COMPLEMENT, inner);
            }
            case OP_MINUS: {
                FactorExpression inner = parseFactor(tokens);
                return new UnaryFactorExpression(UnaryOperator.NEGATE, inner);
            }
            case OPEN_PAREN:
                OrExpression expression = parseOr(tokens);
                consume(tokens, TokenType.CLOSE_PAREN);
                return new SimpleFactorExpression(expression);
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
