package com.etherblood.jassembly.usability.codes.programs;

import com.etherblood.jassembly.usability.codes.Command;

public class If implements CommandBlock {

    private final CommandBlock condition, body;
    private static final int EVAL_HEAD_SIZE = 10;

    public If(CommandBlock condition, CommandBlock body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public int size() {
        return condition.size() + body.size() + EVAL_HEAD_SIZE;
    }

    @Override
    public void toCommands(CommandConsumer consumer) {
        condition.toCommands(consumer);
        int bodyLine = consumer.line() + EVAL_HEAD_SIZE;
        int endLine = bodyLine + body.size();

        consumer.add(Command.ANY.ordinal());
        consumer.add(Command.TO_X0.ordinal());
        consumer.add(Command.LOAD_CONST.ordinal());
        consumer.add(bodyLine ^ endLine);
        consumer.add(Command.AND.ordinal());
        consumer.add(Command.TO_X0.ordinal());
        consumer.add(Command.LOAD_CONST.ordinal());
        consumer.add(endLine);
        consumer.add(Command.XOR.ordinal());
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
