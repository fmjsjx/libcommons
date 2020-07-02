package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.kafka;

import java.util.Optional;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
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

import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.kafka.KafkaProperties.ConsumerProperties;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.kafka.KafkaProperties.ProducerProperties;
import com.github.fmjsjx.libcommons.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@ConditionalOnClass({ KafkaProducer.class, KafkaConsumer.class })
public class KafkaAutoConfiguration {

    @Bean
    public static KafkaRegisteryProcessor kafkaRegisteryProcessor() {
        return new KafkaRegisteryProcessor();
    }

    public static class KafkaRegisteryProcessor implements EnvironmentAware, BeanDefinitionRegistryPostProcessor {
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
            var bindResult = Binder.get(environment).bind("libcommons.kafka", KafkaProperties.class);
            if (bindResult.isBound()) {
                var onsProps = bindResult.get();
                onsProps.getProducers().forEach(this::registerProducer);
                onsProps.getConsumers().forEach(this::registerConsumer);
            }
        }

        private void registerProducer(String name, ProducerProperties config) {
            Properties properties = Optional.ofNullable(config.getConfigs()).orElseGet(Properties::new);
            properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, config.getKeySerializer().getName());
            properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, config.getValueSerializer().getName());
            if (config.getAcks() != null) {
                properties.setProperty(ProducerConfig.ACKS_CONFIG, config.getAcks());
            }
            properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
            if (config.getBufferMemory() != null) {
                properties.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG,
                        String.valueOf(config.getBufferMemory().toBytes()));
            }
            if (config.getCompressionType() != null) {
                properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, config.getCompressionType().getValue());
            }
            if (config.getRetries() != null) {
                properties.setProperty(ProducerConfig.RETRIES_CONFIG, config.getRetries().toString());
            }
            var beanName = name + "KafkaProducer";
            @SuppressWarnings("rawtypes")
            var beanDefinition = BeanDefinitionBuilder
                    .genericBeanDefinition(KafkaProducer.class, () -> new KafkaProducer(properties))
                    .setDestroyMethodName("close").setScope(BeanDefinition.SCOPE_SINGLETON).getBeanDefinition();
            log.debug("Register Kafka Producer {}: {}", beanName, beanDefinition);
            registry.registerBeanDefinition(beanName, beanDefinition);
        }

        private void registerConsumer(String name, ConsumerProperties config) {
            Properties properties = Optional.ofNullable(config.getConfigs()).orElseGet(Properties::new);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, config.getKeyDeserializer().getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    config.getValueDeserializer().getName());
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, config.getGroupId());
            if (StringUtil.isNotEmpty(config.getGroupInstanceId())) {
                properties.setProperty(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, config.getGroupInstanceId());
            }
            if (config.getFetchMinSize() != null) {
                properties.setProperty(ConsumerConfig.FETCH_MIN_BYTES_CONFIG,
                        String.valueOf(config.getFetchMinSize().toBytes()));
            }
            if (config.getFetchMaxSize() != null) {
                properties.setProperty(ConsumerConfig.FETCH_MAX_BYTES_CONFIG,
                        String.valueOf(config.getFetchMaxSize().toBytes()));
            }
            if (config.getHeartbeatInterval() != null) {
                properties.setProperty(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG,
                        String.valueOf(config.getHeartbeatInterval().toMillis()));
            }
            if (config.getSessionTimeout() != null) {
                properties.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,
                        String.valueOf(config.getSessionTimeout().toMillis()));
            }
            if (config.getAutoOffsetReset() != null) {
                properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, config.getAutoOffsetReset().getValue());
            }
            if (config.getMaxPartitionFetchSize() != null) {
                properties.setProperty(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,
                        String.valueOf(config.getMaxPartitionFetchSize().toBytes()));
            }
            if (config.getEnableAutoCommit() != null) {
                properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                        config.getEnableAutoCommit().toString());
            }
            var beanName = name + "KafkaConsumer";
            @SuppressWarnings("rawtypes")
            var beanDefinition = BeanDefinitionBuilder
                    .genericBeanDefinition(KafkaConsumer.class, () -> new KafkaConsumer(properties))
                    .setDestroyMethodName("close").setScope(BeanDefinition.SCOPE_SINGLETON).getBeanDefinition();
            log.debug("Register Kafka Consumer {}: {}", beanName, beanDefinition);
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }
}
