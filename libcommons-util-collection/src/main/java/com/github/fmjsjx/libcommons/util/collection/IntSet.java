package com.github.fmjsjx.libcommons.util.collection;

import java.util.Collection;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

public interface IntSet extends Set<Integer> {

    boolean contains(int v);

    boolean add(int v);

    boolean remove(int v);

    IntStream intStream();

    default int[] toIntArray() {
        return intStream().toArray();
    }

    void forEach(IntConsumer action);

    @Override
    default boolean addAll(Collection<? extends Integer> c) {
        var r = false;
        for (Integer e : c) {
            r = add(e) || r;
        }
        return r;
    }

    @Override
    default boolean removeAll(Collection<?> c) {
        var r = false;
        for (Object e : c) {
            r = remove(e) || r;
        }
        return r;
    }

}
