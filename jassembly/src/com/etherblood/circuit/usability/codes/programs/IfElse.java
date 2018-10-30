package com.etherblood.circuit.usability.codes.programs;

import com.etherblood.circuit.usability.codes.Command;

public class IfElse implements CommandBlock {

    private final CommandBlock condition, body, otherwise;
    private static final int EVAL_HEAD_SIZE = 10;
    private static final int JUMP_SIZE = 3;

    public IfElse(CommandBlock condition, CommandBlock body, CommandBlock otherwise) {
        this.condition = condition;
        this.body = body;
        this.otherwise = otherwise;
    }

    @Override
    public int size() {
        return condition.size() + body.size() + otherwise.size() + EVAL_HEAD_SIZE + JUMP_SIZE;
    }

    @Override
    public void toCommands(CommandConsumer consumer) {
        condition.toCommands(consumer);
        int elseLine = consumer.line() + EVAL_HEAD_SIZE;
        int bodyLine = elseLine + otherwise.size() + JUMP_SIZE;
        int endLine = bodyLine + body.size();

        consumer.add(Command.ANY.ordinal());
        consumer.add(Command.TO_X0.ordinal());
        consumer.add(Command.LOAD_CONST.ordinal());
        consumer.add(bodyLine ^ elseLine);
        consumer.add(Command.AND.ordinal());
        consumer.add(Command.TO_X0.ordinal());
        consumer.add(Command.LOAD_CONST.ordinal());
        consumer.add(elseLine);
        consumer.add(Command.XOR.ordinal());
        consumer.add(Command.JUMP.ordinal());

        if (elseLine != consumer.line()) {
            throw new IllegalStateException();
        }
        otherwise.toCommands(consumer);
        consumer.add(Command.LOAD_CONST.ordinal());
        consumer.add(endLine);
        consumer.add(Command.JUMP.ordinal());

        if (bodyLine != consumer.line()) {
            throw new IllegalStateException();
        }
        body.toCommands(consumer);
        if (endLine != consumer.line()) {
            throw new IllegalStateException();
        }
    }

}
