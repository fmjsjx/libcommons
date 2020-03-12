package com.github.fmjsjx.libcommons.util.function.io;

import java.io.IOException;
import java.util.Objects;

@FunctionalInterface
public interface BiIoConsumer<T, U> {

    void accept(T t, U u) throws IOException;

    default BiIoConsumer<T, U> andThen(BiIoConsumer<? super T, ? super U> after) {
        Objects.requireNonNull(after);
        return (l, r) -> {
            accept(l, r);
            after.accept(l, r);
        };
    }

}
