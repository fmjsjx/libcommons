package com.github.fmjsjx.libcommons.json;

import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * A JSON encode/decode library.
 * 
 * @param <JSON> the type of dynamic JSON object
 * 
 * @since 1.0
 *
 * @author MJ Fang
 */
public interface JsonLibrary<JSON> {

    private static byte[] toBytes(String src) {
        return src.getBytes(StandardCharsets.UTF_8);
    }

    <T extends JSON> T loads(byte[] src) throws JsonException;

    default <T extends JSON> T loads(String src) throws JsonException {
        return loads(toBytes(src));
    }

    <T> T loads(byte[] src, Class<T> type) throws JsonException;

    default <T> T loads(String src, Class<T> type) throws JsonException {
        return loads(toBytes(src), type);
    }

    <T> T loads(byte[] src, Type type) throws JsonException;

    default <T> T loads(String src, Type type) throws JsonException {
        return loads(toBytes(src), type);
    }

    byte[] dumpsToBytes(Object obj) throws JsonException;

    String dumpsToString(Object obj) throws JsonException;

    void dumps(Object obj, OutputStream out) throws JsonException;

}
