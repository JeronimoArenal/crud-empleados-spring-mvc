package com.example.persistence.repository.lang;

import com.example.domain.model.lang.Translation;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface TranslationRepository extends JpaRepository<Translation, Long> {

    // Almacena en la caché "translations" usando una clave combinada (idioma + código)
    @Cacheable(value = "translations", key = "{#locale, #code}")
    Optional<Translation> findByLocaleAndCode(String locale, String code);

    // Desaloja automáticamente el registro de la caché cuando se elimina de la base de datos
    @CacheEvict(value = "translations", key = "{#locale, #code}")
    void deleteByLocaleAndCode(String locale, String code);

    // Opcional: Si necesitas cachear listados completos por idioma
    @Cacheable(value = "translations", key = "#locale")
    List<Translation> findByLocale(String locale);
}
