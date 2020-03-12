package com.github.fmjsjx.libcommons.util.function.io;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface IoConsumer<T> {

    void accept(T t) throws IOException;

    default IoConsumer<T> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> {
            accept(t);
            after.accept(t);
        };
    }

}
