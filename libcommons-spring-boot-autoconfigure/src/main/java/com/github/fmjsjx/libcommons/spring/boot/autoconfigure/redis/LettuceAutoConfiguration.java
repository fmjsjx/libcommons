package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.redis;

import java.util.function.Supplier;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.EnvironmentUtil;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.EnvironmentUtilAutoConfiguration;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.redis.LettuceProperties.RedisConnectionProperties;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.redis.LettuceProperties.RedisClientProperties;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.redis.LettuceProperties.RedisConnectionCodec;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.redis.LettuceProperties.RedisConnectionType;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.redis.LettuceProperties.RedisPoolMode;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.redis.LettuceProperties.RedisPoolProperties;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.support.AsyncConnectionPoolSupport;
import io.lettuce.core.support.AsyncPool;
import io.lettuce.core.support.BoundedPoolConfig;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Configuration
@ConditionalOnClass(RedisClient.class)
@ConditionalOnMissingBean(RedisClient.class)
@EnableConfigurationProperties(LettuceProperties.class)
@AutoConfigureAfter(EnvironmentUtilAutoConfiguration.class)
public class LettuceAutoConfiguration {

    @Bean
    public static final LettuceRegisteryProcessor lettuceRegisteryProcessor() {
        return new LettuceRegisteryProcessor();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class LettuceRegisteryProcessor implements BeanDefinitionRegistryPostProcessor {

        private BeanDefinitionRegistry registry;

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            // ignore
        }

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            this.registry = registry;
            Environment env = EnvironmentUtil.getInstance().getEnvironment();
            BindResult<RedisClientProperties> bindResult = Binder.get(env).bind("libcommons.redis.lettuce.client",
                    RedisClientProperties.class);
            bindResult.ifBound(this::registerBeans);
        }

        private void registerBeans(RedisClientProperties properties) throws BeansException {
            RedisClient client = registerClientBean(properties);
            for (RedisConnectionProperties connectionProperties : properties.getConnections()) {
                registerConnectionBean(client, connectionProperties);
            }
            for (RedisPoolProperties poolProperties : properties.getPools()) {
                registerPoolBean(client, poolProperties);
            }
        }

        private static final String redisClientBeanName() {
            return "io.lettuce.core.RedisClient";
        }

        private RedisClient registerClientBean(RedisClientProperties properties) {
            ClientResources.Builder builder = ClientResources.builder();
            if (properties.getIoThreads() > 0) {
                builder.ioThreadPoolSize(properties.getIoThreads());
            }
            if (properties.getComputationThreads() > 0) {
                builder.computationThreadPoolSize(properties.getComputationThreads());
            }
            RedisClient client = RedisClient.create(builder.build());
            registry.registerBeanDefinition(redisClientBeanName(),
                    BeanDefinitionBuilder.genericBeanDefinition(RedisClient.class, () -> client)
                            .setDestroyMethodName("shutdown").getBeanDefinition());
            return client;
        }

        private void registerConnectionBean(RedisClient client, RedisConnectionProperties properties)
                throws BeansException {
            String beanName = properties.getName() + "RedisConnection";
            RedisURI uri = createUri(properties);
            RedisCodec<?, ?> codec = getRedisCodec(properties.getCodec());
            if (properties.getType() == RedisConnectionType.NORMAL) {
                BeanDefinition beanDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(StatefulRedisConnection.class, () -> client.connect(codec, uri))
                        .addDependsOn(redisClientBeanName()).getBeanDefinition();
                registry.registerBeanDefinition(beanName, beanDefinition);
            } else if (properties.getType() == RedisConnectionType.PUBSUB) {
                BeanDefinition beanDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(StatefulRedisPubSubConnection.class,
                                () -> client.connectPubSub(codec, uri))
                        .addDependsOn(redisClientBeanName()).getBeanDefinition();
                registry.registerBeanDefinition(beanName, beanDefinition);
            }
        }

        private static final RedisURI createUri(RedisConnectionProperties properties) {
            var uri = properties.getUri();
            if (uri != null) {
                return RedisURI.create(uri);
            }
            var redisUri = RedisURI.create(properties.getHost(), properties.getPort());
            redisUri.setDatabase(properties.getDb());
            var auth = properties.getAuth();
            if (auth != null && !auth.isBlank()) {
                redisUri.setPassword(auth.trim());
            }
            return redisUri;
        }

        private static final RedisCodec<?, ?> getRedisCodec(RedisConnectionCodec codec) {
            switch (codec) {
            case ASCII:
                return StringCodec.ASCII;
            case BYTE_ARRAY:
                return ByteArrayCodec.INSTANCE;
            default:
            case UTF8:
                return StringCodec.UTF8;
            }
        }

        private void registerPoolBean(RedisClient client, RedisPoolProperties properties) throws BeansException {
            String beanName = properties.getName() + "RedisPool";
            if (properties.getType() != RedisConnectionType.NORMAL) {
                throw new FatalBeanException("Redis connection type must be normal for pools");
            }
            RedisURI uri = createUri(properties);
            RedisCodec<?, ?> codec = getRedisCodec(properties.getCodec());
            if (properties.getMode() == RedisPoolMode.ASYNC) {
                BoundedPoolConfig config = buildBoundedPoolConfig(properties);
                BeanDefinition beanDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(AsyncPool.class,
                                () -> AsyncConnectionPoolSupport
                                        .createBoundedObjectPool(() -> client.connectAsync(codec, uri), config))
                        .addDependsOn(redisClientBeanName()).getBeanDefinition();
                registry.registerBeanDefinition(beanName, beanDefinition);
            } else {
                Supplier<StatefulRedisConnection<?, ?>> connectionSupplier = () -> client.connect(codec, uri);
                GenericObjectPoolConfig<StatefulRedisConnection<?, ?>> config = buildGenericPoolConfig(properties);
                BeanDefinition beanDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(GenericObjectPool.class,
                                () -> ConnectionPoolSupport.createGenericObjectPool(connectionSupplier, config))
                        .setDestroyMethodName("close").addDependsOn(redisClientBeanName()).getBeanDefinition();
                registry.registerBeanDefinition(beanName, beanDefinition);
            }
        }

        private static final BoundedPoolConfig buildBoundedPoolConfig(RedisPoolProperties properties) {
            BoundedPoolConfig.Builder builder = BoundedPoolConfig.builder();
            if (properties.getMaxTotal() > 0) {
                builder.maxTotal(properties.getMaxTotal());
            }
            if (properties.getMaxIdle() > 0) {
                builder.maxIdle(properties.getMaxIdle());
            }
            if (properties.getMinIdle() > 0) {
                builder.minIdle(properties.getMinIdle());
            }
            BoundedPoolConfig config = builder.build();
            return config;
        }

        private static final GenericObjectPoolConfig<StatefulRedisConnection<?, ?>> buildGenericPoolConfig(
                RedisPoolProperties properties) {
            GenericObjectPoolConfig<StatefulRedisConnection<?, ?>> config = new GenericObjectPoolConfig<>();
            if (properties.getMaxTotal() > 0) {
                config.setMaxTotal(properties.getMaxTotal());
            }
            if (properties.getMaxIdle() > 0) {
                config.setMaxIdle(properties.getMaxIdle());
            }
            if (properties.getMinIdle() > 0) {
                config.setMinIdle(properties.getMinIdle());
            }
            return config;
        }
    }

}
