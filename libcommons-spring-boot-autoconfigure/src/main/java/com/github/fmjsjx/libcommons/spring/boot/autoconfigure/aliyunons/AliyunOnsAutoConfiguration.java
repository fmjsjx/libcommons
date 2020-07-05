package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.aliyunons;

import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;
import java.util.Properties;

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

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PullConsumer;
import com.aliyun.openservices.ons.api.batch.BatchConsumer;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.aliyunons.AliyunOnsProperties.ConfigProperties;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.aliyunons.AliyunOnsProperties.ConsumerProperties;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.aliyunons.AliyunOnsProperties.ProducerProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnClass(ONSFactory.class)
@EnableConfigurationProperties(AliyunOnsProperties.class)
public class AliyunOnsAutoConfiguration {

    @Bean
    public static OnsRegisteryProcessor onsRegisteryProcessor() {
        return new OnsRegisteryProcessor();
    }

    public static class OnsRegisteryProcessor implements EnvironmentAware, BeanDefinitionRegistryPostProcessor {

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
            var bindResult = Binder.get(environment).bind("libcommons.aliyun-ons", AliyunOnsProperties.class);
            if (bindResult.isBound()) {
                var onsProps = bindResult.get();
                onsProps.getProducers().forEach(this::registerProducer);
                onsProps.getConsumers().forEach(this::registerConsumer);
            }
        }

        private void registerProducer(String name, ProducerProperties config) {
            Properties properties = new Properties();
            setBaseProperties(config, properties);
            if (config.getSendMsgTimeout() != null) {
                properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis,
                        String.valueOf(config.getSendMsgTimeout().toMillis()));
            }
            if (config.getCheckImmunityTime() != null) {
                properties.setProperty(PropertyKeyConst.CheckImmunityTimeInSeconds,
                        String.valueOf(config.getCheckImmunityTime().toSeconds()));
            }
            switch (config.getType()) {
            default:
            case NORMAL: {
                var beanName = name + "Producer";
                var beanDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(Producer.class, () -> ONSFactory.createProducer(properties))
                        .setScope(BeanDefinition.SCOPE_SINGLETON).setInitMethodName("start")
                        .setDestroyMethodName("shutdown").getBeanDefinition();
                log.debug("Register Aliyun Ons Producer {}: {}", beanName, beanDefinition);
                registry.registerBeanDefinition(beanName, beanDefinition);
            }
                break;
            case ORDER: {
                var beanName = name + "OrderProducer";
                var beanDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(OrderProducer.class, () -> ONSFactory.createOrderProducer(properties))
                        .setScope(BeanDefinition.SCOPE_SINGLETON).setInitMethodName("start")
                        .setDestroyMethodName("shutdown").getBeanDefinition();
                log.debug("Register Aliyun Ons OrderProducer {}: {}", beanName, beanDefinition);
                registry.registerBeanDefinition(beanName, beanDefinition);
            }
                break;
            case TRANSACTION: {
                if (config.getTransactionCheckerClass() == null) {
                    throw new NoSuchElementException("libcommons.aliyun-ons." + name + ".transaction-checker-class");
                }
                Class<? extends LocalTransactionChecker> transactionCheckerClass = config.getTransactionCheckerClass();
                var beanName = name + "TransactionProducer";
                var beanDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(TransactionProducer.class,
                                () -> ONSFactory.createTransactionProducer(properties,
                                        newInstance(transactionCheckerClass)))
                        .setScope(BeanDefinition.SCOPE_SINGLETON).setInitMethodName("start")
                        .setDestroyMethodName("shutdown").getBeanDefinition();
                log.debug("Register Aliyun Ons TransactionProducer {}: {}", beanName, beanDefinition);
                registry.registerBeanDefinition(beanName, beanDefinition);
            }
                break;
            }

        }

        private <T> T newInstance(Class<? extends T> clazz) {
            try {
                return (T) clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }

        private void setBaseProperties(ConfigProperties config, Properties properties) {
            properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, config.getNamesrvAddr());
            properties.setProperty(PropertyKeyConst.AccessKey, config.getAccessKey());
            properties.setProperty(PropertyKeyConst.SecretKey, config.getSecretKey());
            if (config.getSecretToken() != null) {
                properties.setProperty(PropertyKeyConst.SecurityToken, config.getSecretToken());
            }
            if (config.getOnsChannel() != null) {
                properties.setProperty(PropertyKeyConst.OnsChannel, config.getOnsChannel().name());
            }
            if (config.getMqType() != null) {
                properties.setProperty(PropertyKeyConst.MQType, config.getMqType().name());
            }
            if (config.getGroupId() != null) {
                properties.setProperty(PropertyKeyConst.GROUP_ID, config.getGroupId());
            }
        }

        private void registerConsumer(String name, ConsumerProperties config) {
            Properties properties = new Properties();
            setBaseProperties(config, properties);
            if (config.getMessageModel() != null) {
                properties.setProperty(PropertyKeyConst.MessageModel, config.getMessageModel().name());
            }
            if (config.getConsumeThreadNums() != null) {
                properties.setProperty(PropertyKeyConst.ConsumeThreadNums, config.getConsumeThreadNums().toString());
            }
            if (config.getMaxReconsumeTimes() != null) {
                properties.setProperty(PropertyKeyConst.MaxReconsumeTimes, config.getMaxReconsumeTimes().toString());
            }
            if (config.getConsumeTimeout() != null) {
                properties.setProperty(PropertyKeyConst.ConsumeTimeout,
                        String.valueOf(config.getConsumeTimeout().toMinutes()));
            }
            if (config.getSuspendTime() != null) {
                properties.setProperty(PropertyKeyConst.SuspendTimeMillis,
                        String.valueOf(config.getSuspendTime().toMillis()));
            }
            if (config.getMaxCachedMessageAmount() != null) {
                properties.setProperty(PropertyKeyConst.MaxCachedMessageAmount,
                        config.getMaxCachedMessageAmount().toString());
            }
            if (config.getMaxCachedMessageSize() != null) {
                var mib = Math.min(2048, Math.max(16, config.getMaxCachedMessageSize().toMegabytes()));
                properties.setProperty(PropertyKeyConst.MaxCachedMessageSizeInMiB, String.valueOf(mib));
            }
            switch (config.getType()) {
            default:
            case NORMAL: {
                var beanName = name + "Consumer";
                var beanDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(Consumer.class, () -> ONSFactory.createConsumer(properties))
                        .setScope(BeanDefinition.SCOPE_SINGLETON).setDestroyMethodName("shutdown").getBeanDefinition();
                log.debug("Register Aliyun Ons Consumer {}: {}", beanName, beanDefinition);
                registry.registerBeanDefinition(beanName, beanDefinition);
            }
                break;
            case BATCH: {
                var consumeMessageBatchMaxSize = 32;
                if (config.getConsumeMessageBatchMaxSize() != null) {
                    var size = config.getConsumeMessageBatchMaxSize().intValue();
                    consumeMessageBatchMaxSize = Math.max(Math.min(32, size), 1);
                }
                properties.setProperty(PropertyKeyConst.ConsumeMessageBatchMaxSize,
                        String.valueOf(consumeMessageBatchMaxSize));
                var beanName = name + "BatchConsumer";
                var beanDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(BatchConsumer.class, () -> ONSFactory.createBatchConsumer(properties))
                        .setScope(BeanDefinition.SCOPE_SINGLETON).setDestroyMethodName("shutdown").getBeanDefinition();
                log.debug("Register Aliyun Ons BatchConsumer {}: {}", beanName, beanDefinition);
                registry.registerBeanDefinition(beanName, beanDefinition);
            }
                break;
            case ORDERED: {
                var beanName = name + "OrderConsumer";
                var beanDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(OrderConsumer.class, () -> ONSFactory.createOrderedConsumer(properties))
                        .setScope(BeanDefinition.SCOPE_SINGLETON).setDestroyMethodName("shutdown").getBeanDefinition();
                log.debug("Register Aliyun Ons OrderConsumer {}: {}", beanName, beanDefinition);
                registry.registerBeanDefinition(beanName, beanDefinition);
            }
                break;
            case PULL: {
                if (config.getAutoCommit() != null) {
                    properties.setProperty(PropertyKeyConst.AUTO_COMMIT, config.getAutoCommit().toString());
                }
                if (config.getAutoCommitInterval() != null) {
                    properties.setProperty(PropertyKeyConst.AUTO_COMMIT_INTERVAL_MILLIS,
                            String.valueOf(config.getAutoCommitInterval().toMillis()));
                }
                if (config.getPollTimeout() != null) {
                    properties.setProperty(PropertyKeyConst.POLL_TIMEOUT_MILLIS,
                            String.valueOf(config.getPollTimeout().toMillis()));
                }
                var beanName = name + "PullConsumer";
                var beanDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(PullConsumer.class, () -> ONSFactory.createPullConsumer(properties))
                        .setScope(BeanDefinition.SCOPE_SINGLETON).setDestroyMethodName("shutdown").getBeanDefinition();
                log.debug("Register Aliyun Ons PullConsumer {}: {}", beanName, beanDefinition);
                registry.registerBeanDefinition(beanName, beanDefinition);
            }
                break;
            }
        }

    }

}
