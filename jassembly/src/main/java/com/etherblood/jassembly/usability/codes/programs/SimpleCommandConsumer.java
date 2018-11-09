package com.etherblood.jassembly.usability.codes.programs;

import java.util.ArrayList;
import java.util.List;

public class SimpleCommandConsumer implements CommandConsumer {

    private final List<Integer> list = new ArrayList<>();

    @Override
    public int line() {
        return list.size();
    }

    @Override
    public void add(int command) {
        list.add(command);
    }

    public List<Integer> getList() {
        return list;
    }

}
