package com.example.web.extranet.controller.lang;

import com.example.domain.model.lang.Translation;
import com.example.service.lang.TranslationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/extranet/translations")
@RequiredArgsConstructor
public class ExtranetTranslationController {
    private final TranslationService translationService; // 👈 Cambiado aquí

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("translation", new Translation());
        return "intranet/lang/form-translation";
    }

//    @PostMapping("/guardar")
//    public String guardarTraduccion(
//            @Valid @ModelAttribute("translation") Translation translation,
//            BindingResult result,
//            Model model) {
//
//        if (result.hasErrors()) {
//            return "extranet/lang/form-translation";
//        }
//
//        // Guarda directamente usando el servicio monolítico (que ejecuta el @CacheEvict automáticamente)
//        translationService.guardar(translation);
//
//        return "redirect:/extranet/translations/exito";
//    }

    @GetMapping("/exito")
    public String paginaExito() {
        return "extranet/lang/exito";
    }
}
