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

    private List<RedisClusterClientProperties> clusterClients = Collections.emptyList();

    @Getter
    @Setter
    @ToString
    public static class RedisClientProperties {

        /**
         * The default is {@code "io.lettuce.core.RedisClient"},
         * <p>
         * or <code>"${name}RedisClusterClient"</code> for cluster client.
         */
        private String beanName = "io.lettuce.core.RedisClient";

        private int ioThreads;

        private int computationThreads;

        private List<RedisConnectionProperties> connections = Collections.emptyList();

        private List<RedisPoolProperties> pools = Collections.emptyList();

    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class RedisClusterClientProperties extends RedisClientProperties {

        @NonNull
        private String name;
        /**
         * The REDIS URI.
         */
        private URI uri;
        /**
         * Weather this cluster client is primary or not.
         */
        private boolean primary;
        /**
         * The host.
         * <p>
         * Can't be set with {@code uri}.
         */
        private String host;
        /**
         * The default is 6379
         * <p>
         * Can't be set with {@code uri}.
         */
        private int port = 6379;
        /**
         * The password to AUTH.
         * <p>
         * Can't be set with {@code uri}.
         */
        private String auth;

    }

    @Getter
    @Setter
    @ToString
    public static class RedisConnectionProperties {

        @NonNull
        private String name;
        /**
         * The default is <code>"${name}RedisConnection"</code>,
         * <p>
         * or <code>"${name}RedisPool"</code> for pool,
         * <p>
         * or <code>"${name}RedisClusterConnection"</code> for cluster connection.
         */
        private String beanName;
        /**
         * Weather this connection is primary or not.
         */
        private boolean primary;
        /**
         * The REDIS URI.
         */
        private URI uri;
        /**
         * The host.
         * <p>
         * Can't be set with {@code uri}.
         */
        private String host;
        /**
         * The default is 6379
         * <p>
         * Can't be set with {@code uri}.
         */
        private int port = 6379;
        /**
         * The default is {@code 0}.
         * <p>
         * Can't be set with {@code uri}.
         */
        private int db;
        /**
         * The password to AUTH.
         * <p>
         * Can't be set with {@code uri}.
         */
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

        NORMAL, PUBSUB, SENTINEL

    }

    public enum RedisConnectionCodec {

        UTF8, ASCII, BYTE_ARRAY

    }

    public enum RedisPoolMode {

        SYNC, ASYNC

    }

}
