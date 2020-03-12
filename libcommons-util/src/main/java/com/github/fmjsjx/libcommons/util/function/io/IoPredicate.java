package com.github.fmjsjx.libcommons.util.function.io;

import java.io.IOException;
import java.util.Objects;

@FunctionalInterface
public interface IoPredicate<T> {

    boolean test(T t) throws IOException;

    default IoPredicate<T> and(IoPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) && other.test(t);
    }

    default IoPredicate<T> negate() {
        return (t) -> !test(t);
    }

    default IoPredicate<T> or(IoPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) || other.test(t);
    }

}
