package com.example.web.extranet.controller.empleado;


import com.example.domain.model.entity.empleado.Empleado;
import com.example.service.empleado.DepartamentoService;
import com.example.service.empleado.EmpleadoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController // 1. Cambiado de @Controller a @RestController
@RequestMapping("/api/intranet/empleados") // 2. Buenas prácticas: añadir prefijo /api
@RequiredArgsConstructor
public class EmpleadoRestController {

    private final EmpleadoService empleadoService;
    private final DepartamentoService departamentoService;

    // Se eliminan las constantes de los templates HTML porque ya no se usan

    //....................... listEmpleados .......................................
    @GetMapping({"", "/list"})
    public ResponseEntity<List<Empleado>> listEmpleados() {
        // 4. Se obtienen los datos directamente del servicio
        List<Empleado> empleados = empleadoService.findAll();

        // 5. Se devuelve un estado HTTP 200 (OK) con la lista en el cuerpo (JSON)
        return ResponseEntity.ok(empleados);
    }


}
