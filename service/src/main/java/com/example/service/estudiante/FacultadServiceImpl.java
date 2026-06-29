package com.example.service.estudiante;

import com.example.domain.model.entity.estudiante.Facultad;
import com.example.persistence.repository.estudiante.FacultadRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class FacultadServiceImpl implements FacultadService{

    private final FacultadRepository facultadRepository;
    //............................... Find all..........................
    @Override
    @Transactional
    public List<Facultad> findAll() {
        return facultadRepository.findAll();
    }
}
