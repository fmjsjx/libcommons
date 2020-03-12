package com.github.fmjsjx.libcommons.util.function.io;

import java.io.IOException;

@FunctionalInterface
public interface IntIoSupplier {

    int getAsInt() throws IOException;

}
