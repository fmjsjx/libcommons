package com.github.fmjsjx.libcommons.spring.boot.autoconfigure;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnvironmentUtil implements EnvironmentAware {

    private static final EnvironmentUtil instance = new EnvironmentUtil();

    public static final EnvironmentUtil getInstance() {
        return instance;
    }

    private final AtomicReference<Environment> environmentRef = new AtomicReference<>();

    @Override
    public void setEnvironment(Environment environment) {
        environmentRef.set(environment);
    }

    public final Environment getEnvironment() {
        return environmentRef.get();
    }

}
