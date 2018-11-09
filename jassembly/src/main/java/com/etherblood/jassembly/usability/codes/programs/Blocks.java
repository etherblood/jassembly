package com.etherblood.jassembly.usability.codes.programs;

import java.util.Arrays;

public class Blocks implements CommandBlock {

    private final CommandBlock[] blocks;

    public Blocks(CommandBlock... blocks) {
        this.blocks = blocks;
    }

    @Override
    public void toCommands(CommandConsumer consumer) {
        for (CommandBlock block : blocks) {
            block.toCommands(consumer);
        }
    }

    @Override
    public int size() {
        return Arrays.stream(blocks).mapToInt(CommandBlock::size).sum();
    }

}
