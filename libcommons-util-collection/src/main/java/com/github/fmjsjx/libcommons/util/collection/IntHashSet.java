package com.github.fmjsjx.libcommons.util.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap.PrimitiveEntry;

public class IntHashSet implements IntSet {

    static final Object PRESENT = new Object();

    private final IntObjectHashMap<Object> map;

    public IntHashSet() {
        map = new IntObjectHashMap<>();
    }

    public IntHashSet(Collection<? extends Integer> c) {
        this(Math.max((int) (c.size() / .75f) + 1, 16));
        addAll(c);
    }

    public IntHashSet(int... array) {
        this(Math.max((int) (array.length / .75f) + 1, 16));
        for (int v : array) {
            add(v);
        }
    }

    public IntHashSet(int initialCapacity) {
        map = new IntObjectHashMap<>(initialCapacity);
    }

    public IntHashSet(int initialCapacity, float loadFactor) {
        map = new IntObjectHashMap<>(initialCapacity, loadFactor);
    }

    @Override
    public boolean contains(int v) {
        return map.containsKey(v);
    }

    @Override
    public boolean add(int v) {
        return map.put(v, PRESENT) == null;
    }

    @Override
    public boolean remove(int v) {
        return map.remove(v) == PRESENT;
    }

    @Override
    public IntStream intStream() {
        return StreamSupport.stream(map.entries().spliterator(), false).mapToInt(PrimitiveEntry::key);
    }

    @Override
    public void forEach(IntConsumer action) {
        for (var entry : map.entries()) {
            action.accept(entry.key());
        }
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
    public Iterator<Integer> iterator() {
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
    public boolean add(Integer e) {
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
    public String toString() {
        return map.keySet().toString();
    }

}
