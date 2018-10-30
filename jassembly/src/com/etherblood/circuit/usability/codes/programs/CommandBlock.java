package com.etherblood.circuit.usability.codes.programs;

/**
 *
 * @author Philipp
 */
public interface CommandBlock {
    int size();
    void toCommands(CommandConsumer consumer);
}
