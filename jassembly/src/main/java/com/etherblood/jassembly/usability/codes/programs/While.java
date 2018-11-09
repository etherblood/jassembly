package com.etherblood.jassembly.usability.codes.programs;

import com.etherblood.jassembly.usability.codes.Command;

public class While implements CommandBlock {

    private final CommandBlock condition, body;
    private static final int EVAL_HEAD_SIZE = 10;
    private static final int JUMP_SIZE = 3;

    public While(CommandBlock condition, CommandBlock body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public int size() {
        return condition.size() + body.size() + EVAL_HEAD_SIZE + JUMP_SIZE;
    }

    @Override
    public void toCommands(CommandConsumer consumer) {
        int startLine = consumer.line();
        int bodyLine = startLine + condition.size() + EVAL_HEAD_SIZE;
        int endLine = bodyLine + body.size() + JUMP_SIZE;

        condition.toCommands(consumer);
        
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
        consumer.add(Command.LOAD_CONST.ordinal());
        consumer.add(startLine);
        consumer.add(Command.JUMP.ordinal());
        
        if (endLine != consumer.line()) {
            throw new IllegalStateException();
        }
    }

}
