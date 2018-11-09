package com.etherblood.jassembly.usability.codes.programs;

/**
 *
 * @author Philipp
 */
public interface CommandBlock {
    int size();
    void toCommands(CommandConsumer consumer);
}
