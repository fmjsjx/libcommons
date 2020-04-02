package com.github.fmjsjx.libcommons.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberUtil {

    public static final int intValue(Number number, int defaultValue) {
        if (number == null) {
            return defaultValue;
        }
        return number.intValue();
    }

    public static final int intValue(String value, int defaultValue) {
        if (StringUtil.isBlank(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    public static final long longValue(Number number, long defaultValue) {
        if (number == null) {
            return defaultValue;
        }
        return number.longValue();
    }

    public static final long longValue(String value, long defaultValue) {
        if (StringUtil.isBlank(value)) {
            return defaultValue;
        }
        return Long.parseLong(value);
    }

    public static final float floatValue(Number number, float defaultValue) {
        if (number == null) {
            return defaultValue;
        }
        return number.floatValue();
    }

    public static final float floatValue(String value, float defaultValue) {
        if (StringUtil.isBlank(value)) {
            return defaultValue;
        }
        return Float.parseFloat(value);
    }

    public static final double doubleValue(Number number, double defaultValue) {
        if (number == null) {
            return defaultValue;
        }
        return number.doubleValue();
    }

    public static final double doubleValue(String value, double defaultValue) {
        if (StringUtil.isBlank(value)) {
            return defaultValue;
        }
        return Double.parseDouble(value);
    }

}
