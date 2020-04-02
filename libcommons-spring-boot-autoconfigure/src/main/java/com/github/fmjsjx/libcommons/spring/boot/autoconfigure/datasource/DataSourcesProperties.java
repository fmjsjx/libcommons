package com.github.fmjsjx.libcommons.spring.boot.autoconfigure.datasource;

import java.util.Map;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ConfigurationProperties("libcommons.datasources")
public class DataSourcesProperties {

    private Map<String, DataSourceProperties> pools;

}
