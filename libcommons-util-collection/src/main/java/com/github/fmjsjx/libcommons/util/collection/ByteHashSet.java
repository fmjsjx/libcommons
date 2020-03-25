package com.github.fmjsjx.libcommons.util.collection;

import static com.github.fmjsjx.libcommons.util.collection.IntHashSet.PRESENT;
import java.util.Collection;
import java.util.Iterator;

import com.github.fmjsjx.libcommons.util.function.ByteConsumer;

import io.netty.util.collection.ByteObjectHashMap;
import io.netty.util.collection.ByteObjectMap.PrimitiveEntry;

public class ByteHashSet implements ByteSet {

    private final ByteObjectHashMap<Object> map;

    public ByteHashSet() {
        map = new ByteObjectHashMap<Object>();
    }

    public ByteHashSet(int initialCapacity) {
        map = new ByteObjectHashMap<>(initialCapacity);
    }

    public ByteHashSet(int initialCapacity, float loadFactor) {
        map = new ByteObjectHashMap<>(initialCapacity, loadFactor);
    }

    public ByteHashSet(byte... array) {
        this(Math.max((int) (array.length / .75f) + 1, 16));
        for (byte v : array) {
            add(v);
        }
    }

    public ByteHashSet(Collection<? extends Byte> c) {
        this(Math.max((int) (c.size() / .75f) + 1, 16));
        addAll(c);
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
    public Iterator<Byte> iterator() {
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
    public boolean add(Byte e) {
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
    public boolean contains(byte v) {
        return map.containsKey(v);
    }

    @Override
    public boolean add(byte v) {
        return map.put(v, PRESENT) == null;
    }

    @Override
    public boolean remove(byte v) {
        return map.remove(v) == PRESENT;
    }

    @Override
    public byte[] toByteArray() {
        byte[] b = new byte[size()];
        int i = 0;
        for (Byte v : map.keySet()) {
            b[i] = v.byteValue();
            i++;
        }
        return b;
    }

    @Override
    public void forEach(ByteConsumer action) {
        for (PrimitiveEntry<Object> entry : map.entries()) {
            action.accept(entry.key());
        }
    }

}
