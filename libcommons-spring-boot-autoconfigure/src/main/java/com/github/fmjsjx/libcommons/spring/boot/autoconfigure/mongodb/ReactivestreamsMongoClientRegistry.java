package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.mongodb;

import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.mongodb.MongoDBProperties.MongoClientProperties;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;

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
        Optional.ofNullable(config.getDatabases()).ifPresent(dbs -> {
            dbs.forEach(db -> {
                var dbname = db.getName();
                var bean = Optional.ofNullable(db.getBeanName()).orElseGet(() -> db.getId() + "MongoDatabase");
                var definition = BeanDefinitionBuilder.genericBeanDefinition(MongoDatabase.class)
                        .setFactoryMethodOnBean("getDatabase", beanName).addConstructorArgValue(dbname)
                        .setScope(BeanDefinition.SCOPE_SINGLETON).setPrimary(db.isPrimary()).getBeanDefinition();
                log.debug("Register reactivestreams mongo database bean definition '{}' >>> {}", beanName, definition);
                registry.registerBeanDefinition(bean, definition);
            });
        });
    }

    private static final Supplier<MongoClient> beanFactory(MongoClientProperties config) {
        return () -> MongoClients.create(MongoClientSettingsFactory.create(config));
    }

}
