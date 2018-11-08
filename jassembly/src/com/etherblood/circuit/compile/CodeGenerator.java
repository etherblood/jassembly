package com.etherblood.circuit.compile;

import com.etherblood.circuit.compile.ast.FunctionDeclaration;
import com.etherblood.circuit.compile.ast.Program;
import com.etherblood.circuit.compile.ast.ReturnStatement;
import com.etherblood.circuit.compile.ast.expression.additive.BinaryAdditiveExpression;
import com.etherblood.circuit.compile.ast.expression.additive.SimpleAdditiveExpression;
import com.etherblood.circuit.compile.jassembly.Jassembly;
import com.etherblood.circuit.compile.ast.expression.additive.AdditiveExpression;
import com.etherblood.circuit.compile.ast.expression.and.AndExpression;
import com.etherblood.circuit.compile.ast.expression.and.BinaryAndExpression;
import com.etherblood.circuit.compile.ast.expression.and.SimpleAndExpression;
import com.etherblood.circuit.compile.ast.expression.equality.BinaryEqualityExpression;
import com.etherblood.circuit.compile.ast.expression.equality.EqualityExpression;
import com.etherblood.circuit.compile.ast.expression.equality.SimpleEqualityExpression;
import com.etherblood.circuit.compile.ast.expression.factor.FactorExpression;
import com.etherblood.circuit.compile.ast.expression.factor.LiteralFactorExpression;
import com.etherblood.circuit.compile.ast.expression.factor.SimpleFactorExpression;
import com.etherblood.circuit.compile.ast.expression.factor.UnaryFactorExpression;
import com.etherblood.circuit.compile.ast.expression.or.BinaryOrExpression;
import com.etherblood.circuit.compile.ast.expression.or.OrExpression;
import com.etherblood.circuit.compile.ast.expression.or.SimpleOrExpression;
import com.etherblood.circuit.compile.ast.expression.relational.BinaryRelationalExpression;
import com.etherblood.circuit.compile.ast.expression.relational.RelationalExpression;
import com.etherblood.circuit.compile.ast.expression.relational.SimpleRelationalExpression;
import com.etherblood.circuit.compile.ast.expression.term.BinaryTermExpression;
import com.etherblood.circuit.compile.ast.expression.term.SimpleTermExpression;
import com.etherblood.circuit.compile.ast.expression.term.TermExpression;

/**
 *
 * @author Philipp
 */
public class CodeGenerator {

    public void generateCode(Program program, Jassembly jassembly) {
        function(program.getFunction(), jassembly);
        jassembly.terminate();
    }

    private void function(FunctionDeclaration function, Jassembly jassembly) {
        returnStatement(function.getStatement(), jassembly);
    }

    private void returnStatement(ReturnStatement statement, Jassembly jassembly) {
        or(statement.getExpression(), jassembly);
    }

    private void or(OrExpression expression, Jassembly jassembly) {
        if (expression instanceof SimpleOrExpression) {
            and(((SimpleOrExpression) expression).getTerm(), jassembly);
            return;
        }
        if (expression instanceof BinaryOrExpression) {
            BinaryOrExpression binary = (BinaryOrExpression) expression;
            //TODO
        }
        throw new UnsupportedOperationException();
    }

    private void and(AndExpression expression, Jassembly jassembly) {
        if (expression instanceof SimpleAndExpression) {
            equality(((SimpleAndExpression) expression).getTerm(), jassembly);
            return;
        }
        if (expression instanceof BinaryAndExpression) {
            BinaryAndExpression binary = (BinaryAndExpression) expression;
            //TODO
        }
        throw new UnsupportedOperationException();
    }

    private void equality(EqualityExpression expression, Jassembly jassembly) {
        if (expression instanceof SimpleEqualityExpression) {
            relational(((SimpleEqualityExpression) expression).getTerm(), jassembly);
            return;
        }
        if (expression instanceof BinaryEqualityExpression) {
            BinaryEqualityExpression binary = (BinaryEqualityExpression) expression;
            relational(binary.getA(), jassembly);
            jassembly.pushStack();
            relational(binary.getB(), jassembly);
            jassembly.toX0();
            jassembly.popStack();
            switch (binary.getOperator()) {
                case EQUAL:
                    jassembly.xor();
                    jassembly.any();
                    jassembly.complement();
                    break;
                case NOT_EQUAL:
                    jassembly.xor();
                    jassembly.any();
                    break;
                default:
                    throw new AssertionError(binary.getOperator().name());
            }
            return;
        }
        throw new UnsupportedOperationException();
    }

    private void relational(RelationalExpression expression, Jassembly jassembly) {
        if (expression instanceof SimpleRelationalExpression) {
            additive(((SimpleRelationalExpression) expression).getTerm(), jassembly);
            return;
        }
        if (expression instanceof BinaryRelationalExpression) {
            BinaryRelationalExpression binary = (BinaryRelationalExpression) expression;
            additive(binary.getA(), jassembly);
            jassembly.pushStack();
            additive(binary.getB(), jassembly);
            jassembly.toX0();
            jassembly.popStack();
            //TODO
        }
        throw new UnsupportedOperationException();
    }

    private void additive(AdditiveExpression expression, Jassembly jassembly) {
        if (expression instanceof SimpleAdditiveExpression) {
            term(((SimpleAdditiveExpression) expression).getTerm(), jassembly);
            return;
        }
        if (expression instanceof BinaryAdditiveExpression) {
            BinaryAdditiveExpression binary = (BinaryAdditiveExpression) expression;
            term(binary.getA(), jassembly);
            jassembly.pushStack();
            term(binary.getB(), jassembly);
            jassembly.toX0();
            jassembly.popStack();
            switch (binary.getOperator()) {
                case ADD:
                    jassembly.add();
                    break;
                case SUBTRACT:
                    jassembly.sub();
                    break;
                default:
                    throw new AssertionError(binary.getOperator().name());
            }
            return;
        }
        throw new UnsupportedOperationException();
    }

    private void term(TermExpression expression, Jassembly jassembly) {
        if (expression instanceof SimpleTermExpression) {
            factor(((SimpleTermExpression) expression).getFactor(), jassembly);
            return;
        }
        if (expression instanceof BinaryTermExpression) {
            BinaryTermExpression binary = (BinaryTermExpression) expression;
            //TODO
        }
        throw new UnsupportedOperationException();
    }

    private void factor(FactorExpression factor, Jassembly jassembly) {
        if (factor instanceof LiteralFactorExpression) {
            literalFactor((LiteralFactorExpression) factor, jassembly);
            return;
        }
        if (factor instanceof SimpleFactorExpression) {
            expressionFactor((SimpleFactorExpression) factor, jassembly);
            return;
        }
        if (factor instanceof UnaryFactorExpression) {
            unaryFactor((UnaryFactorExpression) factor, jassembly);
            return;
        }
        throw new UnsupportedOperationException();
    }

    private void unaryFactor(UnaryFactorExpression factor, Jassembly jassembly) {
        factor(factor.getFactor(), jassembly);
        switch (factor.getUnaryOperator()) {
            case COMPLEMENT:
                jassembly.complement();
                break;
            case NEGATE:
                jassembly.negate();
                break;
            default:
                throw new AssertionError(factor);
        }
    }

    private void expressionFactor(SimpleFactorExpression factor, Jassembly jassembly) {
        or(factor.getExpression(), jassembly);
    }

    private void literalFactor(LiteralFactorExpression factor, Jassembly jassembly) {
        jassembly.constant(factor.getLiteral());
    }
}
