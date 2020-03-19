package com.github.fmjsjx.libcommons.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvironmentUtilAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public static final EnvironmentUtil environmentUtil() {
        return EnvironmentUtil.getInstance();
    }
}
