package com.github.fmjsjx.libcommons.util.collection;

import static com.github.fmjsjx.libcommons.util.collection.IntHashSet.PRESENT;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap.PrimitiveEntry;

public class LongHashSet implements LongSet {

    private final LongObjectHashMap<Object> map;

    public LongHashSet() {
        map = new LongObjectHashMap<>();
    }

    public LongHashSet(Collection<? extends Long> c) {
        this(Math.max((int) (c.size() / .75f) + 1, 16));
        addAll(c);
    }

    public LongHashSet(long... array) {
        this(Math.max((int) (array.length / .75f) + 1, 16));
        for (long v : array) {
            add(v);
        }
    }

    public LongHashSet(int initialCapacity) {
        map = new LongObjectHashMap<>(initialCapacity);
    }

    public LongHashSet(int initialCapacity, float loadFactor) {
        map = new LongObjectHashMap<>(initialCapacity, loadFactor);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public Iterator<Long> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return map.keySet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return map.keySet().toArray(a);
    }

    @Override
    public boolean add(Long e) {
        return map.put(e, PRESENT) == null;
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o) == PRESENT;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return map.keySet().containsAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return map.keySet().retainAll(c);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean contains(long v) {
        return map.containsKey(v);
    }

    @Override
    public boolean add(long v) {
        return map.put(v, PRESENT) == null;
    }

    @Override
    public boolean remove(long v) {
        return map.remove(v) == PRESENT;
    }

    @Override
    public LongStream longStream() {
        return StreamSupport.stream(map.entries().spliterator(), false).mapToLong(PrimitiveEntry::key);
    }

    @Override
    public void forEach(LongConsumer action) {
        for (var entry : map.entries()) {
            action.accept(entry.key());
        }
    }

    @Override
    public String toString() {
        return map.keySet().toString();
    }

}
