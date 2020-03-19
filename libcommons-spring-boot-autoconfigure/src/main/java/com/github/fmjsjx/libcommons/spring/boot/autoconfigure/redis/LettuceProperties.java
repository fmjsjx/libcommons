package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.redis;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ConfigurationProperties("libcommons.redis.lettuce")
public class LettuceProperties {

    private RedisClientProperties client;

    @Setter
    @Getter
    @ToString
    public static class RedisClientProperties {

        private int ioThreads;

        private int computationThreads;

        private List<RedisConnectionProperties> connections = Collections.emptyList();

        private List<RedisPoolProperties> pools = Collections.emptyList();

    }

    @Getter
    @Setter
    @ToString
    public static class RedisConnectionProperties {

        @NonNull
        private String name;

        private URI uri;

        private String host;
        /**
         * Default is 6379
         */
        private int port = 6379;
        private int db;
        private String auth;

        /**
         * Default is normal
         */
        private RedisConnectionType type = RedisConnectionType.NORMAL;
        /**
         * Default is utf8
         */
        private RedisConnectionCodec codec = RedisConnectionCodec.UTF8;

    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class RedisPoolProperties extends RedisConnectionProperties {

        /**
         * Default is sync
         */
        private RedisPoolMode mode = RedisPoolMode.SYNC;

        private int maxTotal;
        private int maxIdle;
        private int minIdle;

    }

    public enum RedisConnectionType {

        NORMAL, PUBSUB

    }

    public enum RedisConnectionCodec {

        UTF8, ASCII, BYTE_ARRAY

    }

    public enum RedisPoolMode {

        SYNC, ASYNC

    }

}
