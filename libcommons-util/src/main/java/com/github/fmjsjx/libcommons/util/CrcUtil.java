package com.github.fmjsjx.libcommons.util;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.zip.CRC32;
import java.util.zip.CRC32C;
import java.util.zip.Checksum;

public class CrcUtil {

    public enum CheckType {
        /**
         * {@code "CRC-32"}
         */
        CRC_32(CRC32::new),
        /**
         * {@code "CRC-32C"}
         */
        CRC_32C(CRC32C::new);

        public static final CheckType forName(String name) {
            switch (name) {
            case "CRC-32":
                return CRC_32;
            case "CRC-32C":
                return CRC_32C;
            default:
                throw new NoSuchElementException("No such CheckType for name " + name);
            }
        }

        private final Supplier<Checksum> crcFactory;

        private CheckType(Supplier<Checksum> crcFactory) {
            this.crcFactory = crcFactory;
        }

    }

    public static final CrcUtil newInstance(CheckType checkType) {
        return new CrcUtil(checkType, checkType.crcFactory.get());
    }

    public static final CrcUtil crc32Instance() {
        return CRC32Holder.getInstance();
    }

    private static final class CRC32Holder {

        private static final ThreadLocalUtil threadLocalInstance = new ThreadLocalUtil(CheckType.CRC_32);

        private static final CrcUtil getInstance() {
            return threadLocalInstance.get();
        }

    }

    public static final CrcUtil crc32cInstance() {
        return CRC32CHolder.getInstance();
    }

    private static final class CRC32CHolder {

        private static final ThreadLocalUtil threadLocalInstance = new ThreadLocalUtil(CheckType.CRC_32C);

        private static final CrcUtil getInstance() {
            return threadLocalInstance.get();
        }

    }

    public static final long crc32(byte[] b, int off, int len) {
        return crc32Instance().checkValue(b, off, len);
    }

    public static final long crc32(byte[] b, byte[]... others) {
        return crc32Instance().checkValue(b, others);
    }

    public static final long crc32(ByteBuffer buffer, ByteBuffer... otherBuffers) {
        return crc32Instance().checkValue(buffer, otherBuffers);
    }

    public static final long crc32c(byte[] b, int off, int len) {
        return crc32cInstance().checkValue(b, off, len);
    }

    public static final long crc32c(byte[] b, byte[]... others) {
        return crc32cInstance().checkValue(b, others);
    }

    public static final long crc32c(ByteBuffer buffer, ByteBuffer... otherBuffers) {
        return crc32cInstance().checkValue(buffer, otherBuffers);
    }

    private static final class ThreadLocalUtil extends ThreadLocal<CrcUtil> {

        private final CheckType checkType;

        private ThreadLocalUtil(CheckType checkType) {
            this.checkType = checkType;
        }

        @Override
        protected CrcUtil initialValue() {
            return newInstance(checkType);
        }

    }

    private final CheckType type;
    private final Checksum crc;

    private CrcUtil(CheckType type, Checksum crc) {
        this.type = type;
        this.crc = crc;
    }

    public long checkValue(byte[] b, int off, int len) {
        try {
            crc.update(b, off, len);
            return crc.getValue();
        } finally {
            crc.reset();
        }
    }

    public long checkValue(byte[] b, byte[]... others) {
        try {
            crc.update(b);
            for (byte[] other : others) {
                crc.update(other);
            }
            return crc.getValue();
        } finally {
            crc.reset();
        }
    }

    public long checkValue(ByteBuffer buffer, ByteBuffer... otherBuffers) {
        try {
            crc.update(buffer);
            for (ByteBuffer other : otherBuffers) {
                crc.update(other);
            }
            return crc.getValue();
        } finally {
            crc.reset();
        }
    }

    @Override
    public String toString() {
        return "CrcUtil(type=" + type + ")";
    }

}
