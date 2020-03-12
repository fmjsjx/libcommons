package com.github.fmjsjx.libcommons.util.function.io;

import java.io.IOException;

@FunctionalInterface
public interface ToIntIoFunction<T> {

    int applyAsInt(T value) throws IOException;

}
