package com.example.service.estudiante;

import com.example.domain.model.entity.empleado.Correo;
import com.example.domain.model.entity.empleado.Empleado;
import com.example.domain.model.entity.estudiante.CorreoEs;
import com.example.domain.model.entity.estudiante.Estudiante;
import com.example.persistence.repository.estudiante.CorreoEsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CorreoEsServiceImpl implements CorreoEsService{

    private final CorreoEsRepository correoEsRepository;

    //............................... Find all..........................
    @Override
    @Transactional(readOnly = true)
    public List<CorreoEs> findAll() {
        return correoEsRepository.findAll();
    }

    //............................... findByEstudiante ..........................
    @Override
    @Transactional(readOnly = true)
    public List<CorreoEs> findByEstudiante(Estudiante estudiante) {
        return correoEsRepository.findCorreoByEstudiante(estudiante);
    }

    //............................... save ..........................
    @Override
    public CorreoEs saveCorreoEs(CorreoEs correoEs) {
        return correoEsRepository.save(correoEs);
    }

    //............................... existsByEstudiante ..........................
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEstudiante(Estudiante estudiante) {
        return correoEsRepository.existsByEstudiante(estudiante);
    }

    //............................... deleteByEstudiante ..........................
    @Override
    public void deleteByEstudiante(Estudiante estudiante) {
        correoEsRepository.deleteByEstudiante(estudiante);
    }



}
