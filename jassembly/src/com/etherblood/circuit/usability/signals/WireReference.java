package com.etherblood.circuit.usability.signals;

import com.etherblood.circuit.core.Wire;

/**
 *
 * @author Philipp
 */
public interface WireReference {

    Wire getWire();

    void setWire(Wire wire);
}
