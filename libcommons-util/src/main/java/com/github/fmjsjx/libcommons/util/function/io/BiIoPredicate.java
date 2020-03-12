package com.github.fmjsjx.libcommons.util.function.io;

import java.io.IOException;
import java.util.Objects;

@FunctionalInterface
public interface BiIoPredicate<T, U> {

    boolean test(T t, U u) throws IOException;

    default BiIoPredicate<T, U> and(BiIoPredicate<? super T, ? super U> other) {
        Objects.requireNonNull(other);
        return (T t, U u) -> test(t, u) && other.test(t, u);
    }

    default BiIoPredicate<T, U> negate() {
        return (T t, U u) -> !test(t, u);
    }

    default BiIoPredicate<T, U> or(BiIoPredicate<? super T, ? super U> other) {
        Objects.requireNonNull(other);
        return (T t, U u) -> test(t, u) || other.test(t, u);
    }

}
