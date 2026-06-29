package com.example.persistence.config.i18n;

import com.example.persistence.repository.lang.TranslationRepository;
import org.springframework.context.support.AbstractMessageSource;

import java.text.MessageFormat;
import java.util.Locale;
public class DatabaseMessageSource extends AbstractMessageSource {

    private final TranslationRepository translationRepository;

    public DatabaseMessageSource(TranslationRepository translationDomainRepository) {
        this.translationRepository = translationDomainRepository;
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {

        return translationRepository
                .findByLocaleAndCode(locale.getLanguage(), code)
                .map(t -> new MessageFormat(t.getText(), locale))
                .orElse(null);
    }
}