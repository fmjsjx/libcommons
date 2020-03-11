package com.github.fmjsjx.libcommons.json;

import java.io.OutputStream;
import java.lang.reflect.Type;

import com.jsoniter.any.Any;

public class JsoniterLibrary implements JsonLibrary<Any> {

    @Override
    public <T extends Any> T loads(byte[] src) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T loads(byte[] src, Class<T> type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T loads(byte[] src, Type type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] dumpsToBytes(Object obj) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String dumpsToString(Object obj) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void dumps(Object obj, OutputStream out) {
        // TODO Auto-generated method stub
        
    }

}
