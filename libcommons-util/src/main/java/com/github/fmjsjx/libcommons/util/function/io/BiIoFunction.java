package com.github.fmjsjx.libcommons.util.function.io;

import java.io.IOException;
import java.util.Objects;

@FunctionalInterface
public interface BiIoFunction<T, U, R> {

    R apply(T t, U u) throws IOException;

    default <V> BiIoFunction<T, U, V> andThen(IoFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t, U u) -> after.apply(apply(t, u));
    }

}
