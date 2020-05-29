package com.github.fmjsjx.libcommons.json;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;

public class Jdk8TimeSupport {

    public static final void enableAll() {
        try {
            LocalDateTimeSupport.enable();
        } catch (Exception e) {
            // ignore
        }
        try {
            LocalDateSupport.enable();
        } catch (Exception e) {
            // ignore
        }
        try {
            LocalTimeSupport.enable();
        } catch (Exception e) {
            // ignore
        }
        try {
            OffsetDateTimeSupport.enable();
        } catch (Exception e) {
            // ignore
        }
        try {
            ZonedDateTimeSupport.enable();
        } catch (Exception e) {
            // ignore
        }
    }

    private Jdk8TimeSupport() {
    }

    public static final class LocalDateTimeSupport {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        public static final boolean enabled() {
            return enabled.get();
        }

        public static final void enable() {
            enable(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        private static final void enable(DateTimeFormatter formatter) {
            if (enabled.compareAndSet(false, true)) {
                JsoniterSpi.registerTypeEncoder(LocalDateTime.class, new Encoder.ReflectionEncoder() {
                    @Override
                    public void encode(Object obj, JsonStream stream) throws IOException {
                        stream.writeVal(formatter.format((LocalDateTime) obj));
                    }

                    @Override
                    public Any wrap(Object obj) {
                        return Any.wrap(formatter.format((LocalDateTime) obj));
                    }
                });
                JsoniterSpi.registerTypeDecoder(LocalDateTime.class, new Decoder() {
                    @Override
                    public Object decode(JsonIterator iter) throws IOException {
                        return LocalDateTime.parse(iter.readString(), formatter);
                    }
                });
            } else {
                throw new IllegalStateException("LocalDateTimeSupport.enable can only be called once");
            }
        }

        private LocalDateTimeSupport() {
        }

    }

    public static final class LocalDateSupport {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        public static final boolean enabled() {
            return enabled.get();
        }

        public static final void enable() {
            enable(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        private static final void enable(DateTimeFormatter formatter) {
            if (enabled.compareAndSet(false, true)) {
                JsoniterSpi.registerTypeEncoder(LocalDate.class, new Encoder.ReflectionEncoder() {
                    @Override
                    public void encode(Object obj, JsonStream stream) throws IOException {
                        stream.writeVal(formatter.format((LocalDate) obj));
                    }

                    @Override
                    public Any wrap(Object obj) {
                        return Any.wrap(formatter.format((LocalDate) obj));
                    }
                });
                JsoniterSpi.registerTypeDecoder(LocalDate.class, new Decoder() {
                    @Override
                    public Object decode(JsonIterator iter) throws IOException {
                        return LocalDate.parse(iter.readString(), formatter);
                    }
                });
            } else {
                throw new IllegalStateException("LocalDateSupport.enable can only be called once");
            }
        }

        private LocalDateSupport() {
        }

    }

    public static final class LocalTimeSupport {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        public static final boolean enabled() {
            return enabled.get();
        }

        public static final void enable() {
            enable(DateTimeFormatter.ISO_LOCAL_TIME);
        }

        private static final void enable(DateTimeFormatter formatter) {
            if (enabled.compareAndSet(false, true)) {
                JsoniterSpi.registerTypeEncoder(LocalTime.class, new Encoder.ReflectionEncoder() {
                    @Override
                    public void encode(Object obj, JsonStream stream) throws IOException {
                        stream.writeVal(formatter.format((LocalTime) obj));
                    }

                    @Override
                    public Any wrap(Object obj) {
                        return Any.wrap(formatter.format((LocalTime) obj));
                    }
                });
                JsoniterSpi.registerTypeDecoder(LocalTime.class, new Decoder() {
                    @Override
                    public Object decode(JsonIterator iter) throws IOException {
                        return LocalTime.parse(iter.readString(), formatter);
                    }
                });
            } else {
                throw new IllegalStateException("LocalTimeSupport.enable can only be called once");
            }
        }

        private LocalTimeSupport() {
        }

    }

    public static final class OffsetDateTimeSupport {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        public static final boolean enabled() {
            return enabled.get();
        }

        public static final void enable() {
            enable(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }

        private static final void enable(DateTimeFormatter formatter) {
            if (enabled.compareAndSet(false, true)) {
                JsoniterSpi.registerTypeEncoder(OffsetDateTime.class, new Encoder.ReflectionEncoder() {
                    @Override
                    public void encode(Object obj, JsonStream stream) throws IOException {
                        stream.writeVal(formatter.format((OffsetDateTime) obj));
                    }

                    @Override
                    public Any wrap(Object obj) {
                        return Any.wrap(formatter.format((OffsetDateTime) obj));
                    }
                });
                JsoniterSpi.registerTypeDecoder(OffsetDateTime.class, new Decoder() {
                    @Override
                    public Object decode(JsonIterator iter) throws IOException {
                        return OffsetDateTime.parse(iter.readString(), formatter);
                    }
                });
            } else {
                throw new IllegalStateException("OffsetDateTimeSupport.enable can only be called once");
            }
        }

    }

    public static final class ZonedDateTimeSupport {

        private static final AtomicBoolean enabled = new AtomicBoolean();

        public static final boolean enabled() {
            return enabled.get();
        }

        public static final void enable() {
            enable(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        }

        private static final void enable(DateTimeFormatter formatter) {
            if (enabled.compareAndSet(false, true)) {
                JsoniterSpi.registerTypeEncoder(ZonedDateTime.class, new Encoder.ReflectionEncoder() {
                    @Override
                    public void encode(Object obj, JsonStream stream) throws IOException {
                        stream.writeVal(formatter.format((ZonedDateTime) obj));
                    }

                    @Override
                    public Any wrap(Object obj) {
                        return Any.wrap(formatter.format((ZonedDateTime) obj));
                    }
                });
                JsoniterSpi.registerTypeDecoder(ZonedDateTime.class, new Decoder() {
                    @Override
                    public Object decode(JsonIterator iter) throws IOException {
                        return ZonedDateTime.parse(iter.readString(), formatter);
                    }
                });
            } else {
                throw new IllegalStateException("ZonedDateTimeSupport.enable can only be called once");
            }
        }

    }

}
