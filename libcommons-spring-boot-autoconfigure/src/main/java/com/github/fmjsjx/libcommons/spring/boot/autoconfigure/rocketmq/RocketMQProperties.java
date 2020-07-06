package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.rocketmq;

import java.time.Duration;
import java.util.List;

import org.apache.rocketmq.client.AccessChannel;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.util.unit.DataSize;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ConfigurationProperties("libcommons.rocketmq")
public class RocketMQProperties {

    private List<ProducerProperties> producers;

    private List<ConsumerProperties> consumers;

    public enum ProducedrType {
        /**
         * DefaultMQProducer.
         */
        DEFAULT,
        /**
         * TransactionMQProducer.
         */
        TRANSACTION
    }

    public enum ConsumerType {
        /**
         * MQPushConsumer.
         */
        PUSH,
        /**
         * LitePullConsumer.
         */
        LITE_PULL;
    }

    interface ConfigProperties {

        String getName();

        String getBeanName();

        String getNamesrvAddr();

        String getAccessKey();

        String getSecretKey();

        String getSecretToken();

        AccessChannel getAccessChannel();

        String getGroupId();

    }

    @Getter
    @Setter
    @ToString
    public static class ProducerProperties implements ConfigProperties {

        @NonNull
        private String name;
        /**
         * The default is <code>"${name}RocketMQProducer"</code>.
         */
        private String beanName;

        @NonNull
        private String namesrvAddr;

        private String accessKey;

        private String secretKey;

        private String secretToken;

        private AccessChannel accessChannel;

        private ProducedrType type = ProducedrType.DEFAULT;

        private Duration sendMsgTimeout;

        private String namespace;

        private String groupId;

        /**
         * The default is {@code 4KB}.
         */
        private DataSize compressMsgBodyOverHowmuch;
        /**
         * The default is {@code 2}.
         */
        private Integer retryTimesWhenSendFailed;
        /**
         * The default is {@code false}.
         */
        private Boolean retryAnotherBrokerWhenNotStoreOK;
        /**
         * The default is {@code 4MB}.
         */
        private DataSize maxMessageSize;
    }

    @Getter
    @Setter
    @ToString
    public static class ConsumerProperties implements ConfigProperties {

        @NonNull
        private String name;
        /**
         * The default is <code>"${name}RocketMQConsumer"</code>.
         */
        private String beanName;

        @NonNull
        private String namesrvAddr;

        private String accessKey;

        private String secretKey;

        private String secretToken;

        private AccessChannel accessChannel;

        private ConsumerType type = ConsumerType.PUSH;

        private String namespace;

        @NonNull
        private String groupId;

        private MessageModel messageModel;
        /**
         * The default is {@code 20}.
         */
        private Integer consumeThreadMin;

        /**
         * The default is {@code 20}.
         */
        private Integer consumeThreadMax;
        /**
         * The default is {@code 1}.
         */
        private Integer consumeMessageBatchMaxSize;

        private Integer maxReconsumeTimes;

        private Duration consumeTimeout;

        private Duration suspendTime;

        private ConsumeFromWhere consumeFromWhere;

        /**
         * <p>
         * Only works on LitePullConsumer.
         * <p>
         * The default is {@code true}.
         */
        private Boolean autoCommit;
        /**
         * <p>
         * Only works on LitePullConsumer.
         * <p>
         * The default is {@code 5S}.
         */
        private Duration autoCommitInterval;
        /**
         * <p>
         * Only works on LitePullConsumer.
         * <p>
         * The default is {@code 20}.
         */
        private Integer pullThreadNums;
        /**
         * <p>
         * Only works on LitePullConsumer.
         * <p>
         * The default is {@code 10}.
         */
        private Integer pullBatchSize;
        /**
         * <p>
         * Only works on LitePullConsumer.
         * <p>
         * The default is {@code 5S}.
         */
        private Duration pollTimeout;

    }

}
