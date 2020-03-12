package com.github.fmjsjx.libcommons.util.function;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface IntBiFunction<R> extends BiFunction<Integer, Integer, R> {

    R apply(int left, int right);

    @Override
    default R apply(Integer t, Integer u) {
        return apply(t.intValue(), u.intValue());
    }

    @Override
    default <V> IntBiFunction<V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (left, right) -> after.apply(apply(left, right));
    }

}
