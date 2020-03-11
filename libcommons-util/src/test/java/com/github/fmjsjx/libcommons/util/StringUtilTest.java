package com.github.fmjsjx.libcommons.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    // TODO

}
