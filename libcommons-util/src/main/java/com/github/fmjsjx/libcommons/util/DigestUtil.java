package com.github.fmjsjx.libcommons.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.Objects;

import lombok.ToString;

@ToString
public class DigestUtil {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public enum DigestAlgorithm {
        /**
         * MD5
         */
        MD5("MD5"),
        /**
         * SHA-1
         */
        SHA1("SHA-1"),
        /**
         * SHA-256
         */
        SHA256("SHA-256");

        private final String algorithm;

        private DigestAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        public String algorithm() {
            return algorithm;
        }

        @Override
        public String toString() {
            return name() + "(" + algorithm + ")";
        }

    }

    public static final DigestUtil newInstance(DigestAlgorithm algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm.algorithm());
        return new DigestUtil(digest);
    }

    public static final DigestUtil newInstance(DigestAlgorithm algorithm, String provider)
            throws NoSuchAlgorithmException, NoSuchProviderException {
        MessageDigest digest = MessageDigest.getInstance(algorithm.algorithm(), provider);
        return new DigestUtil(digest);
    }

    public static final DigestUtil newInstance(DigestAlgorithm algorithm, Provider provider)
            throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm.algorithm(), provider);
        return new DigestUtil(digest);
    }

    public static final DigestUtil md5Instance() {
        return MD5Holder.getInstance();
    }

    public static final byte[] md5(String input) {
        return md5(input, DEFAULT_CHARSET);
    }

    public static final byte[] md5(String input, Charset charset) {
        return md5(input.getBytes(charset));
    }

    public static final byte[] md5(byte[] input, byte[]... otherInputs) {
        return md5Instance().digest(input, otherInputs);
    }

    public static final String md5AsHex(String input) {
        return md5AsHex(input, DEFAULT_CHARSET);
    }

    public static final String md5AsHex(String input, Charset charset) {
        return md5AsHex(input.getBytes(charset));
    }

    public static final String md5AsHex(byte[] input, byte[]... otherInputs) {
        return md5Instance().digestAsHex(input, otherInputs);
    }

    public static final DigestUtil sha1Instance() {
        return Sha1Holder.getInstance();
    }

    public static final byte[] sha1(String input) {
        return sha1(input, DEFAULT_CHARSET);
    }

    public static final byte[] sha1(String input, Charset charset) {
        return sha1(input.getBytes(charset));
    }

    public static final byte[] sha1(byte[] input, byte[]... otherInputs) {
        return sha1Instance().digest(input, otherInputs);
    }

    public static final String sha1AsHex(String input) {
        return sha1AsHex(input, DEFAULT_CHARSET);
    }

    public static final String sha1AsHex(String input, Charset charset) {
        return sha1AsHex(input.getBytes(charset));
    }

    public static final String sha1AsHex(byte[] input, byte[]... otherInputs) {
        return sha1Instance().digestAsHex(input, otherInputs);
    }

    public static final DigestUtil sha256Instance() {
        return Sha256Holder.getInstance();
    }

    public static final byte[] sha256(String input) {
        return sha256(input, DEFAULT_CHARSET);
    }

    public static final byte[] sha256(String input, Charset charset) {
        return sha256(input.getBytes(charset));
    }

    public static final byte[] sha256(byte[] input, byte[]... otherInputs) {
        return sha256Instance().digest(input, otherInputs);
    }

    public static final String sha256AsHex(String input) {
        return sha256AsHex(input, DEFAULT_CHARSET);
    }

    public static final String sha256AsHex(String input, Charset charset) {
        return sha256AsHex(input.getBytes(charset));
    }

    public static final String sha256AsHex(byte[] input, byte[]... otherInputs) {
        return sha256Instance().digestAsHex(input, otherInputs);
    }

    private static final class MD5Holder {

        private static final ThreadLocalUtil threadLocalInstance = new ThreadLocalUtil(DigestAlgorithm.MD5);

        private static final DigestUtil getInstance() {
            return threadLocalInstance.get();
        }

    }

    private static final class Sha1Holder {

        private static final ThreadLocalUtil threadLocalInstance = new ThreadLocalUtil(DigestAlgorithm.SHA1);

        private static final DigestUtil getInstance() {
            return threadLocalInstance.get();
        }

    }

    private static final class Sha256Holder {

        private static final ThreadLocalUtil threadLocalInstance = new ThreadLocalUtil(DigestAlgorithm.SHA256);

        private static final DigestUtil getInstance() {
            return threadLocalInstance.get();
        }

    }

    private static final class ThreadLocalUtil extends ThreadLocal<DigestUtil> {

        private final DigestAlgorithm algorithm;

        private ThreadLocalUtil(DigestAlgorithm algorithm) {
            this.algorithm = Objects.requireNonNull(algorithm, "algorithm must not be null");
        }

        @Override
        protected DigestUtil initialValue() {
            try {
                return newInstance(algorithm);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private final MessageDigest digest;

    private DigestUtil(MessageDigest digest) {
        this.digest = digest;
    }

    public byte[] digest(byte[] input, byte[]... otherInputs) {
        try {
            digest.update(input);
            for (byte[] otherInput : otherInputs) {
                digest.update(otherInput);
            }
            return digest.digest();
        } finally {
            digest.reset();
        }
    }

    public byte[] digest(ByteBuffer input, ByteBuffer... otherInputs) {
        try {
            digest.update(input);
            for (ByteBuffer otherInput : otherInputs) {
                digest.update(otherInput);
            }
            return digest.digest();
        } finally {
            digest.reset();
        }
    }

    public String digestAsHex(byte[] input, byte[]... otherInputs) {
        return StringUtil.toHexString(digest(input, otherInputs));
    }

    public String digestAsHex(ByteBuffer input, ByteBuffer... otherInputs) {
        return StringUtil.toHexString(digest(input, otherInputs));
    }

}
