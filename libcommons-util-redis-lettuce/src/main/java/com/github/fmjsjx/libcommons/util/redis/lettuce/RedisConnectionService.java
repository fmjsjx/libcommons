package com.github.fmjsjx.libcommons.util.redis.lettuce;

import java.util.concurrent.CompletionStage;

import com.github.fmjsjx.libcommons.util.redis.LuaScript;

import io.lettuce.core.ScriptOutputType;
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

    @SuppressWarnings("unchecked")
    default <R> R eval(LuaScript script, ScriptOutputType type, K... keys) {
        return RedisUtil.eval(sync(), script, type, keys);
    }

    @SuppressWarnings("unchecked")
    default <R> R eval(LuaScript script, ScriptOutputType type, K[] keys, V... values) {
        return RedisUtil.eval(sync(), script, type, keys, values);
    }

    @SuppressWarnings("unchecked")
    default <R> CompletionStage<R> evalAsync(LuaScript script, ScriptOutputType type, K... keys) {
        return RedisUtil.eval(async(), script, type, keys);
    }

    @SuppressWarnings("unchecked")
    default <R> CompletionStage<R> evalAsync(LuaScript script, ScriptOutputType type, K[] keys, V... values) {
        return RedisUtil.eval(async(), script, type, keys, values);
    }

}
