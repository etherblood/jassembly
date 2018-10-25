package com.etherblood.circuit.core.collections;

import java.util.Iterator;
import java.util.function.ToIntFunction;

/**
 *
 * @author Philipp
 */
public class FastVersionedSet<T> implements Iterable<T> {

    private int version = -1;
    private final ToIntFunction<T> idGetter;
    private final int[] versions;
    private final FastArrayList<T> list;

    public FastVersionedSet(ToIntFunction<T> idGetter, T[] data) {
        this.idGetter = idGetter;
        this.versions = new int[data.length];
        this.list = new FastArrayList<>(data);
    }

    public void add(T item) {
        int id = idGetter.applyAsInt(item);
        if (versions[id] != version) {
            versions[id] = version;
            list.add(item);
        }
    }

    public void clear() {
        list.clear();
        version++;
    }
    
    public int size() {
        return list.size();
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
