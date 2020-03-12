package com.github.fmjsjx.libcommons.util.function.io;

import java.io.IOException;

public interface ToIntBiIoFunction<T, U> {

    int applyAsInt(T t, U u) throws IOException;

}
