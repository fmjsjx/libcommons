package com.github.fmjsjx.libcommons.util.collection;

import java.util.Collection;
import java.util.Set;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;

public interface LongSet extends Set<Long> {

    boolean contains(long v);

    boolean add(long v);

    boolean remove(long v);

    LongStream longStream();

    default long[] toLongArray() {
        return longStream().toArray();
    }

    void forEach(LongConsumer action);

    @Override
    default boolean addAll(Collection<? extends Long> c) {
        var r = false;
        for (Long e : c) {
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
