package com.etherblood.jassembly.usability.modules.wires;

/**
 *
 * @author Philipp
 */
public interface InputReference {

    void connectTo(OutputReference source);

    void cascade();

}
