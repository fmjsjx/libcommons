package com.github.fmjsjx.libcommons.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fmjsjx.libcommons.util.function.io.BiIoConsumer;
import com.github.fmjsjx.libcommons.util.function.io.IoConsumer;
import com.github.fmjsjx.libcommons.util.function.io.IoFunction;
import com.github.fmjsjx.libcommons.util.function.io.IoSupplier;

public class Jackson2Library implements JsonLibrary<JsonNode> {

    public static final class Jackson2Exception extends JsonException {

        private static final long serialVersionUID = -6059180774102905076L;

        public Jackson2Exception(String message, Throwable cause) {
            super(message, cause);
        }

        public Jackson2Exception(Throwable cause) {
            super(cause);
        }

    }

    private static final class DefaultInstanceHolder {

        private static final Jackson2Library instance = new Jackson2Library(createDefaultObjectMapper());

    }

    private static final ObjectMapper createDefaultObjectMapper() {
        return new ObjectMapper().setSerializationInclusion(Include.NON_ABSENT)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    public static final Jackson2Library getInstance() {
        return DefaultInstanceHolder.instance;
    }

    private final ObjectMapper objectMapper;

    public Jackson2Library(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Jackson2Library() {
        this(createDefaultObjectMapper());
    }

    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends JsonNode> T loads(byte[] src) throws Jackson2Exception {
        try {
            return (T) objectMapper.readTree(src);
        } catch (IOException e) {
            throw new Jackson2Exception(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends JsonNode> T loads(String src) throws Jackson2Exception {
        try {
            return (T) objectMapper.readTree(src);
        } catch (IOException e) {
            throw new Jackson2Exception(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends JsonNode> T loads(InputStream src) throws Jackson2Exception {
        try {
            return (T) objectMapper.readTree(src);
        } catch (IOException e) {
            throw new Jackson2Exception(e);
        }
    }

    @Override
    public <T> T loads(byte[] src, Class<T> type) throws Jackson2Exception {
        try {
            return objectMapper.readValue(src, type);
        } catch (IOException e) {
            throw new Jackson2Exception(e);
        }
    }

    @Override
    public <T> T loads(byte[] src, Type type) throws Jackson2Exception {
        return loads(src, objectMapper.constructType(type));
    }

    public <T> T loads(byte[] src, JavaType type) throws Jackson2Exception {
        try {
            return objectMapper.readValue(src, type);
        } catch (IOException e) {
            throw new Jackson2Exception(e);
        }
    }

    public <T> T loads(byte[] src, TypeReference<T> type) throws Jackson2Exception {
        try {
            return objectMapper.readValue(src, type);
        } catch (IOException e) {
            throw new Jackson2Exception(e);
        }
    }

    public <T> T loads(InputStream src, Class<T> type) throws Jackson2Exception {
        try {
            return objectMapper.readValue(src, type);
        } catch (IOException e) {
            throw new Jackson2Exception(e);
        }
    }

    public <T> T loads(InputStream src, JavaType type) throws Jackson2Exception {
        try {
            return objectMapper.readValue(src, type);
        } catch (IOException e) {
            throw new Jackson2Exception(e);
        }
    }

    public <T> T loads(InputStream src, TypeReference<T> type) throws Jackson2Exception {
        try {
            return objectMapper.readValue(src, type);
        } catch (IOException e) {
            throw new Jackson2Exception(e);
        }
    }

    public <T> T loads(IoFunction<ObjectMapper, T> parser) throws Jackson2Exception {
        try {
            return parser.apply(objectMapper);
        } catch (IOException e) {
            throw new Jackson2Exception(e);
        }
    }

    @Override
    public byte[] dumpsToBytes(Object obj) throws Jackson2Exception {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new Jackson2Exception(e);
        }
    }

    @Override
    public String dumpsToString(Object obj) throws Jackson2Exception {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new Jackson2Exception(e);
        }
    }

    @Override
    public void dumps(Object obj, OutputStream out) throws Jackson2Exception {
        try {
            objectMapper.writeValue(out, obj);
        } catch (IOException e) {
            throw new Jackson2Exception(e);
        }
    }

    public void dumps(IoConsumer<ObjectMapper> convertor) throws Jackson2Exception {
        try {
            convertor.accept(objectMapper);
        } catch (IOException e) {
            throw new Jackson2Exception(e);
        }
    }

    public <O> void dumps(BiIoConsumer<ObjectMapper, O> convertor, O output) throws Jackson2Exception {
        try {
            convertor.accept(objectMapper, output);
        } catch (IOException e) {
            throw new Jackson2Exception(e);
        }
    }

    public <O> O dumps(BiIoConsumer<ObjectMapper, O> convertor, IoSupplier<O> outputSupplier) throws Jackson2Exception {
        try {
            O output = outputSupplier.get();
            convertor.accept(objectMapper, output);
            return output;
        } catch (IOException e) {
            throw new Jackson2Exception(e);
        }
    }

}
