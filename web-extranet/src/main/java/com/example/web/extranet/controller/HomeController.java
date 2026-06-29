package com.example.web.extranet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.List;

@Controller
public class HomeController {

    private static final String MAIN_LAYOUT = "design/layout/main";

    // ............................... handleDomain / Raíz ..........................
    @GetMapping("/")
    public String handleDomain(Model model) {
        // ⚡ Inyectamos la ruta del fragmento de la home (Igual que en Empleados)
        model.addAttribute("contentTemplate", "home/home");
        return MAIN_LAYOUT;
    }

    // ........................... MODEL ATTRIBUTES LOCALES DE LA HOME ........................
    // Estos datos solo se inyectarán cuando el usuario visite la raíz (/)

    @ModelAttribute("activeModules")
    public List<String> populateModules() {
        return List.of("Administración", "Seguridad Global", "Logs del Sistema");
    }

    @ModelAttribute("availableAliases")
    public List<String> populateAvailableAliases() {
        return List.of("LAND", "AIR", "WATER");
    }

    @ModelAttribute("activeAliases")
    public List<String> populateActiveAliases() {
        return List.of(); // Lista vacía inicial
    }

    @ModelAttribute("backgroundImage")
    public String populateBackground() {
        return "/uploads/background/default.png";
    }
}