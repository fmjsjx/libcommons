package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.redis;

import org.springframework.beans.BeansException;
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
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.redis.LettuceProperties.RedisClientConnectionProperties;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.redis.LettuceProperties.RedisClientProperties;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.redis.LettuceProperties.RedisConnectionCodec;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.redis.LettuceProperties.RedisConnectionType;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.resource.ClientResources;

@Configuration
@ConditionalOnClass(RedisClient.class)
@ConditionalOnMissingBean(RedisClient.class)
@EnableConfigurationProperties(LettuceProperties.class)
@AutoConfigureAfter(EnvironmentUtilAutoConfiguration.class)
public class LettuceAutoConfiguration {

    @Bean
    public static final ConnectionRegisteryProcessor connectionRegisteryProcessor() {
        return new ConnectionRegisteryProcessor();
    }

    public static final class ConnectionRegisteryProcessor implements BeanDefinitionRegistryPostProcessor {

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
            for (RedisClientConnectionProperties connectionProperties : properties.getConnections()) {
                registerConnectionBean(client, connectionProperties);
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

        private void registerConnectionBean(RedisClient client, RedisClientConnectionProperties properties)
                throws BeansException {
            String beanName = properties.getName() + "RedisConnection";
            RedisURI uri = createUri(properties);
            RedisCodec<?, ?> codec = getRedisCodec(properties.getCodec());
            if (properties.getType() == RedisConnectionType.NORMAL) {
                registry.registerBeanDefinition(redisClientBeanName(),
                        BeanDefinitionBuilder
                                .genericBeanDefinition(StatefulRedisConnection.class, () -> client.connect(codec, uri))
                                .addDependsOn(beanName).getBeanDefinition());
            }
        }

        private static final RedisURI createUri(RedisClientConnectionProperties properties) {
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

    }

}
