package com.github.fmjsjx.libcommons.util.aliyunons;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.order.OrderProducer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OnsClientUtil {

    private static final int defaultRetryTimes = 3;

    public static final SendResult send(Producer producer, Message message) throws TooManyRetryException {
        return send(producer, message, defaultRetryTimes);
    }

    public static final SendResult send(Producer producer, Message message, int retryTimes)
            throws TooManyRetryException {
        try {
            return producer.send(message);
        } catch (Throwable e) {
            log.warn("Send message failed, start retry stage: {}", message, e);
            // retry stage
            List<Throwable> causes = new ArrayList<>();
            for (var remainingTimes = Math.max(0, retryTimes); remainingTimes > 0; remainingTimes--) {
                try {
                    return producer.send(message);
                } catch (Throwable cause) {
                    causes.add(cause);
                }
            }
            throw new TooManyRetryException("send message failed", e, causes);
        }
    }

    public static final SendResult send(OrderProducer producer, Message message, String shardingKey)
            throws TooManyRetryException {
        return send(producer, message, shardingKey, defaultRetryTimes);
    }

    public static final SendResult send(OrderProducer producer, Message message, String shardingKey, int retryTimes)
            throws TooManyRetryException {
        try {
            return producer.send(message, shardingKey);
        } catch (Throwable e) {
            log.warn("Send order message failed, start retry stage: {}", message, e);
            // retry stage
            List<Throwable> causes = new ArrayList<>();
            for (var remainingTimes = Math.max(0, retryTimes); remainingTimes > 0; remainingTimes--) {
                try {
                    return producer.send(message, shardingKey);
                } catch (Throwable cause) {
                    causes.add(cause);
                }
            }
            throw new TooManyRetryException("send order message failed", e, causes);
        }
    }

    public static final CompletableFuture<SendResult> sendAsync(Producer producer, Message message) {
        return sendAsync(producer, message, defaultRetryTimes);
    }

    public static final CompletableFuture<SendResult> sendAsync(Producer producer, Message message, int retryTimes) {
        var future = new SendFuture(producer, message, retryTimes);
        producer.sendAsync(message, future);
        return future;
    }

    private static final class SendFuture extends CompletableFuture<SendResult> implements SendCallback {

        private final Producer producer;
        private final Message message;
        private final AtomicInteger retryTimes;
        private final AtomicReference<Throwable> causeRef = new AtomicReference<>();
        private final AtomicReference<List<Throwable>> causesRef = new AtomicReference<>();

        public SendFuture(Producer producer, Message message, int retryTimes) {
            this.producer = producer;
            this.message = message;
            this.retryTimes = new AtomicInteger(retryTimes);
        }

        @Override
        public void onSuccess(SendResult sendResult) {
            complete(sendResult);
        }

        @Override
        public void onException(OnExceptionContext context) {
            var e = context.getException();
            if (causeRef.compareAndSet(null, e)) {
                causesRef.set(new ArrayList<>());
            } else {
                causesRef.get().add(e);
            }
            var remaining = retryTimes.getAndDecrement();
            if (remaining > 0) {
                producer.sendAsync(message, this);
            } else {
                completeExceptionally(
                        new TooManyRetryException("send message async failed", causeRef.get(), causesRef.get()));
            }
        }

    }

}
