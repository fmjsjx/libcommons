package com.github.fmjsjx.libcommons.util.aliyunons;

public class RocketMQException extends Exception {

    private static final long serialVersionUID = 1L;

    public RocketMQException() {
        super();
    }

    public RocketMQException(String message, Throwable cause) {
        super(message, cause);
    }

    public RocketMQException(String message) {
        super(message);
    }

    public RocketMQException(Throwable cause) {
        super(cause);
    }

}
