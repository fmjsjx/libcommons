package com.github.fmjsjx.libcommons.util.aliyunons;

import java.util.Collection;

public class TooManyRetryException extends RocketMQException {

    private static final long serialVersionUID = 1L;

    private final Iterable<Throwable> causes;
    private final int retryTimes;

    public TooManyRetryException(String message, Throwable cause, Collection<Throwable> causes) {
        super(message, cause);
        this.causes = causes;
        this.retryTimes = causes.size();
    }

    public TooManyRetryException(Throwable cause, Collection<Throwable> causes) {
        super(cause);
        this.causes = causes;
        this.retryTimes = causes.size();
    }

    public Iterable<Throwable> getCauses() {
        return causes;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    @Override
    public String getLocalizedMessage() {
        var msg = getMessage();
        return msg == null ? "retry times " + retryTimes : msg + ", retry times " + retryTimes;
    }

}
