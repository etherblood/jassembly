package com.etherblood.circuit.usability.codes.programs;


public class Noop implements CommandBlock {

    @Override
    public void toCommands(CommandConsumer consumer) {
        
    }

    @Override
    public int size() {
        return 0;
    }

}
