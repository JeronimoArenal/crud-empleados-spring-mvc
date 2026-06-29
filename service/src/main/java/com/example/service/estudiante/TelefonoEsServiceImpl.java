package com.example.service.estudiante;

import com.example.domain.model.entity.empleado.Telefono;
import com.example.domain.model.entity.estudiante.TelefonoEs;
import com.example.persistence.repository.estudiante.TelefonoEsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelefonoEsServiceImpl implements TelefonoEsService {

    private final TelefonoEsRepository telefonoEsRepository;

    //............................... Find all..........................
    @Override
    @Transactional(readOnly = true)
    public List<TelefonoEs> findAll() {
        return telefonoEsRepository.findAll();
    }
}
