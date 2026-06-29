package com.example.persistence.repository.estudiante;

import com.example.domain.model.entity.estudiante.Estudiante;
import com.example.domain.model.entity.estudiante.TelefonoEs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelefonoEsRepository extends JpaRepository<TelefonoEs, Long> {
    boolean existsByNumero(String numero);
    boolean existsByEstudiante(Estudiante estudiante);
    void deleteByEstudiante(Estudiante estudiante);
    List<TelefonoEs> findTelefonoByEstudiante(Estudiante estudiante);
}
