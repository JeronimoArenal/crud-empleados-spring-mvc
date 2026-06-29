package com.example.domain.model.entity.estudiante;

import com.example.domain.model.entity.empleado.Empleado;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "correosEs") // Apunta a tu tabla física de correos
public class CorreoEs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 45, unique = true, name = "email")
    private String email;

    //....................... RELATIONSHIPS .......................................
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false )
    private Estudiante estudiante;

}

