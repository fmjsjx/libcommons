package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.rocketmq;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.LitePullConsumer;
import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.MixAll;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.rocketmq.RocketMQProperties.ConsumerProperties;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.rocketmq.RocketMQProperties.ConsumerType;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.rocketmq.RocketMQProperties.ProducedrType;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.rocketmq.RocketMQProperties.ProducerProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnClass(AclClientRPCHook.class)
@EnableConfigurationProperties(RocketMQProperties.class)
public class RocketMQAutoConfiguration {

    @Bean
    public static RocketMQRegisteryProcessor onsRegisteryProcessor() {
        return new RocketMQRegisteryProcessor();
    }

    public static class RocketMQRegisteryProcessor implements EnvironmentAware, BeanDefinitionRegistryPostProcessor {

        private Environment environment;
        private BeanDefinitionRegistry registry;

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            // ignore
        }

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            this.registry = registry;
            var bindResult = Binder.get(environment).bind("douzi.rocketmq", RocketMQProperties.class);
            if (bindResult.isBound()) {
                var rocketmqProperties = bindResult.get();
                Optional.ofNullable(rocketmqProperties.getProducers())
                        .ifPresent(configs -> configs.forEach(this::registerProducer));
                Optional.ofNullable(rocketmqProperties.getConsumers())
                        .ifPresent(configs -> configs.forEach(this::registerConsumer));
            }
        }

        private void registerProducer(ProducerProperties config) {
            var name = config.getName();
            var beanName = Optional.ofNullable(config.getBeanName()).orElseGet(() -> name + "RocketMQProducer");
            if (config.getType() == ProducedrType.DEFAULT) {
                var beanDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(DefaultMQProducer.class, () -> createMQProducer(name, config))
                        .setScope(BeanDefinition.SCOPE_SINGLETON).setInitMethodName("start")
                        .setDestroyMethodName("shutdown").getBeanDefinition();
                log.debug("Register RocketMQ Producer {}: {}", beanName, beanDefinition);
                registry.registerBeanDefinition(beanName, beanDefinition);
            } else if (config.getType() == ProducedrType.TRANSACTION) {
                var beanDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(TransactionMQProducer.class,
                                () -> createTransactionMQProducer(name, config))
                        .setScope(BeanDefinition.SCOPE_SINGLETON).setDestroyMethodName("shutdown").getBeanDefinition();
                log.debug("Register RocketMQ Transaction Producer {}: {}", beanName, beanDefinition);
                registry.registerBeanDefinition(beanName, beanDefinition);
            }
        }

        private DefaultMQProducer createMQProducer(String name, ProducerProperties config) {
            DefaultMQProducer producer;
            if (config.getAccessKey() != null || config.getSecretKey() != null) {
                var accessKey = config.getAccessKey();
                var secretKey = config.getSecretKey();
                if (accessKey == null) {
                    throw new NoSuchElementException("libcommons.rocketmq." + name + ".access-key");
                }
                if (secretKey == null) {
                    throw new NoSuchElementException("libcommons.rocketmq." + name + ".secret-key");
                }
                if (config.getSecretToken() != null) {
                    if (config.getGroupId() != null) {
                        producer = new DefaultMQProducer(config.getGroupId(), new AclClientRPCHook(
                                new SessionCredentials(accessKey, secretKey, config.getSecretKey())));
                    } else {
                        producer = new DefaultMQProducer(new AclClientRPCHook(
                                new SessionCredentials(accessKey, secretKey, config.getSecretKey())));
                    }
                } else {
                    if (config.getGroupId() != null) {
                        producer = new DefaultMQProducer(config.getGroupId(), new AclClientRPCHook(
                                new SessionCredentials(accessKey, secretKey, config.getSecretKey())));
                    } else {
                        producer = new DefaultMQProducer(
                                new AclClientRPCHook(new SessionCredentials(accessKey, secretKey)));
                    }
                }
            } else {
                if (config.getGroupId() != null) {
                    producer = new DefaultMQProducer(config.getGroupId());
                } else {
                    producer = new DefaultMQProducer();
                }
            }
            setProducerConfigs(config, producer);
            return producer;
        }

        private void setProducerConfigs(ProducerProperties config, DefaultMQProducer producer) {
            producer.setNamesrvAddr(config.getNamesrvAddr());
            if (config.getNamespace() != null) {
                producer.setNamespace(config.getNamespace());
            }
            if (config.getAccessChannel() != null) {
                producer.setAccessChannel(config.getAccessChannel());
            }
            if (config.getSendMsgTimeout() != null) {
                producer.setSendMsgTimeout((int) config.getSendMsgTimeout().toMillis());
            }
            if (config.getCompressMsgBodyOverHowmuch() != null) {
                producer.setCompressMsgBodyOverHowmuch((int) config.getCompressMsgBodyOverHowmuch().toBytes());
            }
            if (config.getRetryTimesWhenSendFailed() != null) {
                producer.setRetryTimesWhenSendFailed(config.getRetryTimesWhenSendFailed());
                producer.setRetryTimesWhenSendAsyncFailed(config.getRetryTimesWhenSendFailed());
            }
            if (config.getRetryAnotherBrokerWhenNotStoreOK() != null) {
                producer.setRetryAnotherBrokerWhenNotStoreOK(config.getRetryAnotherBrokerWhenNotStoreOK());
            }
            if (config.getMaxMessageSize() != null) {
                producer.setMaxMessageSize((int) config.getMaxMessageSize().toBytes());
            }
        }

        private TransactionMQProducer createTransactionMQProducer(String name, ProducerProperties config) {
            TransactionMQProducer producer;
            var groupId = Optional.ofNullable(config.getGroupId()).orElse(MixAll.DEFAULT_PRODUCER_GROUP);
            if (config.getAccessKey() != null || config.getSecretKey() != null) {
                var accessKey = config.getAccessKey();
                var secretKey = config.getSecretKey();
                if (accessKey == null) {
                    throw new NoSuchElementException("libcommons.rocketmq." + name + ".access-key");
                }
                if (secretKey == null) {
                    throw new NoSuchElementException("libcommons.rocketmq." + name + ".secret-key");
                }
                if (config.getSecretToken() != null) {
                    producer = new TransactionMQProducer(groupId,
                            new AclClientRPCHook(new SessionCredentials(accessKey, secretKey, config.getSecretKey())));
                } else {
                    producer = new TransactionMQProducer(groupId,
                            new AclClientRPCHook(new SessionCredentials(accessKey, secretKey, config.getSecretKey())));
                }
            } else {
                producer = new TransactionMQProducer(groupId);
            }
            setProducerConfigs(config, producer);
            return producer;
        }

        private void registerConsumer(ConsumerProperties config) {
            var name = config.getName();
            var beanName = Optional.ofNullable(config.getBeanName()).orElseGet(() -> name + "RocketMQConsumer");
            if (config.getType() == ConsumerType.PUSH) {
                var beanDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(MQPushConsumer.class, () -> initPushConsumer(name, config))
                        .setScope(BeanDefinition.SCOPE_SINGLETON).setDestroyMethodName("shutdown").getBeanDefinition();
                log.debug("Register RocketMQ PushConsumer {}: {}", beanName, beanDefinition);
                registry.registerBeanDefinition(beanName, beanDefinition);
            } else if (config.getType() == ConsumerType.LITE_PULL) {
                var beanDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(LitePullConsumer.class, () -> initLitePullConsumer(name, config))
                        .setScope(BeanDefinition.SCOPE_SINGLETON).setDestroyMethodName("shutdown").getBeanDefinition();
                log.debug("Register RocketMQ LitePullConsumer {}: {}", beanName, beanDefinition);
                registry.registerBeanDefinition(beanName, beanDefinition);
            }
        }

        private MQPushConsumer initPushConsumer(String name, ConsumerProperties config) {
            DefaultMQPushConsumer consumer;
            if (config.getAccessKey() != null || config.getSecretKey() != null) {
                var accessKey = config.getAccessKey();
                var secretKey = config.getSecretKey();
                if (accessKey == null) {
                    throw new NoSuchElementException("libcommons.rocketmq." + name + ".access-key");
                }
                if (secretKey == null) {
                    throw new NoSuchElementException("libcommons.rocketmq." + name + ".secret-key");
                }
                if (config.getSecretToken() != null) {
                    consumer = new DefaultMQPushConsumer(
                            new AclClientRPCHook(new SessionCredentials(accessKey, secretKey, config.getSecretKey())));
                } else {
                    consumer = new DefaultMQPushConsumer(
                            new AclClientRPCHook(new SessionCredentials(accessKey, secretKey)));
                }
            } else {
                consumer = new DefaultMQPushConsumer();
            }
            consumer.setNamesrvAddr(config.getNamesrvAddr());
            if (config.getAccessChannel() != null) {
                consumer.setAccessChannel(config.getAccessChannel());
            }
            if (config.getNamespace() != null) {
                consumer.setNamespace(config.getNamespace());
            }
            consumer.setConsumerGroup(config.getGroupId());
            if (config.getMessageModel() != null) {
                consumer.setMessageModel(config.getMessageModel());
            }
            if (config.getConsumeThreadMin() != null) {
                consumer.setConsumeThreadMin(config.getConsumeThreadMin());
            }
            if (config.getConsumeThreadMax() != null) {
                consumer.setConsumeThreadMax(config.getConsumeThreadMax());
            }
            if (config.getMaxReconsumeTimes() != null) {
                consumer.setMaxReconsumeTimes(config.getMaxReconsumeTimes());
            }
            if (config.getConsumeTimeout() != null) {
                consumer.setConsumeTimeout(config.getConsumeTimeout().toMinutes());
            }
            if (config.getSuspendTime() != null) {
                consumer.setSuspendCurrentQueueTimeMillis(config.getSuspendTime().toMillis());
            }
            if (config.getConsumeFromWhere() != null) {
                consumer.setConsumeFromWhere(config.getConsumeFromWhere());
            }
            return consumer;
        }

        private LitePullConsumer initLitePullConsumer(String name, ConsumerProperties config) {
            DefaultLitePullConsumer consumer;
            if (config.getAccessKey() != null || config.getSecretKey() != null) {
                var accessKey = config.getAccessKey();
                var secretKey = config.getSecretKey();
                if (accessKey == null) {
                    throw new NoSuchElementException("libcommons.rocketmq." + name + ".access-key");
                }
                if (secretKey == null) {
                    throw new NoSuchElementException("libcommons.rocketmq." + name + ".secret-key");
                }
                if (config.getSecretToken() != null) {
                    consumer = new DefaultLitePullConsumer(config.getGroupId(),
                            new AclClientRPCHook(new SessionCredentials(accessKey, secretKey, config.getSecretKey())));
                } else {
                    consumer = new DefaultLitePullConsumer(config.getGroupId(),
                            new AclClientRPCHook(new SessionCredentials(accessKey, secretKey)));
                }
            } else {
                consumer = new DefaultLitePullConsumer(config.getGroupId());
            }
            consumer.setNamesrvAddr(config.getNamesrvAddr());
            if (config.getAccessChannel() != null) {
                consumer.setAccessChannel(config.getAccessChannel());
            }
            if (config.getNamespace() != null) {
                consumer.setNamespace(config.getNamespace());
            }
            if (config.getMessageModel() != null) {
                consumer.setMessageModel(config.getMessageModel());
            }
            if (config.getConsumeTimeout() != null) {
                consumer.setConsumerPullTimeoutMillis(config.getConsumeTimeout().toMinutes());
            }
            if (config.getSuspendTime() != null) {
                consumer.setConsumerTimeoutMillisWhenSuspend(config.getSuspendTime().toMillis());
            }
            if (config.getConsumeFromWhere() != null) {
                consumer.setConsumeFromWhere(config.getConsumeFromWhere());
            }
            if (config.getAutoCommit() != null) {
                config.setAutoCommit(config.getAutoCommit());
            }
            if (config.getAutoCommitInterval() != null) {
                consumer.setAutoCommitIntervalMillis(config.getAutoCommitInterval().toMillis());
            }
            if (config.getPullThreadNums() != null) {
                consumer.setPullThreadNums(config.getPullThreadNums());
            }
            if (config.getPullBatchSize() != null) {
                consumer.setPullBatchSize(config.getPullBatchSize());
            }
            if (config.getPollTimeout() != null) {
                consumer.setPollTimeoutMillis(config.getPollTimeout().toMillis());
            }
            return consumer;
        }

    }

}
