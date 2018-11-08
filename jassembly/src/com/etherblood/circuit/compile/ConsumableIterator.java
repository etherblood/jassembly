package com.etherblood.circuit.compile;

import java.util.Iterator;

/**
 *
 * @author Philipp
 */
public class ConsumableIterator<T> {

    private final Iterator<T> iterator;
    private T current = null;

    public ConsumableIterator(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    public T consume() {
        try {
            return get();
        } finally {
            current = null;
        }
    }

    public T get() {
        if (current == null && iterator.hasNext()) {
            current = iterator.next();
        }
        return current;
    }
}
