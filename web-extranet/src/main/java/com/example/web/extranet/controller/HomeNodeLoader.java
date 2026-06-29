//package com.example.web.extranet.controller;
//
//import com.example.common.loader.NodeLoader;
//import com.example.domain.model.lang.Translation;
//import org.springframework.context.MessageSource;
//import org.springframework.context.support.MessageSourceAccessor;
//import org.springframework.stereotype.Component;
//import org.springframework.ui.Model;
//import java.util.List;
//
//@Component
//public class HomeNodeLoader implements NodeLoader<Translation> {
//
//    private final MessageSourceAccessor accessor;
//
//    // Al pasarle el MessageSource global, Spring MVC le inyecta automáticamente tu DatabaseMessageSource con caché
//    public HomeNodeLoader(MessageSource messageSource) {
//        this.accessor = new MessageSourceAccessor(messageSource);
//    }
//
//    @Override
//    public boolean supports(Object entity) {
//        return entity instanceof Translation;
//    }
//
//    @Override
//    public void load(Translation entity, Model model) {
//        List<String> modulos = List.of("Administración", "Seguridad Global", "Logs del Sistema");
//        model.addAttribute("activeModules", modulos);
//        model.addAttribute("availableAliases", List.of("LAND", "AIR", "WATER"));
//        model.addAttribute("activeAliases", List.of());
//        model.addAttribute("backgroundImage", "/uploads/background/default.png");
//
//        if (!model.containsAttribute("contentTemplate")) {
//            model.addAttribute("contentTemplate", "home/home");
//        }
//    }
//}
