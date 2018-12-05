package com.etherblood.jassembly.compile.jassembly.language.ast.expression;

/**
 *
 * @author Philipp
 */
public class BinaryOperatorUtil {

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

    public static BinaryOperator[] operatorsByPrecedence(int precedence) {
        if (precedence < BINARY_OPERATOR_PRECEDENCE.length) {
            return BINARY_OPERATOR_PRECEDENCE[precedence];
        }
        return new BinaryOperator[0];
    }

}
