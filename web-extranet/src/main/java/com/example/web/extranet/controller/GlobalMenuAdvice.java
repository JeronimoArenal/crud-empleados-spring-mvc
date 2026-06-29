package com.example.web.extranet.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.Map;

@ControllerAdvice // Antes de que cualquier Controller hay que inyectar este, antes de servir la pagina
public class GlobalMenuAdvice {

    @ModelAttribute("menuUrls")
    public Map<String, String> globalMenu() {
        return Map.of(
                "word.students", "/intranet/estudiantes",    //Si son varios lleva , solo 1 sin ,
                "word.employees", "/intranet/empleados"
        );
    }
}
