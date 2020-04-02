package com.github.fmjsjx.libcommons.util;

import java.util.Optional;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassUtil {

    @SuppressWarnings("unchecked")
    public static final <T> Optional<Class<T>> findForName(String className) {
        try {
            return Optional.of((Class<T>) Class.forName(className));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public static final boolean hasClassForName(String className) {
        return findForName(className).isPresent();
    }

}
