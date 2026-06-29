package com.example.persistence.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.persistence.repository.lang",
        entityManagerFactoryRef = "langEntityManagerFactory",
        transactionManagerRef = "langTransactionManager"
)
public class LangJpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean langEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("langDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("com.example.domain.model.lang")
                .persistenceUnit("langPU")
                .build();
    }

    @Bean
    public PlatformTransactionManager langTransactionManager(
            @Qualifier("langEntityManagerFactory") EntityManagerFactory emf) {

        return new JpaTransactionManager(emf);
    }
}