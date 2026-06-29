package com.example.service.empleado;

import com.example.domain.model.entity.empleado.Empleado;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EmpleadoService {

    // ............................... Find all ..........................
    List<Empleado> findAll();

    // ............................... Find by Id ..........................
    Empleado findById(Long id);

    // ............................... Save ..........................
//    Empleado save(Empleado empleado, String strTelefono, String strCorreo);
    Empleado save(Empleado empleado, String strTelefono, String strCorreo, MultipartFile image);

    // ............................... Update ..........................
//    Empleado update(Long id, Empleado empleado, String strTelefono, String strCorreo);
    Empleado update(Long id, Empleado empleadoDetalles, String strTelefono, String strCorreo, MultipartFile image);


    // ............................... Delete por ID ..........................
    void deleteEmpleado(Long id);

    // ............................... Process image ..........................
    String processEmpleadoImage(Empleado empleado, MultipartFile image) throws IOException;
}
