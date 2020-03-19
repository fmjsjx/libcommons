package com.github.fmjsjx.libcommons.json;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;
import com.dslplatform.json.runtime.TypeDefinition;

public class DsljsonLibrary implements JsonLibrary<Map<String, ?>> {

    public static final class DsljsonException extends JsonException {

        private static final long serialVersionUID = 5206247465614432014L;

        public DsljsonException(String message, Throwable cause) {
            super(message, cause);
        }

        public DsljsonException(Throwable cause) {
            super(cause);
        }

    }

    private static final class InstanceHolder {
        private static final DsljsonLibrary instance = new DsljsonLibrary();
    }

    public static final DsljsonLibrary getInstance() {
        return InstanceHolder.instance;
    }

    private static final Type mapType = ((ParameterizedType) DsljsonLibrary.class.getGenericInterfaces()[0])
            .getActualTypeArguments()[0];

    private final DslJson<Object> dslJson;

    public DsljsonLibrary() {
        this(defaultDslJson());
    }

    private static final DslJson<Object> defaultDslJson() {
        var json = new DslJson<Object>(Settings.basicSetup());
        return json;
    }

    public DsljsonLibrary(DslJson<Object> dslJson) {
        this.dslJson = dslJson;
    }

    public DslJson<Object> dslJson() {
        return dslJson;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Map<String, ?>> T loads(byte[] src) throws DsljsonException {
        try {
            return (T) dslJson.deserialize(mapType, src, src.length);
        } catch (IOException e) {
            throw new DsljsonException(e);
        }
    }

    @Override
    public <T> T loads(byte[] src, Class<T> type) throws DsljsonException {
        try {
            return dslJson.deserialize(type, src, src.length);
        } catch (IOException e) {
            throw new DsljsonException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T loads(byte[] src, Type type) throws DsljsonException {
        try {
            return (T) dslJson.deserialize(type, src, src.length);
        } catch (IOException e) {
            throw new DsljsonException(e);
        }
    }

    public <T> T loads(byte[] src, TypeDefinition<T> typeDefinition) throws DsljsonException {
        return loads(src, typeDefinition.type);
    }

    public <T> T loads(String src, TypeDefinition<T> typeDefinition) throws DsljsonException {
        return loads(src, typeDefinition.type);
    }

    @Override
    public byte[] dumpsToBytes(Object obj) throws DsljsonException {
        var jsonWriter = dslJson.newWriter();
        try {
            dslJson.serialize(jsonWriter, obj);
        } catch (IOException e) {
            throw new DsljsonException(e);
        }
        return jsonWriter.toByteArray();
    }

    @Override
    public String dumpsToString(Object obj) throws DsljsonException {
        var jsonWriter = dslJson.newWriter();
        try {
            dslJson.serialize(jsonWriter, obj);
        } catch (IOException e) {
            throw new DsljsonException(e);
        }
        return jsonWriter.toString();
    }

    @Override
    public void dumps(Object obj, OutputStream out) throws DsljsonException {
        try {
            dslJson.serialize(obj, out);
        } catch (IOException e) {
            throw new DsljsonException(e);
        }
    }

}
