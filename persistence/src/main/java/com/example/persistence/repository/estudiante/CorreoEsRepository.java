package com.example.persistence.repository.estudiante;

import com.example.domain.model.entity.empleado.Correo;
import com.example.domain.model.entity.estudiante.CorreoEs;
import com.example.domain.model.entity.estudiante.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CorreoEsRepository extends JpaRepository<CorreoEs, Long> {
    Optional<CorreoEs> findByEmail(String email);
    boolean existsByEstudiante(Estudiante estudiante);
    void deleteByEstudiante(Estudiante estudiante);
    List<CorreoEs> findCorreoByEstudiante(Estudiante estudiante);
}
