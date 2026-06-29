package com.example.persistence.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LangDataSourceConfig {

    @Bean
    @ConfigurationProperties("lang.datasource")
    public DataSourceProperties langDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "langDataSource")
    @ConfigurationProperties("lang.datasource.hikari")
    public DataSource langDataSource(
            @Qualifier("langDataSourceProperties") DataSourceProperties properties) {

        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }
}