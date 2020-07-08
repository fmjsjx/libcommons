package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.redis;

import java.util.function.Supplier;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.redis.LettuceProperties.RedisPoolProperties;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.support.ConnectionPoolSupport;

class GenericPoolRegistry {

    static void registerGenericPoolBean(BeanDefinitionRegistry registry, String clientBeanName, RedisClient client,
            RedisPoolProperties properties, String beanName, RedisURI uri, RedisCodec<?, ?> codec) {
        Supplier<StatefulRedisConnection<?, ?>> connectionSupplier = () -> client.connect(codec, uri);
        GenericObjectPoolConfig<StatefulRedisConnection<?, ?>> config = GenericPoolRegistry
                .buildGenericPoolConfig(properties);
        BeanDefinition beanDefinition = BeanDefinitionBuilder
                .genericBeanDefinition(GenericObjectPool.class,
                        () -> ConnectionPoolSupport.createGenericObjectPool(connectionSupplier, config))
                .setPrimary(properties.isPrimary()).addDependsOn(clientBeanName).getBeanDefinition();
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    static final GenericObjectPoolConfig<StatefulRedisConnection<?, ?>> buildGenericPoolConfig(
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
