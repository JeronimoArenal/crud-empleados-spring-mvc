package com.example.persistence.repository.empleado;

import com.example.domain.model.entity.empleado.Correo;
import com.example.domain.model.entity.empleado.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CorreoRepository extends JpaRepository<Correo, Long> {

    // Busca el correo y nos permite comprobar si pertenece a otro ID de empleado
    Optional<Correo> findByEmail(String email);
    boolean existsCorreoByEmpleado(Empleado empleado);
    void deleteByEmpleado(Empleado empleado);
    List<Correo> findCorreoByEmpleado(Empleado empleado);

}
