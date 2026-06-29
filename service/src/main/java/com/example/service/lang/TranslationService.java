package com.example.service.lang;

import com.example.domain.model.lang.Translation;
import com.example.persistence.repository.lang.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private final TranslationRepository translationRepository;

    @Transactional(value = "langTransactionManager", readOnly = true)
    @Cacheable(value = "translations", key = "#locale + '_' + #code")
    public Optional<Translation> buscarPorIdiomaAndCodigo(String locale, String code) {
        System.out.println("CONSULTANDO DB SECUNDARIA (Caché Miss): " + locale + " - " + code);
        return translationRepository.findByLocaleAndCode(locale, code);
    }

    @Transactional("langTransactionManager")
    @CacheEvict(value = "translations", key = "#translation.locale + '_' + #translation.code")
    public Translation guardar(Translation translation) {
        return translationRepository.save(translation);
    }

    @Transactional("langTransactionManager")
    @CacheEvict(value = "translations", key = "#locale + '_' + #code")
    public void eliminar(String locale, String code) {
        translationRepository.deleteByLocaleAndCode(locale, code);
    }
}