package com.github.fmjsjx.libcommons.util.redis.lettuce;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.github.fmjsjx.libcommons.util.redis.LuaScript;

import io.lettuce.core.RedisNoScriptException;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.support.AsyncPool;

public interface RedisAsyncPoolService<K, V> {

    AsyncPool<StatefulRedisConnection<K, V>> pool();

    default <R> CompletableFuture<R> apply(Function<RedisAsyncCommands<K, V>, CompletionStage<R>> action) {
        var pool = pool();
        return pool.acquire().thenCompose(conn -> {
            return action.apply(conn.async()).whenComplete((r, e) -> pool.release(conn));
        });
    }

    default <R> CompletableFuture<R> applyAsync(Function<RedisAsyncCommands<K, V>, CompletionStage<R>> action) {
        var pool = pool();
        return pool.acquire().thenComposeAsync(conn -> {
            return action.apply(conn.async()).whenComplete((r, e) -> pool.release(conn));
        });
    }

    default <R> CompletableFuture<R> applyAsync(Function<RedisAsyncCommands<K, V>, CompletionStage<R>> action,
            Executor executor) {
        var pool = pool();
        return pool.acquire().thenComposeAsync(conn -> {
            return action.apply(conn.async()).whenComplete((r, e) -> pool.release(conn));
        }, executor);
    }

    default CompletableFuture<Void> accept(Function<RedisAsyncCommands<K, V>, CompletionStage<?>> action) {
        var pool = pool();
        return pool.acquire().thenAccept(conn -> {
            action.apply(conn.async()).whenComplete((r, e) -> pool.release(conn));
        });
    }

    default CompletableFuture<Void> acceptAsync(Function<RedisAsyncCommands<K, V>, CompletionStage<?>> action) {
        var pool = pool();
        return pool.acquire().thenAcceptAsync(conn -> {
            action.apply(conn.async()).whenComplete((r, e) -> pool.release(conn));
        });
    }

    default CompletableFuture<Void> acceptAsync(Function<RedisAsyncCommands<K, V>, CompletionStage<?>> action,
            Executor executor) {
        var pool = pool();
        return pool.acquire().thenAcceptAsync(conn -> {
            action.apply(conn.async()).whenComplete((r, e) -> pool.release(conn));
        }, executor);
    }

    @SuppressWarnings("unchecked")
    default <R> CompletableFuture<R> eval(LuaScript script, ScriptOutputType type, K... keys) {
        return apply(redis -> {
            return redis.<R>evalsha(script.sha1(), type, keys).<CompletionStage<R>>handle((r, e) -> {
                if (e != null) {
                    if (e instanceof RedisNoScriptException) {
                        return redis.<R>eval(script.script(), type, keys);
                    }
                    return CompletableFuture.<R>failedStage(e);
                }
                return CompletableFuture.<R>completedStage(r);
            }).<R>thenCompose(r -> r);
        });
    }

    @SuppressWarnings("unchecked")
    default <R> CompletableFuture<R> evalAsync(Executor executor, LuaScript script, ScriptOutputType type, K... keys) {
        return applyAsync(redis -> {
            return redis.<R>evalsha(script.sha1(), type, keys).<CompletionStage<R>>handle((r, e) -> {
                if (e != null) {
                    if (e instanceof RedisNoScriptException) {
                        return redis.<R>eval(script.script(), type, keys);
                    }
                    return CompletableFuture.<R>failedStage(e);
                }
                return CompletableFuture.<R>completedStage(r);
            }).<R>thenCompose(r -> r);
        }, executor);
    }

    @SuppressWarnings("unchecked")
    default <R> CompletableFuture<R> eval(LuaScript script, ScriptOutputType type, K[] keys, V... values) {
        return apply(redis -> {
            return redis.<R>evalsha(script.sha1(), type, keys, values).<CompletionStage<R>>handle((r, e) -> {
                if (e != null) {
                    if (e instanceof RedisNoScriptException) {
                        return redis.<R>eval(script.script(), type, keys, values);
                    }
                    return CompletableFuture.<R>failedStage(e);
                }
                return CompletableFuture.<R>completedStage(r);
            }).<R>thenCompose(r -> r);
        });
    }

    @SuppressWarnings("unchecked")
    default <R> CompletableFuture<R> evalAsync(Executor executor, LuaScript script, ScriptOutputType type, K[] keys,
            V... values) {
        return applyAsync(redis -> {
            return redis.<R>evalsha(script.sha1(), type, keys, values).<CompletionStage<R>>handle((r, e) -> {
                if (e != null) {
                    if (e instanceof RedisNoScriptException) {
                        return redis.<R>eval(script.script(), type, keys, values);
                    }
                    return CompletableFuture.<R>failedStage(e);
                }
                return CompletableFuture.<R>completedStage(r);
            }).<R>thenCompose(r -> r);
        }, executor);
    }

}
