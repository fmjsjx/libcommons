package com.github.fmjsjx.libcommons.util.redis.lettuce;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.fmjsjx.libcommons.util.redis.LuaScript;

import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.RedisNoScriptException;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.output.KeyStreamingChannel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisUtil {

    @SuppressWarnings("unchecked")
    public static final <K, V, R> R eval(RedisCommands<K, V> redis, LuaScript script, ScriptOutputType type,
            K... keys) {
        try {
            return redis.evalsha(script.sha1(), type, keys);
        } catch (RedisNoScriptException e) {
            return redis.eval(script.script(), type, keys);
        }
    }

    @SuppressWarnings("unchecked")
    public static final <K, V, R> R eval(RedisCommands<K, V> redis, LuaScript script, ScriptOutputType type, K[] keys,
            V... values) {
        try {
            return redis.evalsha(script.sha1(), type, keys, values);
        } catch (RedisNoScriptException e) {
            return redis.eval(script.script(), type, keys, values);
        }
    }

    @SuppressWarnings("unchecked")
    public static final <K, V, R> CompletionStage<R> eval(RedisAsyncCommands<K, V> redis, LuaScript script,
            ScriptOutputType type, K... keys) {
        return redis.<R>evalsha(script.sha1(), type, keys).<CompletionStage<R>>handle((r, e) -> {
            if (e != null) {
                if (e instanceof RedisNoScriptException) {
                    return redis.<R>eval(script.script(), type, keys);
                }
                return CompletableFuture.failedStage(e);
            }
            return CompletableFuture.completedStage(r);
        }).thenCompose(r -> r);
    }

    @SuppressWarnings("unchecked")
    public static final <K, V, R> CompletionStage<R> eval(RedisAsyncCommands<K, V> redis, LuaScript script,
            ScriptOutputType type, K[] keys, V... values) {
        return redis.<R>evalsha(script.sha1(), type, keys, values).<CompletionStage<R>>handle((r, e) -> {
            if (e != null) {
                if (e instanceof RedisNoScriptException) {
                    return redis.<R>eval(script.script(), type, keys, values);
                }
                return CompletableFuture.failedStage(e);
            }
            return CompletableFuture.completedStage(r);
        }).thenCompose(r -> r);
    }

    public static final <K, V> void scanForEach(RedisCommands<K, V> redis, Consumer<K> action) {
        scanForEach(redis, null, action);
    }

    public static final <K, V> void scanForEach(RedisCommands<K, V> redis, ScanArgs scanArgs, Consumer<K> action) {
        scanForEach0(redis, scanArgs, action, false);
    }

    static final <K, V> void scanForEach0(RedisCommands<K, V> redis, ScanArgs scanArgs, Consumer<K> action,
            boolean useStreamChannel) {
        Objects.requireNonNull(action, "action must not be null");
        if (useStreamChannel) {
            KeyStreamingChannel<K> channel = action::accept;
            var cursor = redis.scan(channel, scanArgs);
            for (; !cursor.isFinished();) {
                cursor = redis.scan(channel, cursor, scanArgs);
            }
        } else {
            var cursor = redis.scan(scanArgs);
            for (;;) {
                for (K key : cursor.getKeys()) {
                    action.accept(key);
                }
                if (cursor.isFinished()) {
                    break;
                }
                cursor = redis.scan(cursor, scanArgs);
            }
        }
    }

    public static final <K, V> void hscanForEach(RedisCommands<K, V> redis, K key, BiConsumer<K, V> action) {
        hscanForEach(redis, key, null, action);
    }

    public static final <K, V> void hscanForEach(RedisCommands<K, V> redis, K key, ScanArgs scanArgs,
            BiConsumer<K, V> action) {
        hscanForEach0(redis, key, scanArgs, action, false);
    }

    static final <K, V> void hscanForEach0(RedisCommands<K, V> redis, K key, ScanArgs scanArgs, BiConsumer<K, V> action,
            boolean useStreamChannel) {
        if (useStreamChannel) {
            var cursor = redis.hscan(action::accept, key, scanArgs);
            for (; !cursor.isFinished();) {
                cursor = redis.hscan(action::accept, key, cursor, scanArgs);
            }
        } else {
            var cursor = redis.hscan(key, scanArgs);
            for (;;) {
                cursor.getMap().forEach(action);
                if (cursor.isFinished()) {
                    break;
                }
                cursor = redis.hscan(key, cursor, scanArgs);
            }
        }
    }

    public static final <K, V> Map<K, V> hscanGetAll(RedisCommands<K, V> redis, K key) {
        return hscanGetAll(redis, key, LinkedHashMap::new);
    }

    public static final <K, V> Map<K, V> hscanGetAll(RedisCommands<K, V> redis, K key,
            Supplier<Map<K, V>> hashFactory) {
        var hash = hashFactory.get();
        hscanForEach0(redis, key, null, hash::put, true);
        return hash;
    }

    public static final <K, V> Map<K, V> hscanGetAll(RedisCommands<K, V> redis, K key, ScanArgs scanArgs) {
        return hscanGetAll(redis, key, scanArgs, LinkedHashMap::new);
    }

    public static final <K, V> Map<K, V> hscanGetAll(RedisCommands<K, V> redis, K key, ScanArgs scanArgs,
            Supplier<Map<K, V>> hashFactory) {
        var hash = hashFactory.get();
        hscanForEach0(redis, key, scanArgs, hash::put, true);
        return hash;
    }

    public static final <K, V> CompletionStage<Void> scanForEach(RedisAsyncCommands<K, V> redis,
            BiConsumer<RedisAsyncCommands<K, V>, K> action) {
        return scanForEach(redis, null, null, action);
    }

    public static final <K, V> CompletionStage<Void> scanForEach(RedisAsyncCommands<K, V> redis, ScanArgs scanArgs,
            BiConsumer<RedisAsyncCommands<K, V>, K> action) {
        return scanForEach(redis, null, scanArgs, action);
    }

    public static final <K, V> CompletionStage<Void> scanForEach(RedisAsyncCommands<K, V> redis, ScanCursor scanCursor,
            ScanArgs scanArgs, BiConsumer<RedisAsyncCommands<K, V>, K> action) {
        Objects.requireNonNull(action, "action must not be null");
        if (scanCursor == null) {
            return redis.scan(scanArgs).thenApply(cursor -> {
                doActionForEachKey(redis, action, cursor);
                return cursor;
            }).thenCompose(cursor -> scanForEach(redis, cursor, scanArgs, action));
        }
        if (scanCursor.isFinished()) {
            return CompletableFuture.completedStage(null);
        }
        return redis.scan(scanCursor).thenApply(cursor -> {
            doActionForEachKey(redis, action, cursor);
            return cursor;
        }).thenCompose(cursor -> scanForEach(redis, cursor, scanArgs, action));
    }

    private static final <K, V> void doActionForEachKey(RedisAsyncCommands<K, V> redis,
            BiConsumer<RedisAsyncCommands<K, V>, K> action, KeyScanCursor<K> cursor) {
        for (K key : cursor.getKeys()) {
            action.accept(redis, key);
        }
    }

    public static final <K, V> CompletionStage<Void> scanForEachAsync(RedisAsyncCommands<K, V> redis,
            BiConsumer<RedisAsyncCommands<K, V>, K> action) {
        return scanForEachAsync(redis, null, null, action);
    }

    public static final <K, V> CompletionStage<Void> scanForEachAsync(RedisAsyncCommands<K, V> redis, ScanArgs scanArgs,
            BiConsumer<RedisAsyncCommands<K, V>, K> action) {
        return scanForEachAsync(redis, null, scanArgs, action);
    }

    public static final <K, V> CompletionStage<Void> scanForEachAsync(RedisAsyncCommands<K, V> redis,
            ScanCursor scanCursor, ScanArgs scanArgs, BiConsumer<RedisAsyncCommands<K, V>, K> action) {
        Objects.requireNonNull(action, "action must not be null");
        if (scanCursor == null) {
            return redis.scan(scanArgs).thenApplyAsync(cursor -> {
                doActionForEachKey(redis, action, cursor);
                return cursor;
            }).thenCompose(cursor -> scanForEachAsync(redis, cursor, scanArgs, action));
        }
        if (scanCursor.isFinished()) {
            return CompletableFuture.completedStage(null);
        }
        return redis.scan(scanCursor).thenApplyAsync(cursor -> {
            doActionForEachKey(redis, action, cursor);
            return cursor;
        }).thenCompose(cursor -> scanForEachAsync(redis, cursor, scanArgs, action));
    }

    public static final <K, V> CompletionStage<Void> scanForEachAsync(RedisAsyncCommands<K, V> redis,
            BiConsumer<RedisAsyncCommands<K, V>, K> action, Executor executor) {
        return scanForEachAsync(redis, null, null, action, executor);
    }

    public static final <K, V> CompletionStage<Void> scanForEachAsync(RedisAsyncCommands<K, V> redis, ScanArgs scanArgs,
            BiConsumer<RedisAsyncCommands<K, V>, K> action, Executor executor) {
        return scanForEachAsync(redis, null, scanArgs, action, executor);
    }

    public static final <K, V> CompletionStage<Void> scanForEachAsync(RedisAsyncCommands<K, V> redis,
            ScanCursor scanCursor, ScanArgs scanArgs, BiConsumer<RedisAsyncCommands<K, V>, K> action,
            Executor executor) {
        Objects.requireNonNull(action, "action must not be null");
        Objects.requireNonNull(executor, "executor must not be null");
        if (scanCursor == null) {
            return redis.scan(scanArgs).thenApplyAsync(cursor -> {
                doActionForEachKey(redis, action, cursor);
                return cursor;
            }, executor).thenCompose(cursor -> scanForEachAsync(redis, cursor, scanArgs, action, executor));
        }
        if (scanCursor.isFinished()) {
            return CompletableFuture.completedStage(null);
        }
        return redis.scan(scanCursor).thenApplyAsync(cursor -> {
            doActionForEachKey(redis, action, cursor);
            return cursor;
        }).thenCompose(cursor -> scanForEachAsync(redis, cursor, scanArgs, action, executor));
    }

    public static final <K, V> CompletionStage<Void> hscanForEach(RedisAsyncCommands<K, V> redis, K key,
            BiConsumer<K, V> action) {
        return hscanForEach(redis, key, null, null, action);
    }

    public static final <K, V> CompletionStage<Void> hscanForEach(RedisAsyncCommands<K, V> redis, K key,
            ScanArgs scanArgs, BiConsumer<K, V> action) {
        return hscanForEach(redis, key, null, scanArgs, action);
    }

    public static final <K, V> CompletionStage<Void> hscanForEach(RedisAsyncCommands<K, V> redis, K key,
            ScanCursor scanCursor, ScanArgs scanArgs, BiConsumer<K, V> action) {
        Objects.requireNonNull(action, "action must not be null");
        if (scanCursor == null) {
            return redis.hscan(action::accept, key, scanArgs)
                    .thenCompose(cursor -> hscanForEach(redis, key, cursor, scanArgs, action));
        }
        if (scanCursor.isFinished()) {
            return CompletableFuture.completedStage(null);
        }
        return redis.hscan(action::accept, key, scanCursor, scanArgs)
                .thenCompose(cursor -> hscanForEach(redis, key, cursor, scanArgs, action));
    }

    public static final <K, V> CompletionStage<Void> hscanForEachAsync(RedisAsyncCommands<K, V> redis, K key,
            BiConsumer<K, V> action) {
        return hscanForEachAsync(redis, key, null, null, action);
    }

    public static final <K, V> CompletionStage<Void> hscanForEachAsync(RedisAsyncCommands<K, V> redis, K key,
            ScanArgs scanArgs, BiConsumer<K, V> action) {
        return hscanForEachAsync(redis, key, null, scanArgs, action);
    }

    public static final <K, V> CompletionStage<Void> hscanForEachAsync(RedisAsyncCommands<K, V> redis, K key,
            ScanCursor scanCursor, ScanArgs scanArgs, BiConsumer<K, V> action) {
        Objects.requireNonNull(action, "action must not be null");
        if (scanCursor == null) {
            return redis.hscan(key, scanArgs).thenApplyAsync(cursor -> {
                cursor.getMap().forEach(action);
                return cursor;
            }).thenCompose(cursor -> hscanForEach(redis, key, cursor, scanArgs, action));
        }
        if (scanCursor.isFinished()) {
            return CompletableFuture.completedStage(null);
        }
        return redis.hscan(key, scanCursor, scanArgs).thenApplyAsync(cursor -> {
            cursor.getMap().forEach(action);
            return cursor;
        }).thenCompose(cursor -> hscanForEach(redis, key, cursor, scanArgs, action));
    }

    public static final <K, V> CompletionStage<Void> hscanForEachAsync(RedisAsyncCommands<K, V> redis, K key,
            BiConsumer<K, V> action, Executor executor) {
        return hscanForEachAsync(redis, key, null, null, action, executor);
    }

    public static final <K, V> CompletionStage<Void> hscanForEachAsync(RedisAsyncCommands<K, V> redis, K key,
            ScanArgs scanArgs, BiConsumer<K, V> action, Executor executor) {
        return hscanForEachAsync(redis, key, null, scanArgs, action, executor);
    }

    public static final <K, V> CompletionStage<Void> hscanForEachAsync(RedisAsyncCommands<K, V> redis, K key,
            ScanCursor scanCursor, ScanArgs scanArgs, BiConsumer<K, V> action, Executor executor) {
        Objects.requireNonNull(action, "action must not be null");
        Objects.requireNonNull(executor, "executor must not be null");
        if (scanCursor == null) {
            return redis.hscan(key, scanArgs).thenApplyAsync(cursor -> {
                cursor.getMap().forEach(action);
                return cursor;
            }, executor).thenCompose(cursor -> hscanForEach(redis, key, cursor, scanArgs, action));
        }
        if (scanCursor.isFinished()) {
            return CompletableFuture.completedStage(null);
        }
        return redis.hscan(key, scanCursor, scanArgs).thenApplyAsync(cursor -> {
            cursor.getMap().forEach(action);
            return cursor;
        }, executor).thenCompose(cursor -> hscanForEach(redis, key, cursor, scanArgs, action));
    }

    public static final <K, V> CompletionStage<Map<K, V>> hscanGetAll(RedisAsyncCommands<K, V> redis, K key) {
        return hscanGetAll(redis, key, null, null, LinkedHashMap::new);
    }

    public static final <K, V> CompletionStage<Map<K, V>> hscanGetAll(RedisAsyncCommands<K, V> redis, K key,
            ScanArgs scanArgs) {
        return hscanGetAll(redis, key, null, scanArgs, LinkedHashMap::new);
    }

    public static final <K, V> CompletionStage<Map<K, V>> hscanGetAll(RedisAsyncCommands<K, V> redis, K key,
            ScanCursor scanCursor, ScanArgs scanArgs) {
        return hscanGetAll(redis, key, scanCursor, scanArgs, LinkedHashMap::new);
    }

    public static final <K, V> CompletionStage<Map<K, V>> hscanGetAll(RedisAsyncCommands<K, V> redis, K key,
            Supplier<Map<K, V>> hashFactory) {
        return hscanGetAll(redis, key, null, null, hashFactory);
    }

    public static final <K, V> CompletionStage<Map<K, V>> hscanGetAll(RedisAsyncCommands<K, V> redis, K key,
            ScanArgs scanArgs, Supplier<Map<K, V>> hashFactory) {
        return hscanGetAll(redis, key, null, scanArgs, hashFactory);
    }

    public static final <K, V> CompletionStage<Map<K, V>> hscanGetAll(RedisAsyncCommands<K, V> redis, K key,
            ScanCursor scanCursor, ScanArgs scanArgs, Supplier<Map<K, V>> hashFactory) {
        var hash = hashFactory.get();
        if (scanCursor == null) {
            return redis.hscan(hash::put, key, scanArgs)
                    .thenCompose(cursor -> hscanGetAll(redis, key, cursor, scanArgs, () -> hash));
        }
        if (scanCursor.isFinished()) {
            return CompletableFuture.completedStage(hash);
        }
        return redis.hscan(hash::put, key, scanCursor, scanArgs)
                .thenCompose(cursor -> hscanGetAll(redis, key, cursor, scanArgs, () -> hash));
    }

}
