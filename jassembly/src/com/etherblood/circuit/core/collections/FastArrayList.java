package com.etherblood.circuit.core.collections;

import java.util.Iterator;

/**
 *
 * @author Philipp
 */
public class FastArrayList<T> implements Iterable<T> {

    private final T[] data;
    private int count = 0;

    public FastArrayList(T[] data) {
        this.data = data;
    }

    public void add(T item) {
        data[count++] = item;
    }

    public void clear() {
        count = 0;
    }
    
    public int size() {
        return count;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int current;

            @Override
            public boolean hasNext() {
                return current < count;
            }

            @Override
            public T next() {
                return data[current++];
            }
        };
    }
}
