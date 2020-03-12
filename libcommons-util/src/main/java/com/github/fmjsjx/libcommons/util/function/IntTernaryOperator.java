package com.github.fmjsjx.libcommons.util.function;

@FunctionalInterface
public interface IntTernaryOperator {

    int applyAsInt(int first, int second, int third);

}
