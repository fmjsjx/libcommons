package com.github.fmjsjx.libcommons.json;

import java.io.IOException;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;

public class Jdk8OptionalSupport {

    public static final void enableAll() {
        try {
            OptionalIntSupport.enable();
        } catch (Exception e) {
            // ignore
        }
        try {
            OptionalLongSupport.enable();
        } catch (Exception e) {
            // ignore
        }
        try {
            OptionalDoubleSupport.enable();
        } catch (Exception e) {
            // ignore
        }
    }

    private Jdk8OptionalSupport() {
    }

    public static final class OptionalIntSupport {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        public static final boolean enabled() {
            return enabled.get();
        }

        public static final void enable() {
            if (enabled.compareAndSet(false, true)) {
                JsoniterSpi.registerTypeEncoder(OptionalInt.class, new Encoder.ReflectionEncoder() {

                    @Override
                    public void encode(Object obj, JsonStream stream) throws IOException {
                        OptionalInt value = (OptionalInt) obj;
                        if (value.isPresent()) {
                            stream.writeVal(value.getAsInt());
                        } else {
                            stream.writeNull();
                        }
                    }

                    @Override
                    public Any wrap(Object obj) {
                        OptionalInt value = (OptionalInt) obj;
                        if (value.isPresent()) {
                            return Any.wrap(value.getAsInt());
                        } else {
                            return Any.wrapNull();
                        }
                    }

                });
                JsoniterSpi.registerTypeDecoder(OptionalInt.class, new Decoder() {

                    @Override
                    public Object decode(JsonIterator iter) throws IOException {
                        ValueType valueType = iter.whatIsNext();
                        if (valueType == ValueType.NUMBER) {
                            return OptionalInt.of(iter.readInt());
                        } else if (valueType == ValueType.NULL) {
                            iter.skip();
                            return OptionalInt.empty();
                        } else {
                            throw new JsonException("expect int, but found " + valueType);
                        }
                    }

                });
            } else {
                throw new IllegalStateException("OptionalIntSupport.enable can only be called once");
            }
        }

    }

    public static final class OptionalLongSupport {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        public static final boolean enabled() {
            return enabled.get();
        }

        public static final void enable() {
            if (enabled.compareAndSet(false, true)) {
                JsoniterSpi.registerTypeEncoder(OptionalLong.class, new Encoder.ReflectionEncoder() {

                    @Override
                    public void encode(Object obj, JsonStream stream) throws IOException {
                        OptionalLong value = (OptionalLong) obj;
                        if (value.isPresent()) {
                            stream.writeVal(value.getAsLong());
                        } else {
                            stream.writeNull();
                        }
                    }

                    @Override
                    public Any wrap(Object obj) {
                        OptionalLong value = (OptionalLong) obj;
                        if (value.isPresent()) {
                            return Any.wrap(value.getAsLong());
                        } else {
                            return Any.wrapNull();
                        }
                    }

                });
                JsoniterSpi.registerTypeDecoder(OptionalLong.class, new Decoder() {

                    @Override
                    public Object decode(JsonIterator iter) throws IOException {
                        ValueType valueType = iter.whatIsNext();
                        if (valueType == ValueType.NUMBER) {
                            return OptionalLong.of(iter.readLong());
                        } else if (valueType == ValueType.NULL) {
                            iter.skip();
                            return OptionalLong.empty();
                        } else {
                            throw new JsonException("expect long, but found " + valueType);
                        }
                    }

                });
            } else {
                throw new IllegalStateException("OptionalLongSupport.enable can only be called once");
            }
        }

    }

    public static final class OptionalDoubleSupport {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        public static final boolean enabled() {
            return enabled.get();
        }

        public static final void enable() {
            if (enabled.compareAndSet(false, true)) {
                JsoniterSpi.registerTypeEncoder(OptionalDouble.class, new Encoder.ReflectionEncoder() {

                    @Override
                    public void encode(Object obj, JsonStream stream) throws IOException {
                        OptionalDouble value = (OptionalDouble) obj;
                        if (value.isPresent()) {
                            stream.writeVal(value.getAsDouble());
                        } else {
                            stream.writeNull();
                        }
                    }

                    @Override
                    public Any wrap(Object obj) {
                        OptionalDouble value = (OptionalDouble) obj;
                        if (value.isPresent()) {
                            return Any.wrap(value.getAsDouble());
                        } else {
                            return Any.wrapNull();
                        }
                    }

                });
                JsoniterSpi.registerTypeDecoder(OptionalDouble.class, new Decoder() {

                    @Override
                    public Object decode(JsonIterator iter) throws IOException {
                        ValueType valueType = iter.whatIsNext();
                        if (valueType == ValueType.NUMBER) {
                            return OptionalDouble.of(iter.readDouble());
                        } else if (valueType == ValueType.NULL) {
                            iter.skip();
                            return OptionalDouble.empty();
                        } else {
                            throw new JsonException("expect double, but found " + valueType);
                        }
                    }

                });
            } else {
                throw new IllegalStateException("OptionalDoubleSupport.enable can only be called once");
            }
        }

    }

}
