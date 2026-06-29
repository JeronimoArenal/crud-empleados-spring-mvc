package com.example.service.empleado;

import com.example.domain.model.entity.empleado.Correo;
import com.example.domain.model.entity.empleado.Empleado;

import java.util.List;

public interface CorreoService {
    //............................... Find all..........................
    List<Correo> findAll();

    //............................... save ..........................
    Correo saveCorreo(Correo correo);

    boolean existsByEmpleado(Empleado empleado);

    void deleteByEmpleado(Empleado empleado);

    List<Correo> findByEmpleado(Empleado empleado);
}
