package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.kafka;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.util.unit.DataSize;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ConfigurationProperties("libcommons.kafka")
public class KafkaProperties {

    private List<ProducerProperties> producers;
    private List<ConsumerProperties> consumers;

    @Getter
    @Setter
    @ToString
    public static class ProducerProperties {

        @NonNull
        private String name;
        /**
         * The default is <code>"${name}KafkaProducer"</code>.
         */
        private String beanName;

        /**
         * The default is
         * {@code org.apache.kafka.common.serialization.StringSerializer}.
         */
        private Class<? extends Serializer<?>> keySerializer = StringSerializer.class;
        /**
         * The default is
         * {@code org.apache.kafka.common.serialization.StringSerializer}.
         */
        private Class<? extends Serializer<?>> valueSerializer = StringSerializer.class;

        /**
         * The default is {@code 1}.
         */
        private String acks;

        @NonNull
        private String bootstrapServers;
        /**
         * The default is {@code 32MB}.
         */
        private DataSize bufferMemory;
        /**
         * The default is {@code none}.
         */
        private CompressionType compressionType;
        /**
         * The default is {@code Integer.MAX_VALUE}.
         */
        private Integer retries;
        /**
         * Other producer configurations.
         */
        private Properties configs;
    }

    public enum CompressionType {

        NONE("none"), GZIP("gzip"), SNAPPY("snappy"), LZ4("lz4"), ZSTD("zstd");

        private final String value;

        private CompressionType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return name() + "(" + value + ")";
        }

    }

    @Getter
    @Setter
    @ToString
    public static class ConsumerProperties {

        @NonNull
        private String name;
        /**
         * The default is <code>"${name}KafkaConsumer"</code>.
         */
        private String beanName;

        /**
         * The default is
         * {@code org.apache.kafka.common.serialization.StringDeserializer}.
         */
        private Class<? extends Deserializer<?>> keyDeserializer = StringDeserializer.class;
        /**
         * The default is
         * {@code org.apache.kafka.common.serialization.StringDeserializer}.
         */
        private Class<? extends Deserializer<?>> valueDeserializer = StringDeserializer.class;
        @NonNull
        private String bootstrapServers;
        @NonNull
        private String groupId;

        private String groupInstanceId;
        /**
         * The default is {@code 1B}.
         */
        private DataSize fetchMinSize;
        /**
         * The default is {@code 512MB}.
         */
        private DataSize fetchMaxSize;
        /**
         * The default is {@code 3S}.
         */
        private Duration heartbeatInterval;
        /**
         * The default is {@code 10S}.
         */
        private Duration sessionTimeout;
        /**
         * The default is {@code latest}.
         */
        private AutoOffsetReset autoOffsetReset;
        /**
         * The default if {@code 1MB}.
         */
        private DataSize maxPartitionFetchSize;
        /**
         * The default is {@code true}.
         */
        private Boolean enableAutoCommit;
        /**
         * Other producer configurations.
         */
        private Properties configs;
    }

    public enum AutoOffsetReset {

        LATEST("latest"), EARLIEST("earliest"), NONE("none");

        private final String value;

        private AutoOffsetReset(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return name() + "(" + value + ")";
        }
    }

}
