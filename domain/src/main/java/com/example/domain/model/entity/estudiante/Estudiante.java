package com.example.domain.model.entity.estudiante;

import com.example.domain.constant.Genero;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "estudiantes")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{message.god.name}")
    @Size(max = 75)
    @Pattern(regexp = "^[A-ZÁÉÍÓÚÑÜ][a-záéíóúñü]+(?: [A-ZÁÉÍÓÚÑÜ][a-záéíóúñü]+)*$",
            message = "{message.regexp.error}")
    private String nombre;

    @NotBlank
    @Size(min = 2, max = 75)
    @Column(length = 75)
    @Pattern(regexp = "^[A-ZÁÉÍÓÚÑÜ][a-záéíóúñü]+(?: [A-ZÁÉÍÓÚÑÜ][a-záéíóúñü]+)*$",
            message = "{message.regexp.error}")
    private String primerApellido;


    @Size(max = 75)
    @Column(length = 75)
    @Pattern(regexp = "^$|^[A-ZÁÉÍÓÚÑÜ][a-záéíóúñü]+(?: [A-ZÁÉÍÓÚÑÜ][a-záéíóúñü]+)*$",
            message = "{message.regexp.error}")
    private String segundoApellido;

    @Enumerated(value = EnumType.STRING)
    private Genero genero;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @NotNull(message = "{message.human.datenull}")
    @PastOrPresent(message = "{message.human.datepast}")
    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    private BigDecimal beca;

    @Column(name = "total_asignaturas", nullable = false)
    private Integer totalAsignaturas;

    private String image;

    //....................... RELATIONSHIPS .......................................
    //Por defecto FetchType es EAGER
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "facultad_id", referencedColumnName="id" ) //No es obligatorio pero controlas el nombre de la columna.
    private Facultad facultad;

    //Por defecto FetchType es LAZY
    @Builder.Default
    @OneToMany( mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TelefonoEs> telefonos = new HashSet<>();  //new para evitar NullPointerException

    //Por defecto FetchType es LAZY
    @Builder.Default
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CorreoEs> emails = new HashSet<>();       //new para evitar NullPointerException

}

