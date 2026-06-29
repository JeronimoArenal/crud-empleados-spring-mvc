package com.example.service.estudiante;

import com.example.domain.model.entity.estudiante.CorreoEs;
import com.example.domain.model.entity.estudiante.Estudiante;

import java.util.List;

public interface CorreoEsService {
    //............................... Find all..........................
    List<CorreoEs> findAll();

    //............................... save ..........................
    CorreoEs saveCorreoEs(CorreoEs correo);

    boolean existsByEstudiante(Estudiante estudiante);

    void deleteByEstudiante(Estudiante estudiante);

    List<CorreoEs> findByEstudiante(Estudiante estudiante);

}
