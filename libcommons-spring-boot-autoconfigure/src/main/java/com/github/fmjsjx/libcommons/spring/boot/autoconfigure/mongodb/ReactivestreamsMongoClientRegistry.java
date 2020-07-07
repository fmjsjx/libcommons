package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.mongodb;

import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.mongodb.MongoDBProperties.MongoClientProperties;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

class ReactivestreamsMongoClientRegistry {

    static final void register(BeanDefinitionRegistry registry, MongoClientProperties config) {
        var name = config.getName();
        var beanName = Optional.ofNullable(config.getBeanName()).orElseGet(() -> name + "MongoClient");
        var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(MongoClient.class, beanFactory(config))
                .setScope(BeanDefinition.SCOPE_SINGLETON).getBeanDefinition();
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    private static final Supplier<MongoClient> beanFactory(MongoClientProperties config) {
        if (config.getUri() != null) {
            return () -> MongoClients.create(config.getUri());
        }
        var settings = MongoClientSettingsFactory.create(config);
        return () -> MongoClients.create(settings);
    }

}
