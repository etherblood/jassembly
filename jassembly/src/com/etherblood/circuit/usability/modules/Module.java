package com.etherblood.circuit.usability.modules;

import com.etherblood.circuit.core.Wire;

/**
 *
 * @author Philipp
 */
public interface Module {

    Wire[] inputs();
    Wire[] outputs();
}
