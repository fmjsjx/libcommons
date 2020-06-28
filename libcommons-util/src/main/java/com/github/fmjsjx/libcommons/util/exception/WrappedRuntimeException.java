package com.github.fmjsjx.libcommons.util.exception;

public class WrappedRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1803886065912825194L;

    public WrappedRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrappedRuntimeException(Throwable cause) {
        super(cause);
    }

    public Throwable wrappedCause() {
        return getCause();
    }

}
