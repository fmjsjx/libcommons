package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import io.lettuce.core.RedisClient;

@Configuration
@ConditionalOnClass(RedisClient.class)
@EnableConfigurationProperties(LettuceProperties.class)
public class LettuceAutoConfiguration {

}
