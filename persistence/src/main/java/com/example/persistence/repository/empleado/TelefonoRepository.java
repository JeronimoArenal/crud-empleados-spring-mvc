package com.example.persistence.repository.empleado;

import com.example.domain.model.entity.empleado.Empleado;
import com.example.domain.model.entity.empleado.Telefono;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelefonoRepository extends JpaRepository<Telefono, Long> {

    boolean existsByNumero(String numero);
    boolean existsTelefonoByEmpleado(Empleado empleado);
    void deleteByEmpleado(Empleado empleado);
    List<Telefono> findTelefonoByEmpleado(Empleado empleado);
}
