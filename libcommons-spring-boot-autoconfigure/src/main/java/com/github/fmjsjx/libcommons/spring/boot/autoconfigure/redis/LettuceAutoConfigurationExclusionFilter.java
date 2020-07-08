package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.redis;

import java.util.Set;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LettuceAutoConfigurationExclusionFilter implements AutoConfigurationImportFilter {

    private static final Set<String> classNames = Set.of(
            "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
            "org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration");

    @Override
    public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        boolean[] matches = new boolean[autoConfigurationClasses.length];
        for (int i = 0; i < matches.length; i++) {
            var className = autoConfigurationClasses[i];
            boolean exclude = className == null ? false : classNames.contains(className);
            matches[i] = !exclude;
            if (exclude) {
                log.debug("Exclude AutoConfigurationName >>> {}", className);
            }
        }
        log.trace("Match result: {} >>> {}", autoConfigurationClasses, matches);
        return matches;
    }

}
