package com.github.fmjsjx.libcommons.spring.boot.autoconfigure;

import java.util.Set;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AutoConfigurationExclusionFilter implements AutoConfigurationImportFilter {

    private static final Set<String> classNames = Set.of(
            // REDIS
            "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration",
            // MongoDB
            "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.mongo.MongoReactiveRepositoriesAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration",
            "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration",
            "org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration",
            "org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration");

    @Override
    public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        boolean[] matches = new boolean[autoConfigurationClasses.length];
        for (int i = 0; i < matches.length; i++) {
            var className = autoConfigurationClasses[i];
            log.trace("Check AutoConfigurationClass <<< {}", className);
            matches[i] = className == null ? true : !classNames.contains(className);
        }
        return matches;
    }

}
