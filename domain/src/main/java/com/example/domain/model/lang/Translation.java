package com.example.domain.model.lang;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "translations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"locale", "code"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El idioma (locale) es obligatorio")
    @Size(min = 2, max = 5, message = "El locale debe tener entre 2 y 5 caracteres (ej. es, en_US)")
    @Column(nullable = false, length = 5)
    private String locale;

    @NotBlank(message = "El código de traducción es obligatorio")
    @Column(nullable = false)
    private String code;

    @NotBlank(message = "El texto de la traducción es obligatorio")
    @Column(name = "translation_text", nullable = false, columnDefinition = "TEXT")
    private String text;
}
