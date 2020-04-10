package com.github.fmjsjx.libcommons.util.redis.lettuce;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;

public interface RedisConnectionService<K, V> {

    StatefulRedisConnection<K, V> connection();

    default RedisAsyncCommands<K, V> async() {
        return connection().async();
    }

    default RedisCommands<K, V> sync() {
        return connection().sync();
    }

}
