package com.github.fmjsjx.libcommons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class StringUtilTest {

    @Test
    public void testIsBlank() {
        assertTrue(StringUtil.isBlank(null));
        assertTrue(StringUtil.isBlank(""));
        assertTrue(StringUtil.isBlank(" "));
        assertTrue(StringUtil.isBlank("   "));
        assertFalse(StringUtil.isBlank("a"));
        assertFalse(StringUtil.isBlank(" a"));
        assertFalse(StringUtil.isBlank("a "));
        assertFalse(StringUtil.isBlank(" a "));
    }

    @Test
    public void testToHexString() {
        try {
            assertEquals(null, StringUtil.toHexString(null));
            assertEquals("", StringUtil.toHexString(new byte[0]));
            assertEquals("1f", StringUtil.toHexString(new byte[] { 0x1f }));

            assertEquals("1f2e3d4c5b6a7980", StringUtil.toHexString(new byte[] { 0x1f, 0x2e, 0x3d, 0x4c, 0x5b, 0x6a, 0x79, (byte) 0x80 }));

        } catch (Exception e) {
            fail(e);
        }
    }

}
