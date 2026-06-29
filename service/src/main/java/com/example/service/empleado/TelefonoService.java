package com.example.service.empleado;

import com.example.domain.model.entity.empleado.Empleado;
import com.example.domain.model.entity.empleado.Telefono;

import java.util.List;

public interface TelefonoService {

    List<Telefono> findAll();

    Telefono saveTelefono(Telefono telefono);

    boolean existsByEmpleado(Empleado empleado);

    void deleteByEmpleado(Empleado empleado);

    List<Telefono> findByEmpleado(Empleado empleado);
}
