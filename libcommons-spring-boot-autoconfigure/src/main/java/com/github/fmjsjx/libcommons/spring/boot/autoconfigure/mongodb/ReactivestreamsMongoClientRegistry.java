package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.mongodb;

import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.mongodb.MongoDBProperties.MongoClientProperties;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ReactivestreamsMongoClientRegistry {

    static final void register(BeanDefinitionRegistry registry, MongoClientProperties config) {
        var name = config.getName();
        var beanName = Optional.ofNullable(config.getBeanName()).orElseGet(() -> name + "MongoClient");
        var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(MongoClient.class, beanFactory(config))
                .setScope(BeanDefinition.SCOPE_SINGLETON).setPrimary(config.isPrimary()).getBeanDefinition();
        log.debug("Register reactivestreams mongo client bean definition \"{}\" >>> {}", beanName, beanDefinition);
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    private static final Supplier<MongoClient> beanFactory(MongoClientProperties config) {
        return () -> MongoClients.create(MongoClientSettingsFactory.create(config));
    }

}
