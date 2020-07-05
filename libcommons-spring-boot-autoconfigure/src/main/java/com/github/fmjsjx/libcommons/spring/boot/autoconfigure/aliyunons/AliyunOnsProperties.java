package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.aliyunons;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.util.unit.DataSize;

import com.aliyun.openservices.ons.api.MQType;
import com.aliyun.openservices.ons.api.impl.rocketmq.ONSChannel;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ConfigurationProperties("libcommons.aliyun-ons")
public class AliyunOnsProperties {

    private Map<String, ProducerProperties> producers = new LinkedHashMap<>();

    private Map<String, ConsumerProperties> consumers = new LinkedHashMap<>();

    public enum ProducerType {
        NORMAL, ORDER, TRANSACTION
    }

    public enum ConsumerType {
        NORMAL, BATCH, ORDERED, PULL
    }

    interface ConfigProperties {

        String getNamesrvAddr();

        String getAccessKey();

        String getSecretKey();

        String getSecretToken();

        ONSChannel getOnsChannel();

        String getGroupId();

        MQType getMqType();

    }

    @Getter
    @Setter
    @ToString
    public static class ProducerProperties implements ConfigProperties {

        @NonNull
        private String namesrvAddr;
        @NonNull
        private String accessKey;
        @NonNull
        private String secretKey;

        private String secretToken;

        private ONSChannel onsChannel;

        private MQType mqType;

        private ProducerType type = ProducerType.NORMAL;

        private String groupId;

        private Duration sendMsgTimeout;

        private Duration checkImmunityTime;

        private Class<? extends LocalTransactionChecker> transactionCheckerClass;

    }

    @Getter
    @Setter
    @ToString
    public static class ConsumerProperties implements ConfigProperties {

        @NonNull
        private String namesrvAddr;
        @NonNull
        private String accessKey;
        @NonNull
        private String secretKey;

        private String secretToken;

        private ONSChannel onsChannel;

        private MQType mqType;

        private ConsumerType type = ConsumerType.NORMAL;
        @NonNull
        private String groupId;

        private MessageModel messageModel;
        /**
         * The default is {@code 20}.
         */
        private Integer consumeThreadNums;
        /**
         * The default is {@code 16}.
         */
        private Integer maxReconsumeTimes;
        /**
         * The default is {@code 15M}.
         */
        private Duration consumeTimeout;
        /**
         * Only works on OrderedConsumer.
         */
        private Duration suspendTime;
        /**
         * The default is {@code 1000}.
         */
        private Integer maxCachedMessageAmount;
        /**
         * The default is {@code 512MB}. (For PullConsumer is {@code 100MB}.)
         */
        private DataSize maxCachedMessageSize;
        /**
         * The default is {@code 32}.
         */
        private Integer consumeMessageBatchMaxSize;
        /**
         * The default is {@code true}.
         */
        private Boolean autoCommit;
        /**
         * The default is {@code 5S}.
         */
        private Duration autoCommitInterval;
        /**
         * The default is {@code 5S}.
         */
        private Duration pollTimeout;

    }

}
