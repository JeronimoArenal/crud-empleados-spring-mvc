package com.example.service.estudiante;

import com.example.domain.model.entity.estudiante.Estudiante;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EstudianteService {

    // ............................... Find all ..........................
    List<Estudiante> findAll();

    // ............................... Find by Id ..........................
    Estudiante findById(Long id);

    // ............................... Save ..........................
    Estudiante save(Estudiante estudiante, String strTelefono, String strCorreo, MultipartFile image);

    // ............................... Update ..........................
    Estudiante update(Long id, Estudiante estudianteDetalles, String strTelefono, String strCorreo, MultipartFile image);

    // ............................... Delete por ID ..........................
    void deleteEstudiante(Long id);
}
