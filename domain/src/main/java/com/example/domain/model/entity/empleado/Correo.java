package com.example.domain.model.entity.empleado;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "correos")
public class Correo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=100, nullable=false, unique=true)
    @Email
    private String email;

    //....................... RELATIONSHIPS .......................................
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id")
    private Empleado empleado;
}
