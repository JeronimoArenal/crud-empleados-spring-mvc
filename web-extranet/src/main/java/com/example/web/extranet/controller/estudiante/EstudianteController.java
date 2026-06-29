package com.example.web.extranet.controller.estudiante;

import com.example.domain.constant.Genero;
import com.example.domain.model.entity.empleado.Correo;
import com.example.domain.model.entity.empleado.Empleado;
import com.example.domain.model.entity.empleado.Telefono;
import com.example.domain.model.entity.estudiante.CorreoEs;
import com.example.domain.model.entity.estudiante.Estudiante;
import com.example.domain.model.entity.estudiante.Facultad;
import com.example.domain.model.entity.estudiante.TelefonoEs;
import com.example.service.estudiante.EstudianteService;
import com.example.service.estudiante.FacultadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/intranet/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final FacultadService facultadService;

    private static final String MAIN_LAYOUT = "design/layout/main";
    private static final String FORM_TEMPLATE = "estudiantes/add";


    //....................... listEstudiantes .......................................
    @GetMapping({"", "/list"})
    public String listEstudiantes(Model model) {
        model.addAttribute("allEstudiantes", estudianteService.findAll());
        model.addAttribute("contentTemplate", "estudiantes/list");
        return MAIN_LAYOUT;
    }

    //....................... add .......................................
    @GetMapping("/add")
    // Añade nombre explícito al ModelAttribute para evitar fallos de nombres
    public String addEstudiante(@ModelAttribute("estudiante") Estudiante estudiante, Model model) {
        estudiante.setFechaNacimiento(LocalDate.now());
        estudiante.setFacultad(new Facultad());

        model.addAttribute("txtTelefono", "");
        model.addAttribute("txtCorreo", "");
        model.addAttribute("contentTemplate", FORM_TEMPLATE);
        return MAIN_LAYOUT;
    }

    //....................... editEstudiante .......................................
    @GetMapping("/edit/{id}")
    public String editEstudiante(@PathVariable Long id, Model model) {

        try {
            Estudiante estudiante = estudianteService.findById(id);
            model.addAttribute("estudiante", estudiante);

            // Convertimos las colecciones en String plano separado por ";" para la vista
            String tels = estudiante.getTelefonos() != null ? estudiante.getTelefonos().stream()
                    .map(TelefonoEs::getNumero)
                    .collect(Collectors.joining(";"))
                    : "";
            String correos = estudiante.getEmails() != null ? estudiante.getEmails().stream()
                    .map(CorreoEs::getEmail)
                    .collect(Collectors.joining(";"))
                    : "";

            model.addAttribute("txtTelefono", tels);
            model.addAttribute("txtCorreo", correos);
            model.addAttribute("contentTemplate", FORM_TEMPLATE);
            return MAIN_LAYOUT;
        } catch (Exception e) {
            model.addAttribute("errorMensaje", e.getMessage());
            return "redirect:/intranet/estudiantes/list";
        }
    }

    //....................... saveEstudiante .......................................
    @PostMapping("/save")
    public String saveEstudiante(@Valid @ModelAttribute("estudiante") Estudiante estudiante,
            BindingResult resultBind,
            @RequestParam String strTelefono,
            @RequestParam  String strCorreo,
            @RequestParam(name = "imagenArchivo", required = false) MultipartFile image,
            Model model) {

        if (resultBind.hasErrors()) {
            // Devolvemos las cadenas escritas por el usuario
            model.addAttribute("txtTelefono", strTelefono);
            model.addAttribute("txtCorreo", strCorreo);

            model.addAttribute("contentTemplate", FORM_TEMPLATE);
            return MAIN_LAYOUT;
        }

        try {
            if (estudiante.getId() != null) {
                estudianteService.update(estudiante.getId(), estudiante, strTelefono, strCorreo, image);
            } else {
                estudianteService.save(estudiante, strTelefono, strCorreo, image);
            }

            // Redirección alineada con las URLs que maneja tu botón 'Volver' del formulario
            return "redirect:/intranet/estudiantes";

        } catch (Exception e) { //Captura cualquier excepción (incluidas las de archivos IO)
            model.addAttribute("txtTelefono", strTelefono);
            model.addAttribute("txtCorreo", strCorreo);
            model.addAttribute("errorMensaje", e.getMessage());


            if (estudiante.getFacultad() == null) {
                estudiante.setFacultad(new Facultad());
            }
            model.addAttribute("contentTemplate", FORM_TEMPLATE);
            return MAIN_LAYOUT;
        }
    }

    // ............................... profileEstudiante ..........................
    @GetMapping("/profile/{id}")
    public String profileEstudiante(@PathVariable("id") Long id,
                                  Model model,
                                  RedirectAttributes redirectAttributes) { // Añadido para gestionar errores en redirecciones
        try {
            Estudiante estudiante = estudianteService.findById(id);
            model.addAttribute("estudiante", estudiante);

            // Carga de teléfonos y correos en punto y coma de forma segura
            String tels = estudiante.getTelefonos() != null
                    ? String.join("; ", estudiante.getTelefonos().stream()
                    .map(TelefonoEs::getNumero)
                    .toList())
                    : "";
            String correos = estudiante.getEmails() != null
                    ? String.join("; ", estudiante.getEmails().stream()
                    .map(CorreoEs::getEmail)
                    .toList())
                    : "";

            model.addAttribute("txtTelefono", tels);
            model.addAttribute("txtCorreo", correos);

            // Enviamos readonly para indicar al formulario HTML que oculte el input file y bloquee los campos
            model.addAttribute("readonly", true);
            model.addAttribute("contentTemplate", FORM_TEMPLATE);
            return MAIN_LAYOUT;

        } catch (Exception e) {
            // Usamos addFlashAttribute para que el mensaje sobreviva a la redirección HTTP
            redirectAttributes.addFlashAttribute("errorMensaje", "Error al cargar el perfil: " + e.getMessage());
            return "redirect:/intranet/estudiantes";
        }
    }

    // ............................... verInfoContacto ..........................
    @GetMapping("/detalle/{id}")
    public String verInfoContacto(@PathVariable("id") Long id, Model model) {
        try {
            model.addAttribute("estudiante", estudianteService.findById(id));
            model.addAttribute("contentTemplate", "estudiantes/detalle");
            return MAIN_LAYOUT;
        } catch (Exception e) {
            model.addAttribute("errorMensaje", "Error al cargar la información: " + e.getMessage());
            return "redirect:/intranet/estudiantes/list";
        }
    }

    //....................... deleteEmpleado .......................................
    @PostMapping("/delete/{id}")
    public String deleteEstudiante(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Llama a tu servicio que ahora elimina tanto el registro como el archivo físico
            estudianteService.deleteEstudiante(id);

            // Mensaje de éxito flash que se muestra solo en la siguiente pantalla
            redirectAttributes.addFlashAttribute("exitoMensaje", "Estudiante y su imagen de perfil eliminados correctamente.");

        } catch (Exception e) {
            // Si hay un error (ej. restricción de clave foránea o fallo de disco), lo notificamos
            redirectAttributes.addFlashAttribute("errorMensaje", "No se pudo eliminar el estudiante: " + e.getMessage());
        }

        // Redirige al listado de empleados de la intranet
        return "redirect:/intranet/estudiantes/list";
    }



    //....................... MODEL ATTRIBUTES .......................................
    @ModelAttribute("allGenders")
    public List<Genero> populateGenders(){ return Arrays.asList(Genero.values()); }

    @ModelAttribute("allFaculties")
    public List<Facultad> populateFaculties() { return facultadService.findAll(); }

}
