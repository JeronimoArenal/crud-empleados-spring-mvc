package com.example.persistence.config.i18n;

import com.example.persistence.repository.lang.TranslationRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class MessageSourceConfig {

    @Bean("messageSource")
    public MessageSource messageSource(TranslationRepository translationDomainRepository) {

        // Fuente principal: DB
        DatabaseMessageSource dbMessageSource = new DatabaseMessageSource(translationDomainRepository);

        // Fallback: messages.properties en classpath
        ReloadableResourceBundleMessageSource fileMessageSource = new ReloadableResourceBundleMessageSource();
        fileMessageSource.setBasenames("classpath:i18n/messages");
        fileMessageSource.setDefaultEncoding("UTF-8");

        // Si no encuentra en DB, busca en properties
        dbMessageSource.setParentMessageSource(fileMessageSource);

        return dbMessageSource;
    }

    // Conecta el motor de validación de Java con nuestros sistema de traducción personalizado.
    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }

}
