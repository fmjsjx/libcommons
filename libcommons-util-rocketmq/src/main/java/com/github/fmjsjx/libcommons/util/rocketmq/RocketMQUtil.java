package com.github.fmjsjx.libcommons.util.rocketmq;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RocketMQUtil {

    private static final int defaultRetryTimes = 3;

    public static final SendResult send(MQProducer producer, Message msg)
            throws MQClientException, TooManyRetryException {
        return send(producer, msg, defaultRetryTimes);
    }

    public static final SendResult send(MQProducer producer, Message msg, int retryTimes)
            throws MQClientException, TooManyRetryException {
        try {
            return producer.send(msg);
        } catch (RemotingException | MQBrokerException | InterruptedException e) {
            log.warn("Send message failed, start retry stage: {}", msg, e);
            // retry stage
            List<Throwable> causes = new ArrayList<>();
            for (var remainingTimes = Math.max(0, retryTimes); remainingTimes > 0; remainingTimes--) {
                try {
                    return producer.send(msg);
                } catch (RemotingException | MQBrokerException | InterruptedException cause) {
                    causes.add(cause);
                }
            }
            throw new TooManyRetryException("send message failed", e, causes);
        }
    }

    public static final CompletableFuture<SendResult> sendAsync(MQProducer producer, Message msg) {
        return sendAsync(producer, msg, defaultRetryTimes);
    }

    public static final CompletableFuture<SendResult> sendAsync(MQProducer producer, Message msg, int retryTimes) {
        var future = new SendFuture(producer, msg, retryTimes);
        try {
            producer.send(msg, future);
            return future;
        } catch (MQClientException | RemotingException | InterruptedException e) {
            future.onException(e);
            return future;
        }
    }

    private static final class SendFuture extends CompletableFuture<SendResult> implements SendCallback {

        private final MQProducer producer;
        private final Message msg;
        private final AtomicInteger retryTimes;
        private final AtomicReference<Throwable> causeRef = new AtomicReference<>();
        private final AtomicReference<List<Throwable>> causesRef = new AtomicReference<>();

        public SendFuture(MQProducer producer, Message msg, int retryTimes) {
            this.producer = producer;
            this.msg = msg;
            this.retryTimes = new AtomicInteger(retryTimes);
        }

        @Override
        public void onSuccess(SendResult sendResult) {
            complete(sendResult);
        }

        @Override
        public void onException(Throwable e) {
            if (e instanceof MQClientException) {
                completeExceptionally(e);
                return;
            }
            if (causeRef.compareAndSet(null, e)) {
                causesRef.set(new ArrayList<>());
            } else {
                causesRef.get().add(e);
            }
            var remaining = retryTimes.getAndDecrement();
            if (remaining > 0) {
                try {
                    producer.send(msg, this);
                } catch (MQClientException | RemotingException | InterruptedException cause) {
                    onException(cause);
                }
            } else {
                completeExceptionally(
                        new TooManyRetryException("send message async failed", causeRef.get(), causesRef.get()));
            }
        }

    }

}
