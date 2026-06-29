package com.example.service.empleado;

import com.example.domain.model.entity.empleado.Empleado;
import com.example.domain.model.entity.empleado.Telefono;
import com.example.persistence.repository.empleado.TelefonoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelefonoServiceImpl implements TelefonoService{

    private final TelefonoRepository telefonoRepository;

    //............................... Find all..........................
    @Override
    @Transactional(readOnly = true)
    public List<Telefono> findAll() {
        return telefonoRepository.findAll();
    }

    //............................... save ..........................
    @Override
    public Telefono saveTelefono(Telefono telefono) {
        return telefonoRepository.save(telefono);
    }

    //............................... existsByEmpleado ..........................
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmpleado(Empleado empleado) {
        return telefonoRepository.existsTelefonoByEmpleado(empleado);
    }

    //............................... deleteByEmpleado ..........................
    @Override
    public void deleteByEmpleado(Empleado empleado) {
        telefonoRepository.deleteByEmpleado(empleado);
    }

    //............................... findByEmpleado ..........................
    @Override
    @Transactional(readOnly = true)
    public List<Telefono> findByEmpleado(Empleado empleado) {
        return telefonoRepository.findTelefonoByEmpleado(empleado);
    }
}
