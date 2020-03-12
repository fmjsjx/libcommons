package com.github.fmjsjx.libcommons.util.function.io;

import java.io.IOException;

@FunctionalInterface
public interface LongIoSupplier {

    long getAsLong() throws IOException;

}
