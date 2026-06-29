package com.example.persistence.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.*;
import javax.sql.DataSource;

@Configuration
public class EmpleadosDataSourceConfig {

    // =========================
    // PROPIEDADES DB EMPLEADOS
    // =========================
    @Bean
    @Primary
    @ConfigurationProperties("empleados.datasource")
    public DataSourceProperties empleadosDataSourceProperties() {
        return new DataSourceProperties();
    }

    // =========================
    // DATASOURCE EMPLEADOS
    // =========================
    @Bean(name = "empleadosDataSource")
    @Primary
    @ConfigurationProperties("empleados.datasource.hikari")
    public DataSource empleadosDataSource(
            @Qualifier("empleadosDataSourceProperties") DataSourceProperties properties) {

        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }
}
