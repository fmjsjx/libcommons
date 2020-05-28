package com.github.fmjsjx.libcommons.json;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Config;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;

/**
 * A jsoniter implementation of {@link JsonLibrary}.
 * 
 * @since 1.0
 *
 * @author MJ Fang
 */
@SuppressWarnings("unchecked")
public class JsoniterLibrary implements JsonLibrary<Any> {

    public static final class JsoniterException extends JsonException {

        private static final long serialVersionUID = 4253609897736931336L;

        public JsoniterException(String message, Throwable cause) {
            super(message, cause);
        }

        public JsoniterException(Throwable cause) {
            super(cause);
        }

    }

    private static final class DefaultInstanceHolder {
        private static final JsoniterLibrary instance = new JsoniterLibrary();

        static {
            JsoniterSpi.setDefaultConfig(JsoniterSpi.getDefaultConfig().copyBuilder().escapeUnicode(false).build());
            // TODO
        }
    }

    public static final JsoniterLibrary getInstance() {
        return DefaultInstanceHolder.instance;
    }

    private static final byte[] NULL_BYTE_ARRAY = "null".getBytes();

    private final boolean useDefaultConfig;
    private final Config config;

    public JsoniterLibrary() {
        this.useDefaultConfig = true;
        this.config = null;
    }

    public JsoniterLibrary(Config config) {
        this.useDefaultConfig = false;
        this.config = Objects.requireNonNull(config, "config must not be null");
    }

    @Override
    public <T extends Any> T loads(byte[] src) {
        try {
            if (useDefaultConfig) {
                return (T) JsonIterator.deserialize(src);
            }
            return (T) JsonIterator.deserialize(config, src);
        } catch (Exception e) {
            throw new JsoniterException(e);
        }
    }

    @Override
    public <T> T loads(byte[] src, Class<T> type) {
        try {
            if (useDefaultConfig) {
                return JsonIterator.deserialize(src, type);
            }
            return JsonIterator.deserialize(config, src, type);
        } catch (Exception e) {
            throw new JsoniterException(e);
        }
    }

    @Override
    public <T> T loads(byte[] src, Type type) {
        if (type instanceof Class) {
            return loads(src, (Class<T>) type);
        }
        TypeLiteral<T> typeLiteral = TypeLiteral.create(type);
        return loads(src, typeLiteral);
    }

    public <T> T loads(byte[] src, TypeLiteral<T> typeLiteral) {
        try {
            if (useDefaultConfig) {
                return JsonIterator.deserialize(src, typeLiteral);
            }
            return JsonIterator.deserialize(config, src, typeLiteral);
        } catch (Exception e) {
            throw new JsoniterException(e);
        }
    }

    public <T> T loads(String src, TypeLiteral<T> typeLiteral) {
        try {
            return loads(src.getBytes(StandardCharsets.UTF_8), typeLiteral);
        } catch (Exception e) {
            throw new JsoniterException(e);
        }
    }

    @Override
    public byte[] dumpsToBytes(Object obj) {
        if (obj == null) {
            return NULL_BYTE_ARRAY;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        dumps(obj, out);
        return out.toByteArray();
    }

    @Override
    public String dumpsToString(Object obj) {
        try {
            if (useDefaultConfig) {
                return JsonStream.serialize(obj);
            }
            return JsonStream.serialize(config, obj);
        } catch (Exception e) {
            throw new JsoniterException(e);
        }
    }

    @Override
    public void dumps(Object obj, OutputStream out) {
        try {
            if (useDefaultConfig) {
                JsonStream.serialize(obj, out);
            } else {
                JsonStream.serialize(config, obj, out);
            }
        } catch (Exception e) {
            throw new JsoniterException(e);
        }
    }

}
