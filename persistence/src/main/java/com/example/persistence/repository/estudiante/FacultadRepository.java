package com.example.persistence.repository.estudiante;

import com.example.domain.model.entity.estudiante.Facultad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacultadRepository extends JpaRepository<Facultad, Long> {
}
