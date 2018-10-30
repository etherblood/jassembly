package com.etherblood.circuit.core.collections;

import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author Philipp
 */
public class FastArrayList<T> implements Iterable<T> {

    private T[] data;
    private int count = 0;

    @SuppressWarnings("unchecked")
    public FastArrayList() {
        this.data = (T[]) new Object[8];
    }

    public void add(T item) {
        if(count == data.length) {
            data = Arrays.copyOf(data, 2 * count);
        }
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
