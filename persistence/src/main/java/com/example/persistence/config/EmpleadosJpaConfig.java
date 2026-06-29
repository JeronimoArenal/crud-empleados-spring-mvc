package com.example.persistence.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.*;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.example.persistence.repository.empleado",
                "com.example.persistence.repository.estudiante"
        },
        entityManagerFactoryRef = "empleadosEntityManagerFactory",
        transactionManagerRef = "empleadosTransactionManager"
)
public class EmpleadosJpaConfig {

    @Primary
    @Bean(name = "empleadosEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean empleadosEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("empleadosDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("com.example.domain.model.entity")
                .persistenceUnit("empleadosPU")
                .build();
    }

    @Primary
    @Bean(name = "empleadosTransactionManager")
    public PlatformTransactionManager empleadosTransactionManager(
            @Qualifier("empleadosEntityManagerFactory") EntityManagerFactory emf) {

        return new JpaTransactionManager(emf);
    }
}