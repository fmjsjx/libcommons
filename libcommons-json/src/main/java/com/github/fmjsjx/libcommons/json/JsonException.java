package com.github.fmjsjx.libcommons.json;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class JsonException extends RuntimeException implements Supplier<Throwable> {

    private static final long serialVersionUID = 5604421476181087459L;

    protected JsonException(String message, Throwable cause) {
        super(message, Objects.requireNonNull(cause, "cause must not be null"));
    }

    protected JsonException(Throwable cause) {
        super(Objects.requireNonNull(cause, "cause must not be null"));
    }

    @Override
    public Throwable get() {
        return getCause();
    }

}
