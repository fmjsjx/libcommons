package com.github.fmjsjx.libcommons.util.exception;

public class WrappedRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1803886065912825194L;
    
    protected final Throwable wrappedCause;

    public WrappedRuntimeException(String message, Throwable cause) {
        super(message, cause);
        this.wrappedCause = cause;
    }

    public WrappedRuntimeException(Throwable cause) {
        super(cause);
        this.wrappedCause = cause;
    }

    public Throwable wrappedCause() {
        return wrappedCause;
    }
    
}
