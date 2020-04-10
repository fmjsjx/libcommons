package com.github.fmjsjx.libcommons.util.redis.lettuce;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.AsyncPool;

public interface RedisAsyncPoolService<K, V> {

    AsyncPool<StatefulRedisConnection<K, V>> pool();
    
    // TODO

}
