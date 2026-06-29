package com.example.service.empleado;

import com.example.domain.model.entity.empleado.Correo;
import com.example.domain.model.entity.empleado.Empleado;
import com.example.persistence.repository.empleado.CorreoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CorreoServiceImpl implements CorreoService{

    private final CorreoRepository correoRepository;

    //............................... Find all..........................
    @Override
    @Transactional(readOnly = true)
    public List<Correo> findAll() {
        return correoRepository.findAll();
    }

    //............................... save ..........................
    @Override
    public Correo saveCorreo(Correo correo) {
        return correoRepository.save(correo);
    }

    //............................... existsByEmpleado ..........................
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmpleado(Empleado empleado) {
        return correoRepository.existsCorreoByEmpleado(empleado);
    }

    //............................... deleteByEmpleado ..........................
    @Override
    public void deleteByEmpleado(Empleado empleado) {
        correoRepository.deleteByEmpleado(empleado);
    }

    //............................... findByEmpleado ..........................
    @Override
    @Transactional(readOnly = true)
    public List<Correo> findByEmpleado(Empleado empleado) {
        return correoRepository.findCorreoByEmpleado(empleado);
    }
}
