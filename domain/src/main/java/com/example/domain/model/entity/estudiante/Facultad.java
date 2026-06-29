package com.example.domain.model.entity.estudiante;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "facultades")
public class Facultad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    //....................... RELATIONSHIPS .......................................
    //mappedBy apunta al atributo propietario de la relacion en el lado de Many
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "facultad")
    private List<Estudiante> estudiantes = new ArrayList<>();   //Tambien iniciaizamos la List

}

