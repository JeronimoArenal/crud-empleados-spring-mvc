package com.example.domain.model.entity.empleado;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "departamentos")
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    //....................... RELATIONSHIPS .......................................
    //mappedBy apunta al atributo propietario de la relacion en el lado de Many
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "departamento")
    private List<Empleado> empleados = new ArrayList<>();   //Tambien iniciaizamos la List

}
