package com.etherblood.jassembly.compile.ast.expression;

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

    public static ExpressionType resultType(ExpressionType a, BinaryOperator op, ExpressionType b) {
        switch (op) {
            case ADD:
            case SUB:
                if (a == ExpressionType.UINT && b == ExpressionType.UINT) {
                    return ExpressionType.UINT;
                }
                if (a == ExpressionType.SINT && b == ExpressionType.SINT) {
                    return ExpressionType.SINT;
                }
                break;
            case RSHIFT:
            case LSHIFT:
                if (a == ExpressionType.UINT && b == ExpressionType.UINT) {
                    return ExpressionType.UINT;
                }
                break;
            case AND:
            case OR:
            case XOR:
                if (a == ExpressionType.UINT && b == ExpressionType.UINT) {
                    return ExpressionType.UINT;
                }
                break;
            case EQUAL:
            case NOT_EQUAL:
                if (a == b) {
                    return ExpressionType.BOOL;
                }
                break;
            case LESS_THAN:
            case GREATER_THAN:
            case LESS_OR_EQUAL:
            case GREATER_OR_EQUAL:
                if (a == ExpressionType.UINT && b == ExpressionType.UINT) {
                    return ExpressionType.UINT;
                }
                if (a == ExpressionType.SINT && b == ExpressionType.SINT) {
                    return ExpressionType.SINT;
                }
                break;
            case MULT:
            case DIV:
            case REMAINDER:
                if (a == ExpressionType.UINT && b == ExpressionType.UINT) {
                    return ExpressionType.UINT;
                }
                if (a == ExpressionType.SINT && b == ExpressionType.SINT) {
                    return ExpressionType.SINT;
                }
                break;
            case LAZY_OR:
            case LAZY_AND:
                if (a == ExpressionType.BOOL && b == ExpressionType.BOOL) {
                    return ExpressionType.BOOL;
                }
                break;
            default:
                throw new AssertionError(op.name());

        }
        throw new UnsupportedOperationException(a.name() + " " + op.name() + " " + b.name());
    }
}
