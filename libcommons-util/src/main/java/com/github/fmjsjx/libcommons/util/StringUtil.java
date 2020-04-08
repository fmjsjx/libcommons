package com.github.fmjsjx.libcommons.util;

import java.nio.charset.StandardCharsets;

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

    public static final boolean isNumberic(String value) {
        if (isBlank(value)) {
            return false;
        }
        char[] chars = value.toCharArray();
        for (int i = chars.length - 1; i >= 0; i--) {
            if (chars[i] < '0' || chars[i] > '9') {
                return false;
            }
        }
        return true;
    }

    public static final String[] split(String value, String regex, int limit) {
        if (value == null) {
            return null;
        }
        return value.split(regex, limit);
    }

    public static final String[] split(String value, String regex) {
        return split(value, regex, 0);
    }

    public static final int[] splitInt(String value, String regex) {
        String[] strings = value.split(regex);
        int[] values = new int[strings.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = Integer.parseInt(strings[i]);
        }
        return values;
    }

    public static final long[] splitLong(String value, String regex) {
        String[] strings = value.split(regex);
        long[] values = new long[strings.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = Long.parseLong(strings[i]);
        }
        return values;
    }

    public static final boolean toBoolean(String value) {
        if (value == null) {
            return false;
        }
        switch (value) {
        case "true":
            return true;
        case "TRUE":
            return true;
        case "1":
            return true;
        default:
            return false;
        }
    }

    public static final String toHexString(byte[] value) {
        if (value == null) {
            return null;
        }
        if (value.length == 0) {
            return "";
        }
        return toHexString0(value);
    }

    private static final String toHexString0(byte[] value) {
        final byte[] hexBytes = HexBytesHolder.HEX_BYTES;
        byte[] hexValue = new byte[value.length << 1];
        for (int i = 0; i < value.length; i++) {
            byte b = value[i];
            int index = i * 2;
            hexValue[index] = hexBytes[(b >>> 0x4) & 0xf];
            hexValue[index + 1] = hexBytes[b & 0xf];
        }
        return new String(hexValue, StandardCharsets.US_ASCII);
    }

    private static final class HexBytesHolder {
        private static final byte[] HEX_BYTES = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f' };
    }

}
