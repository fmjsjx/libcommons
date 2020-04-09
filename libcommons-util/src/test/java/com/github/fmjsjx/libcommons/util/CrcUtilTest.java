package com.github.fmjsjx.libcommons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.zip.CRC32;
import java.util.zip.CRC32C;

import org.junit.jupiter.api.Test;

public class CrcUtilTest {

    @Test
    public void testCrc32() {
        CrcUtil util = CrcUtil.crc32Instance();
        CRC32 crc32 = new CRC32();

        crc32.update("This is a test text string!!!".getBytes());
        assertEquals(crc32.getValue(), util.checkValue("This is a test text string!!!".getBytes()));

        try {
            util.checkValue((byte[]) null);
            fail("null");
        } catch (Exception e) {
            // OK
            assertEquals(crc32.getValue(), util.checkValue("This is a test text string!!!".getBytes()));
        }

        try {
            util.checkValue("ignored string".getBytes(), (byte[]) null, "ignored string".getBytes());
            fail("null");
        } catch (Exception e) {
            // OK
            assertEquals(crc32.getValue(), util.checkValue("This is a test text string!!!".getBytes()));
        }

        try {
            util.checkValue("ignored string".getBytes(), "ignored string".getBytes(), (byte[]) null);
            fail("null");
        } catch (Exception e) {
            // OK
            assertEquals(crc32.getValue(), util.checkValue("This is a test text string!!!".getBytes()));
        }
        crc32.update("This is a test text string!!!".getBytes());
        assertEquals(crc32.getValue(),
                CrcUtil.crc32("This is a test text string!!!".getBytes(), "This is a test text string!!!".getBytes()));

    }
    

    @Test
    public void testCrc32c() {
        CrcUtil util = CrcUtil.crc32cInstance();
        CRC32C crc32c = new CRC32C();

        crc32c.update("This is a test text string!!!".getBytes());
        assertEquals(crc32c.getValue(), util.checkValue("This is a test text string!!!".getBytes()));

        try {
            util.checkValue((byte[]) null);
            fail("null");
        } catch (Exception e) {
            // OK
            assertEquals(crc32c.getValue(), util.checkValue("This is a test text string!!!".getBytes()));
        }

        try {
            util.checkValue("ignored string".getBytes(), (byte[]) null, "ignored string".getBytes());
            fail("null");
        } catch (Exception e) {
            // OK
            assertEquals(crc32c.getValue(), util.checkValue("This is a test text string!!!".getBytes()));
        }

        try {
            util.checkValue("ignored string".getBytes(), "ignored string".getBytes(), (byte[]) null);
            fail("null");
        } catch (Exception e) {
            // OK
            assertEquals(crc32c.getValue(), util.checkValue("This is a test text string!!!".getBytes()));
        }
        crc32c.update("This is a test text string!!!".getBytes());
        assertEquals(crc32c.getValue(),
                CrcUtil.crc32c("This is a test text string!!!".getBytes(), "This is a test text string!!!".getBytes()));

    }

}
