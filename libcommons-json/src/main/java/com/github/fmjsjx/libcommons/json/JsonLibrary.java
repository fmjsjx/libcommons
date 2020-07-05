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

    /**
     * Decodes dynamic JSON object from byte array.
     * 
     * @param <T> the type of dynamic JSON object
     * @param src the source byte array
     * @return the dynamic JSON object
     * @throws JsonException if any JSON decode error occurs
     */
    <T extends JSON> T loads(byte[] src) throws JsonException;

    /**
     * Decodes dynamic JSON object from string.
     * 
     * @param <T> the type of dynamic JSON object
     * @param src the source string
     * @return the dynamic JSON object
     * @throws JsonException if any JSON decode error occurs
     */
    default <T extends JSON> T loads(String src) throws JsonException {
        return loads(toBytes(src));
    }

    /**
     * Decodes data from byte array.
     * 
     * @param <T>  the type of the data
     * @param src  the source byte array
     * @param type the class of the type
     * @return a data object as given type
     * @throws JsonException if any JSON decode error occurs
     */
    <T> T loads(byte[] src, Class<T> type) throws JsonException;

    /**
     * Decodes data from string.
     * 
     * @param <T>  the type of the data
     * @param src  the source string
     * @param type the class of the type
     * @return a data object as given type
     * @throws JsonException if any JSON decode error occurs
     */
    default <T> T loads(String src, Class<T> type) throws JsonException {
        return loads(toBytes(src), type);
    }

    /**
     * Decodes data from byte array.
     * 
     * @param <T>  the type of the data
     * @param src  the source byte array
     * @param type the type of the data
     * @return a data object as given type
     * @throws JsonException if any JSON decode error occurs
     */
    <T> T loads(byte[] src, Type type) throws JsonException;

    /**
     * Decodes data from string.
     * 
     * @param <T>  the type of the data
     * @param src  the source string
     * @param type the type of the data
     * @return a data object as given type
     * @throws JsonException if any JSON decode error occurs
     */
    default <T> T loads(String src, Type type) throws JsonException {
        return loads(toBytes(src), type);
    }

    /**
     * Encodes object to JSON value as byte array.
     * 
     * @param obj the object to be decoded
     * @return a {@code byte[]}
     * @throws JsonException if any JSON encode error occurs
     */
    byte[] dumpsToBytes(Object obj) throws JsonException;

    /**
     * Encodes object to JSON value as string.
     * 
     * @param obj the object to be decoded
     * @return a {@code String}
     * @throws JsonException if any JSON encode error occurs
     */
    String dumpsToString(Object obj) throws JsonException;

    /**
     * Encodes object to JSON value.
     * 
     * @param obj the object to be decoded
     * @param out the {@code OutputStream}
     * @throws JsonException if any JSON encode error occurs
     */
    void dumps(Object obj, OutputStream out) throws JsonException;

}
