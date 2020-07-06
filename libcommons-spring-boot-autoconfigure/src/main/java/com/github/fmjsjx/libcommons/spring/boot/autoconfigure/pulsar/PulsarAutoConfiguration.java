package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.pulsar;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.pulsar.client.api.ClientBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.PulsarClientException.UnsupportedAuthenticationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
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

import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.pulsar.PulsarProperties.PulsarClientProperties;

@Configuration
@EnableConfigurationProperties(PulsarProperties.class)
@ConditionalOnClass(PulsarClient.class)
public class PulsarAutoConfiguration {

    @Bean
    public static PulsarRegisteryProcessor pulsarRegisteryProcessor() {
        return new PulsarRegisteryProcessor();
    }

    private static class PulsarRegisteryProcessor implements EnvironmentAware, BeanDefinitionRegistryPostProcessor {

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
            var bindResult = Binder.get(environment).bind("libcommons.pulsar", PulsarProperties.class);
            if (bindResult.isBound()) {
                var properties = bindResult.get();
                if (properties.getClients() != null) {
                    properties.getClients().forEach(this::registerClientBean);
                }
            }
        }

        private void registerClientBean(PulsarClientProperties config) {
            String beanName = Optional.ofNullable(config.getBeanName())
                    .orElseGet(() -> config.getName() + "PulsarClient");
            var builder = PulsarClient.builder();
            configClientBuilder(config, beanName, builder);
            Supplier<PulsarClient> beanFactory = () -> {
                try {
                    return builder.build();
                } catch (PulsarClientException e) {
                    throw unexpectedErrorOccurs(beanName, e);
                }
            };
            registry.registerBeanDefinition(beanName,
                    BeanDefinitionBuilder.genericBeanDefinition(PulsarClient.class, beanFactory)
                            .setDestroyMethodName("close").getBeanDefinition());
        }

        void configClientBuilder(PulsarClientProperties config, String beanName, ClientBuilder builder) {
            Optional.ofNullable(config.getServiceUrl()).ifPresent(builder::serviceUrl);
            Optional.ofNullable(config.getListenerName()).ifPresent(builder::listenerName);
            Optional.ofNullable(config.getAuthPluginClassName()).ifPresent(authPluginClassName -> {
                try {
                    if (config.getAuthParams() != null) {
                        builder.authentication(authPluginClassName, config.getAuthParams());
                    } else {
                        builder.authentication(authPluginClassName, config.getAuthParamsString());
                    }
                } catch (UnsupportedAuthenticationException e) {
                    throw unexpectedErrorOccurs(beanName, e);
                }
            });
            Optional.ofNullable(config.getOperationTimeout())
                    .ifPresent(ot -> builder.operationTimeout((int) ot.toMillis(), TimeUnit.MILLISECONDS));
            Optional.ofNullable(config.getStatsInterval())
                    .ifPresent(si -> builder.statsInterval(si.toSeconds(), TimeUnit.SECONDS));
            Optional.ofNullable(config.getNumIoThreads()).ifPresent(builder::ioThreads);
            Optional.ofNullable(config.getNumListenerThreads()).ifPresent(builder::listenerThreads);
            Optional.ofNullable(config.getConnectionsPerBroker()).ifPresent(builder::connectionsPerBroker);
            Optional.of(config.getUseTcpNoDelay()).ifPresent(builder::enableTcpNoDelay);
            Optional.of(config.getTlsTrustCertsFilePath()).ifPresent(builder::tlsTrustCertsFilePath);
            Optional.of(config.getTlsAllowInsecureConnection()).ifPresent(builder::allowTlsInsecureConnection);
            Optional.of(config.getTlsHostnameVerificationEnable()).ifPresent(builder::enableTlsHostnameVerification);
            Optional.of(config.getUseKeyStoreTls()).ifPresent(builder::useKeyStoreTls);
            Optional.of(config.getSslProvider()).ifPresent(builder::sslProvider);
            Optional.of(config.getTlsTrustStoreType()).ifPresent(builder::tlsTrustStoreType);
            Optional.of(config.getTlsTrustStorePath()).ifPresent(builder::tlsTrustStorePath);
            Optional.of(config.getTlsTrustStorePassword()).ifPresent(builder::tlsTrustStorePassword);
            Optional.of(config.getTlsCiphers()).filter(s -> s.size() > 0).ifPresent(builder::tlsCiphers);
            Optional.of(config.getTlsProtocols()).filter(s -> s.size() > 0).ifPresent(builder::tlsProtocols);
            Optional.of(config.getConcurrentLookupRequest()).ifPresent(builder::maxConcurrentLookupRequests);
            Optional.of(config.getMaxLookupRequest()).ifPresent(builder::maxLookupRequests);
            Optional.of(config.getMaxLookupRedirects()).ifPresent(builder::maxLookupRedirects);
            Optional.of(config.getMaxNumberOfRejectedRequestPerConnection())
                    .ifPresent(builder::maxNumberOfRejectedRequestPerConnection);
            Optional.of(config.getKeepAliveInterval())
                    .ifPresent(kai -> builder.keepAliveInterval((int) kai.toSeconds(), TimeUnit.SECONDS));
            Optional.of(config.getConnectionTimeout())
                    .ifPresent(ct -> builder.connectionTimeout((int) ct.toMillis(), TimeUnit.MILLISECONDS));
            Optional.of(config.getDefaultBackoffIntervalNanos())
                    .ifPresent(dbin -> builder.startingBackoffInterval(dbin.toNanos(), TimeUnit.NANOSECONDS));
            Optional.of(config.getMaxBackoffIntervalNanos())
                    .ifPresent(mbi -> builder.maxBackoffInterval(mbi.toNanos(), TimeUnit.NANOSECONDS));
        }

        private static final BeanCreationException unexpectedErrorOccurs(String beanName, Throwable cause) {
            return new BeanCreationException(beanName, "Unexpected error occurs when create pulsar client", cause);
        }
    }

}
