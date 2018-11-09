package com.etherblood.jassembly.compile;

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

    public T pop() {
        try {
            return peek();
        } finally {
            current = null;
        }
    }

    public T peek() {
        if (current == null && iterator.hasNext()) {
            current = iterator.next();
        }
        return current;
    }
}
