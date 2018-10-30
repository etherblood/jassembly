package com.etherblood.circuit.usability.codes.programs;

/**
 *
 * @author Philipp
 */
public interface CommandConsumer {

    int line();

    void add(int command);
}
