package com.github.fmjsjx.libcommons.json;

import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public interface JsonLibrary<JSON> {

    private static byte[] toBytes(String src) {
        return src.getBytes(StandardCharsets.UTF_8);
    }

    <T extends JSON> T loads(byte[] src);

    default <T extends JSON> T loads(String src) {
        return loads(toBytes(src));
    }

    <T> T loads(byte[] src, Class<T> type);

    default <T> T loads(String src, Class<T> type) {
        return loads(toBytes(src), type);
    }

    <T> T loads(byte[] src, Type type);

    default <T> T loads(String src, Type type) {
        return loads(toBytes(src), type);
    }

    byte[] dumpsToBytes(Object obj);

    String dumpsToString(Object obj);

    void dumps(Object obj, OutputStream out);

}
