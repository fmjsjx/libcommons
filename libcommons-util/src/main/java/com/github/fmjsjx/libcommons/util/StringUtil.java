package com.github.fmjsjx.libcommons.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtil {

    public static final boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static final boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    public static final boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static final boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    // TODO
    
}
