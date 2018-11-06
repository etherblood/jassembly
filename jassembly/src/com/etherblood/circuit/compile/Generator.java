package com.etherblood.circuit.compile;

import com.etherblood.circuit.compile.ast.Constant;
import com.etherblood.circuit.compile.ast.FunctionDeclaration;
import com.etherblood.circuit.compile.ast.Program;
import com.etherblood.circuit.compile.ast.ReturnStatement;
import com.etherblood.circuit.usability.codes.Command;
import com.etherblood.circuit.usability.codes.programs.CommandConsumer;

/**
 *
 * @author Philipp
 */
public class Generator {

    public void generateCode(Program program, CommandConsumer consumer) {
        generateCode(program.getFunction(), consumer);
        consumer.add(Command.TERMINATE.ordinal());
    }

    public void generateCode(FunctionDeclaration function, CommandConsumer consumer) {
        generateCode(function.getStatement(), consumer);
    }

    public void generateCode(ReturnStatement statement, CommandConsumer consumer) {
        generateCode(statement.getConstant(), consumer);
    }

    public void generateCode(Constant constant, CommandConsumer consumer) {
        consumer.add(Command.LOAD_CONST.ordinal());
        consumer.add(constant.getValue());
    }
}
