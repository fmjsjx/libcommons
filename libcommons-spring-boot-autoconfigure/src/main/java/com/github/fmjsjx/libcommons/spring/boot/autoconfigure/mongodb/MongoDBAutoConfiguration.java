package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.mongodb;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.mongodb.MongoDBProperties.CompressorProperties;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.mongodb.MongoDBProperties.DriverType;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.mongodb.MongoDBProperties.MongoClientProperties;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.mongodb.MongoDBProperties.PoolProperties;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.mongodb.MongoDBProperties.ServerHost;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.mongodb.MongoDBProperties.SocketProperties;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.mongodb.MongoDBProperties.SslProperties;
import com.mongodb.AuthenticationMechanism;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.ServerSettings;
import com.mongodb.connection.SocketSettings;
import com.mongodb.connection.SslSettings;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnClass(MongoClientSettings.class)
@EnableConfigurationProperties(MongoDBProperties.class)
public class MongoDBAutoConfiguration {

    @Bean
    public static MongoDBRegisteryProcessor mongodbRegisteryProcessor() {
        return new MongoDBRegisteryProcessor();
    }

    private static final Object NETTY_LIBRARY_LOCK = new Object();
    private static volatile NettyLibrary nettyLibrary;

    static NettyLibrary getNettyLibrary() {
        if (nettyLibrary == null) {
            synchronized (NETTY_LIBRARY_LOCK) {
                if (nettyLibrary == null) {
                    var threadFactory = new DefaultThreadFactory("mongodb-stream", true);
                    if (Epoll.isAvailable()) {
                        nettyLibrary = new NettyLibrary(new EpollEventLoopGroup(threadFactory),
                                EpollSocketChannel.class);
                    } else if (KQueue.isAvailable()) {
                        nettyLibrary = new NettyLibrary(new KQueueEventLoopGroup(threadFactory),
                                KQueueSocketChannel.class);
                    } else {
                        nettyLibrary = new NettyLibrary(new NioEventLoopGroup(threadFactory), NioSocketChannel.class);
                    }
                }
            }
        }
        return nettyLibrary;
    }

    @Getter
    @Setter
    @ToString
    @RequiredArgsConstructor
    static class NettyLibrary {

        private final EventLoopGroup eventLoopGroup;
        private final Class<? extends SocketChannel> socketChannelClass;

    }

    public static class MongoDBRegisteryProcessor implements EnvironmentAware, BeanDefinitionRegistryPostProcessor {

        private Environment environment;
        private BeanDefinitionRegistry registry;

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            // ignore
        }

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            this.registry = registry;
            var bindResult = Binder.get(environment).bind("libcommons.mongodb", MongoDBProperties.class);
            if (bindResult.isBound()) {
                Optional.ofNullable(bindResult.get().getClients()).ifPresent(this::registerClients);
            }
        }

        private void registerClients(List<MongoClientProperties> clients) {
            var groups = clients.stream().collect(Collectors.groupingBy(MongoClientProperties::getDriver));
            var syncGroup = groups.get(DriverType.SYNC);
            if (syncGroup != null) {
                if (syncGroup.size() == 1) {
                    syncGroup.get(0).setPrimary(true);
                }
                syncGroup.forEach(config -> {
                    log.debug("Register sync client >>> {}", config);
                    SyncMongoClientRegistry.register(registry, config);
                });
            }
            var reactivestreamsGroup = groups.get(DriverType.REACTIVESTREAMS);
            if (reactivestreamsGroup != null) {
                if (reactivestreamsGroup.size() == 1) {
                    reactivestreamsGroup.get(0).setPrimary(true);
                }
                reactivestreamsGroup.forEach(config -> {
                    log.debug("Register reactivestreams client >>> {}", config);
                    ReactivestreamsMongoClientRegistry.register(registry, config);
                });
            }
        }
    }

}

@Slf4j
class MongoClientSettingsFactory {

    static MongoClientSettings create(MongoClientProperties config) {
        var builder = MongoClientSettings.builder();
        if (config.getUri() != null) {
            builder.applyConnectionString(new ConnectionString(config.getUri()));
        } else {
            builder.applyToClusterSettings(b -> apply(b, config)) // cluster
                    .applyToConnectionPoolSettings(b -> apply(b, config.getPool())) // pool
                    .applyToServerSettings(b -> apply(b, config)) // server
                    .applyToSocketSettings(b -> apply(b, config.getSocket())) // socket
                    .applyToSslSettings(b -> apply(b, config.getSsl())); // SSL
            // compressor list
            Optional.ofNullable(config.getCompressorList()).filter(l -> l.size() > 0).ifPresent(list -> {
                var cl = list.stream().map(CompressorProperties::toMongoCompressor).collect(Collectors.toList());
                log.debug("Set compressor list >>> {}", cl);
                builder.compressorList(cl);
            });
            // credential
            var mechanism = config.getAuthMechanism();
            var userName = config.getUsername();
            var source = Optional.ofNullable(config.getAuthdb()).orElse("admin");
            var password = config.getPassword();
            if (userName != null) {
                builder.credential(createCredential(mechanism, userName, source, password));
            } else if (mechanism != null) {
                builder.credential(createCredential(mechanism, userName, source, password));
            }
        }
        if (config.isUseNetty()) {
            var library = MongoDBAutoConfiguration.getNettyLibrary();
            var sff = NettyStreamFactoryFactory.builder().eventLoopGroup(library.getEventLoopGroup())
                    .socketChannelClass(library.getSocketChannelClass()).build();
            log.debug("Set MongoClient NettyStreamFactoryFactory >>> {}", sff);
            builder.streamFactoryFactory(sff);
        }
        return builder.build();
    }

    private static void apply(ClusterSettings.Builder builder, MongoClientProperties config) {
        Optional.ofNullable(config.getSrvHost()).ifPresent(builder::srvHost);
        Optional.ofNullable(config.getHosts())
                .map(hosts -> hosts.stream().map(ServerHost::toServerAddress).collect(Collectors.toList()))
                .ifPresent(builder::hosts);
        Optional.ofNullable(config.getClusterConnectionMode()).ifPresent(builder::mode);
        Optional.ofNullable(config.getRequiredReplicaSetName()).ifPresent(builder::requiredReplicaSetName);
        Optional.ofNullable(config.getRequiredClusterType()).ifPresent(builder::requiredClusterType);
        Optional.ofNullable(config.getLocalThreshold())
                .ifPresent(v -> builder.localThreshold(v.toMillis(), TimeUnit.MILLISECONDS));
        Optional.ofNullable(config.getServerSelectionTimeout())
                .ifPresent(v -> builder.serverSelectionTimeout(v.toMillis(), TimeUnit.MILLISECONDS));
    }

    private static void apply(ConnectionPoolSettings.Builder builder, PoolProperties config) {
        if (config != null) {
            Optional.ofNullable(config.getMaxSize()).ifPresent(builder::maxSize);
            Optional.ofNullable(config.getMinSize()).ifPresent(builder::minSize);
            Optional.ofNullable(config.getMaxWaitTime())
                    .ifPresent(v -> builder.maxWaitTime(v.toMillis(), TimeUnit.MILLISECONDS));
            Optional.ofNullable(config.getMaxConnectionLifeTime())
                    .ifPresent(v -> builder.maxConnectionLifeTime(v.toMillis(), TimeUnit.MILLISECONDS));
            Optional.ofNullable(config.getMaxConnectionIdleTime())
                    .ifPresent(v -> builder.maxConnectionIdleTime(v.toMillis(), TimeUnit.MILLISECONDS));
            Optional.ofNullable(config.getMaintenanceInitialDelay())
                    .ifPresent(v -> builder.maintenanceInitialDelay(v.toMillis(), TimeUnit.MILLISECONDS));
            Optional.ofNullable(config.getMaintenanceFrequency())
                    .ifPresent(v -> builder.maintenanceFrequency(v.toMillis(), TimeUnit.MILLISECONDS));
        }
    }

    private static void apply(ServerSettings.Builder builder, MongoClientProperties config) {
        Optional.ofNullable(config.getHeartbeatFrequency())
                .ifPresent(v -> builder.heartbeatFrequency(v.toMillis(), TimeUnit.MILLISECONDS));
        Optional.ofNullable(config.getMinHeartbeatFrequency())
                .ifPresent(v -> builder.minHeartbeatFrequency(v.toMillis(), TimeUnit.MILLISECONDS));
    }

    private static void apply(SocketSettings.Builder builder, SocketProperties config) {
        if (config != null) {
            Optional.ofNullable(config.getConnectTimeout())
                    .ifPresent(v -> builder.connectTimeout((int) v.toMillis(), TimeUnit.MILLISECONDS));
            Optional.ofNullable(config.getReadTimeout())
                    .ifPresent(v -> builder.readTimeout((int) v.toMillis(), TimeUnit.MILLISECONDS));
            Optional.ofNullable(config.getReceiveBufferSize())
                    .ifPresent(v -> builder.receiveBufferSize((int) v.toBytes()));
            Optional.ofNullable(config.getSendBufferSize()).ifPresent(v -> builder.sendBufferSize((int) v.toBytes()));
        }
    }

    private static void apply(SslSettings.Builder builder, SslProperties config) {
        if (config != null) {
            Optional.ofNullable(config.getEnabled()).ifPresent(builder::enabled);
            Optional.ofNullable(config.getInvalidHostNameAllowed()).ifPresent(builder::invalidHostNameAllowed);
        }

    }

    private static MongoCredential createCredential(AuthenticationMechanism mechanism, String userName, String source,
            char[] password) {
        if (mechanism != null) {
            switch (mechanism) {
            case GSSAPI:
                return MongoCredential.createGSSAPICredential(userName);
            case MONGODB_X509:
                return MongoCredential.createMongoX509Credential(userName);
            case PLAIN:
                return MongoCredential.createPlainCredential(userName, source, password);
            case SCRAM_SHA_1:
                return MongoCredential.createScramSha1Credential(userName, source, password);
            case SCRAM_SHA_256:
                return MongoCredential.createScramSha256Credential(userName, source, password);
            }
        }
        return MongoCredential.createCredential(userName, source, password);
    }

}
