package com.github.fmjsjx.libcommons.util.collection;

import java.util.Collection;
import java.util.Set;

import com.github.fmjsjx.libcommons.util.function.ByteConsumer;

public interface ByteSet extends Set<Byte> {

	boolean contains(byte v);

    boolean add(byte v);

    boolean remove(byte v);

    byte[] toByteArray();
    
    void forEach(ByteConsumer action);
    
    @Override
    default boolean addAll(Collection<? extends Byte> c) {
        var r = false;
        for (Byte e : c) {
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
