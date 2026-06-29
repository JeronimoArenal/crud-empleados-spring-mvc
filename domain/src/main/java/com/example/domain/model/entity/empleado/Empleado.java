package com.example.domain.model.entity.empleado;

import com.example.domain.constant.Genero;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/** @Data genera automáticamente los métodos toString(), equals() y hashCode().
 * El problema es que si tu entidad tiene relaciones (@OneToMany o @ManyToOne),
 * estos métodos intentan cargar las entidades relacionadas de forma infinita.
 * Esto provoca un error de desbordamiento de memoria (StackOverflowError) o peticiones innecesarias
 * a la base de datos (Lazy Initialization Exception).
 * ADVERTENCIA: Al tener relaciones bidireccionales (Empleado apunta a Correo y Correo apunta a Empleado),
 * si en el futuro se añade @Data o @ToString para depurar el código o imprimirlo en consola, la aplicación se romperá
 * inmediatamente por un bucle infinito
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "empleados")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{message.god.name}")
    @Size(min = 4, max = 75)
    @Pattern(regexp = "^[A-ZÁÉÍÓÚÑÜ][a-záéíóúñü]+(?: [A-ZÁÉÍÓÚÑÜ][a-záéíóúñü]+)*$",
            message = "{message.regexp.error}")
    private String nombre;

    @NotBlank(message = "{message.god.name}")
    @Size(min = 2, max = 75)
    @Pattern(regexp = "^([A-ZÁÉÍÓÚÑÜ][a-záéíóúñü]+(\s)?)+$", //Del profe
            message = "{message.regexp.error}")
    private String primerApellido;

    @Size(max = 100)
    @Pattern(regexp = "^$|^[A-ZÁÉÍÓÚÑÜ][a-záéíóúñü]+(?: [A-ZÁÉÍÓÚÑÜ][a-záéíóúñü]+)*$",
            message = "{message.regexp.error}")
    private String segundoApellido;

    @NotNull(message = "{message.god.name}")
    @Enumerated(value = EnumType.STRING)
    private Genero genero;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @NotNull(message = "{message.human.datenull}")
    @PastOrPresent(message = "{message.human.datepast}")
    private LocalDate fechaAlta;


    @PositiveOrZero(message = "El salario no puede ser negativo.")
    @Column(precision = 10, scale = 2)
    private BigDecimal salario;

    private String image;

    //....................... RELATIONSHIPS .......................................
    //Por defecto FetchType es EAGER
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", referencedColumnName="id" ) //No es obligatorio pero controlas el nombre de la columna.
    private Departamento departamento;

    //Por defecto FetchType es LAZY
    @Builder.Default
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Evita bucles infinitos si imprimes el objeto en logs
    private Set<Telefono> telefonos = new HashSet<>();  //new para evitar NullPointerException

    //Por defecto FetchType es LAZY
    @Builder.Default
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Evita bucles infinitos si imprimes el objeto en logs
    private Set<Correo> emails = new HashSet<>();       //new para evitar NullPointerException

}
