package com.github.fmjsjx.libcommons.util.redis.lettuce;

import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.pool2.impl.GenericObjectPool;

import com.github.fmjsjx.libcommons.util.redis.LuaScript;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public interface RedisPoolService<K, V> {

    GenericObjectPool<StatefulRedisConnection<K, V>> pool();

    default <R> R apply(Function<RedisCommands<K, V>, R> action) throws Exception {
        try (var conn = pool().borrowObject()) {
            return action.apply(conn.sync());
        }
    }

    default void accept(Consumer<RedisCommands<K, V>> action) throws Exception {
        try (var conn = pool().borrowObject()) {
            action.accept(conn.sync());
        }
    }

    @SuppressWarnings("unchecked")
    default <R> R eval(LuaScript script, ScriptOutputType type, K... keys) throws Exception {
        return apply(redis -> RedisUtil.eval(redis, script, type, keys));
    }

    @SuppressWarnings("unchecked")
    default <R> R eval(LuaScript script, ScriptOutputType type, K[] keys, V... values) throws Exception {
        return apply(redis -> RedisUtil.eval(redis, script, type, keys, values));
    }

}
