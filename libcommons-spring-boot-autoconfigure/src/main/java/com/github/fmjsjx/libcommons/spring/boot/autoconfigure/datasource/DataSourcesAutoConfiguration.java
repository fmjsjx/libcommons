package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.datasource;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.EnvironmentUtil;
import com.github.fmjsjx.libcommons.spring.boot.autoconfigure.EnvironmentUtilAutoConfiguration;
import com.github.fmjsjx.libcommons.util.ClassUtil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Configuration
@AutoConfigureAfter(EnvironmentUtilAutoConfiguration.class)
public class DataSourcesAutoConfiguration {

    @Bean
    public static final DataSourcesRegisteryProcessor dataSourcesRegisteryProcessor() {
        return new DataSourcesRegisteryProcessor();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class DataSourcesRegisteryProcessor implements BeanDefinitionRegistryPostProcessor {

        private BeanDefinitionRegistry registry;

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            // ignore
        }

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            this.registry = registry;
            Environment env = EnvironmentUtil.getInstance().getEnvironment();
            BindResult<DataSourcesProperties> bindResult = Binder.get(env).bind("libcommons.datasources",
                    DataSourcesProperties.class);
            bindResult.ifBound(this::registerBeans);
        }

        private void registerBeans(DataSourcesProperties properties) throws BeansException {
            Map<String, DataSourceProperties> pools = properties.getPools();
            pools.forEach(this::registerStaticPool);
        }

        private void registerStaticPool(String poolName, DataSourceProperties properties) throws BeansException {
            Environment env = EnvironmentUtil.getInstance().getEnvironment();
            String beanName = poolName + "DataSource";
            Class<? extends DataSource> type = properties.getType();
            if (type == null) {
                if (ClassUtil.hasClassForName("com.zaxxer.hikari.HikariDataSource")) {
                    registerHikari(poolName, properties, env, beanName);
                } else if (ClassUtil.hasClassForName("org.apache.commons.dbcp2.BasicDataSource")) {
                    registerDbcp2(poolName, properties, env, beanName);
                } else {
                    registerOther(properties, beanName);
                }
            } else {
                switch (type.getName()) {
                case "com.zaxxer.hikari.HikariDataSource":
                    registerHikari(poolName, properties, env, beanName);
                    break;
                case "org.apache.commons.dbcp2.BasicDataSource":
                    registerDbcp2(poolName, properties, env, beanName);
                    break;
                default:
                    registerOther(properties, beanName);
                    break;
                }
            }
        }

        private void registerOther(DataSourceProperties properties, String beanName) {
            DataSource dataSource = properties.initializeDataSourceBuilder().build();
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(dataSource.getClass());
            builder.getRawBeanDefinition().setInstanceSupplier(() -> dataSource);
            registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
        }

        private void registerHikari(String poolName, DataSourceProperties properties, Environment env,
                String beanName) {
            var configFieldName = "libcommons.datasources.pools." + poolName + ".hikari";
            com.zaxxer.hikari.HikariDataSource dataSource = createDataSource(properties,
                    com.zaxxer.hikari.HikariDataSource.class);
            if (properties.getName() != null) {
                dataSource.setPoolName(properties.getName());
            }
            Binder.get(env).bind(configFieldName, Bindable.ofInstance(dataSource));
            BeanDefinitionBuilder builder = BeanDefinitionBuilder
                    .genericBeanDefinition(com.zaxxer.hikari.HikariDataSource.class, () -> dataSource);
            registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
        }

        private void registerDbcp2(String poolName, DataSourceProperties properties, Environment env, String beanName) {
            var configFieldName = "libcommons.datasources.pools." + poolName + ".dbcp2";
            org.apache.commons.dbcp2.BasicDataSource dataSource = createDataSource(properties,
                    org.apache.commons.dbcp2.BasicDataSource.class);
            Binder.get(env).bind(configFieldName, Bindable.ofInstance(dataSource));
            BeanDefinitionBuilder builder = BeanDefinitionBuilder
                    .genericBeanDefinition(org.apache.commons.dbcp2.BasicDataSource.class, () -> dataSource);
            registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
        }

        @SuppressWarnings("unchecked")
        private static final <T> T createDataSource(DataSourceProperties properties, Class<? extends DataSource> type) {
            return (T) properties.initializeDataSourceBuilder().type(type).build();
        }

    }

}
