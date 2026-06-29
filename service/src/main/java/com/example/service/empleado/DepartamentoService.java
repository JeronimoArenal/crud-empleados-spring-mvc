package com.example.service.empleado;

import com.example.domain.model.entity.empleado.Departamento;

import java.util.List;
import java.util.Optional;

public interface DepartamentoService {

    //............................... Find all..........................
    List<Departamento> findAll();

    //............................... Find by Id ..........................
    // Usamos Optional para manejar limpiamente si el empleado no existe
    Optional<Departamento> findById(Long id);

    //............................... save ..........................
    Departamento saveDepartamento(Departamento departamento);

}
