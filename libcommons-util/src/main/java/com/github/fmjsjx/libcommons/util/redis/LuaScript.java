package com.github.fmjsjx.libcommons.util.redis;

import java.util.Objects;

import com.github.fmjsjx.libcommons.util.DigestUtil;

public class LuaScript {

    private final String script;
    private final String sha1;
    private final int numkeys;

    public LuaScript(String script, int numkeys) {
        this.script = Objects.requireNonNull(script, "script must not be null");
        this.sha1 = DigestUtil.sha1AsHex(script);
        this.numkeys = numkeys;
    }

    public LuaScript(String script) {
        this(script, -1);
    }

    public String script() {
        return script;
    }

    public String sha1() {
        return sha1;
    }

    public int numkeys() {
        return numkeys;
    }

    public boolean isDynamicNumkeys() {
        return numkeys < 0;
    }

    @Override
    public String toString() {
        return "LuaScript(numkeys=" + (isDynamicNumkeys() ? "DYNAMIC" : numkeys) + ", sha1=" + sha1 + ", script="
                + script + ")";
    }

}
