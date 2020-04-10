package com.github.fmjsjx.libcommons.util.redis.lettuce;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.github.fmjsjx.libcommons.util.redis.LuaScript;

import io.lettuce.core.RedisNoScriptException;
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
        try {
            return sync().evalsha(script.sha1(), type, keys);
        } catch (RedisNoScriptException e) {
            return sync().eval(script.script(), type, keys);
        }
    }

    @SuppressWarnings("unchecked")
    default <R> R eval(LuaScript script, ScriptOutputType type, K[] keys, V... values) {
        try {
            return sync().evalsha(script.sha1(), type, keys, values);
        } catch (RedisNoScriptException e) {
            return sync().eval(script.script(), type, keys, values);
        }
    }

    @SuppressWarnings("unchecked")
    default <R> CompletionStage<R> evalAsync(LuaScript script, ScriptOutputType type, K... keys) {
        return async().<R>evalsha(script.sha1(), type, keys).<CompletionStage<R>>handle((r, e) -> {
            if (e != null) {
                if (e instanceof RedisNoScriptException) {
                    return async().<R>eval(script.script(), type, keys);
                }
                return CompletableFuture.failedStage(e);
            }
            return CompletableFuture.completedStage(r);
        }).thenCompose(r -> r);
    }

    @SuppressWarnings("unchecked")
    default <R> CompletionStage<R> evalAsync(LuaScript script, ScriptOutputType type, K[] keys, V... values) {
        return async().<R>evalsha(script.sha1(), type, keys, values).<CompletionStage<R>>handle((r, e) -> {
            if (e != null) {
                if (e instanceof RedisNoScriptException) {
                    return async().<R>eval(script.script(), type, keys, values);
                }
                return CompletableFuture.failedStage(e);
            }
            return CompletableFuture.completedStage(r);
        }).thenCompose(r -> r);
    }

}
