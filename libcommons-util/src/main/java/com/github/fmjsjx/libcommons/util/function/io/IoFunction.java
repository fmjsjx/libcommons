package com.github.fmjsjx.libcommons.util.function.io;

import java.io.IOException;
import java.util.Objects;

@FunctionalInterface
public interface IoFunction<T, R> {

    R apply(T t) throws IOException;

    default <V> IoFunction<V, R> compose(IoFunction<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }

    default <V> IoFunction<T, V> andThen(IoFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }

    static <T> IoFunction<T, T> identity() {
        return t -> t;
    }

}
