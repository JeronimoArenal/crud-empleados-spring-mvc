package com.example.web.extranet.controller.empleado;

import com.example.domain.constant.Genero;
import com.example.domain.model.entity.empleado.Correo;
import com.example.domain.model.entity.empleado.Departamento;
import com.example.domain.model.entity.empleado.Empleado;
import com.example.domain.model.entity.empleado.Telefono;
import com.example.service.empleado.DepartamentoService;
import com.example.service.empleado.EmpleadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/intranet/empleados")
@RequiredArgsConstructor

public class EmpleadoController {

    private final EmpleadoService empleadoService;
    private final DepartamentoService departamentoService;

    private static final String MAIN_LAYOUT = "design/layout/main";
    private static final String FORM_TEMPLATE = "empleados/add";

    //....................... listEmpleados .......................................
    @GetMapping({"", "/list"})
    public String listEmpleados(Model model) {
        model.addAttribute("allEmpleados", empleadoService.findAll());
        model.addAttribute("contentTemplate", "empleados/list");
        return MAIN_LAYOUT;
    }

    //....................... add .......................................
    @GetMapping("/add")
    public String addEmpleado(@ModelAttribute Empleado empleado, Model model) {
        empleado.setFechaAlta(LocalDate.now());

        model.addAttribute("txtTelefono", "");  //Inyectamos manualmente con valor vacio para que thymeleaf no falle
        model.addAttribute("txtCorreo", "");
        model.addAttribute("contentTemplate", FORM_TEMPLATE);
        return MAIN_LAYOUT;
    }

    //....................... editEmpleado .......................................
    @GetMapping("/edit/{id}")
    public String editEmpleado(@PathVariable Long id, Model model) {

        //LOG con Slf4j de Lombok
        log.info("Accediendo a la edición del empleado con ID: {}", id);

        try {
            Empleado empleado = empleadoService.findById(id);
            model.addAttribute("empleado", empleado);

            // Convertimos las colecciones en String plano separado por ";" para la vista
            String tels = empleado.getTelefonos() != null ? empleado.getTelefonos().stream()
                    .map(Telefono::getNumero)
                    .collect(Collectors.joining(";"))
                    : "";
            String correos = empleado.getEmails() != null ? empleado.getEmails().stream()
                    .map(Correo::getEmail)
                    .collect(Collectors.joining(";"))
                    : "";

            model.addAttribute("txtTelefono", tels);
            model.addAttribute("txtCorreo", correos);
            model.addAttribute("contentTemplate", FORM_TEMPLATE);
            return MAIN_LAYOUT;
        } catch (Exception e) {
            model.addAttribute("errorMensaje", e.getMessage());
            return "redirect:/intranet/empleados/list";
        }
    }

    //....................... saveEmpleado .......................................
    @PostMapping("/save")
    public String saveEmpleado(@Valid @ModelAttribute("empleado") Empleado empleado,
                               BindingResult resultBind,
                               @RequestParam String strTelefono,
                               @RequestParam String strCorreo,
                               @RequestParam(name = "imagenArchivo", required = false) MultipartFile image, // Captura el archivo binario del formulario
                               Model model) {

        if (resultBind.hasErrors()) {
            // Devolvemos las cadenas escritas por el usuario
            model.addAttribute("txtTelefono", strTelefono);
            model.addAttribute("txtCorreo", strCorreo);

            model.addAttribute("contentTemplate", FORM_TEMPLATE);
            return MAIN_LAYOUT;
        }

        try {
            // Pasamos el empleado, los textos planos y el archivo directamente al Service
            if (empleado.getId() != null) {
                empleadoService.update(empleado.getId(), empleado, strTelefono, strCorreo, image);
            } else {
                empleadoService.save(empleado, strTelefono, strCorreo, image);
            }

            // Redirección alineada con las URLs que maneja el botón 'Volver' del formulario
            return "redirect:/intranet/empleados";

        } catch (Exception e) { //Captura cualquier excepción (incluidas las de archivos IO)
            model.addAttribute("txtTelefono", strTelefono);
            model.addAttribute("txtCorreo", strCorreo);
            model.addAttribute("errorMensaje", e.getMessage());

            if (empleado.getDepartamento() == null) {
                empleado.setDepartamento(new Departamento());
            }
            model.addAttribute("contentTemplate", FORM_TEMPLATE);
            return MAIN_LAYOUT;
        }
    }

    // ............................... profileEmpleado ..........................
    @GetMapping("/profile/{id}")
    public String profileEmpleado(@PathVariable("id") Long id,
                                  Model model,
                                  RedirectAttributes redirectAttributes) { // Añadido para gestionar errores en redirecciones
        try {
            Empleado empleado = empleadoService.findById(id);
            model.addAttribute("empleado", empleado);

            // Carga de teléfonos y correos en punto y coma de forma segura
            String tels = empleado.getTelefonos() != null
                    ? String.join("; ", empleado.getTelefonos().stream().map(Telefono::getNumero).toList())
                    : "";
            String correos = empleado.getEmails() != null
                    ? String.join("; ", empleado.getEmails().stream().map(Correo::getEmail).toList())
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
            return "redirect:/intranet/empleados";
        }
    }

    // ............................... verInfoContacto ..........................
    @GetMapping("/detalle/{id}")
    public String verInfoContacto(@PathVariable("id") Long id, Model model) {
        try {
            model.addAttribute("empleado", empleadoService.findById(id));
            model.addAttribute("contentTemplate", "empleados/detalle");
            return MAIN_LAYOUT;
        } catch (Exception e) {
            model.addAttribute("errorMensaje", "Error al cargar la información: " + e.getMessage());
            return "redirect:/intranet/empleados/list";
        }
    }

//....................... deleteEmpleado .......................................
@PostMapping("/delete/{id}")
public String deleteEmpleado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
        // Llama a tu servicio que ahora elimina tanto el registro como el archivo físico
        empleadoService.deleteEmpleado(id);

        // Mensaje de éxito flash que se muestra solo en la siguiente pantalla
        redirectAttributes.addFlashAttribute("exitoMensaje", "Empleado y su imagen de perfil eliminados correctamente.");

    } catch (Exception e) {
        // Si hay un error (ej. restricción de clave foránea o fallo de disco), lo notificamos
        redirectAttributes.addFlashAttribute("errorMensaje", "No se pudo eliminar al empleado: " + e.getMessage());
    }

    // Redirige al listado de empleados de la intranet
    return "redirect:/intranet/empleados/list";
}


    //....................... MODEL ATTRIBUTES .......................................
    @ModelAttribute("allGenders")
    public List<Genero> populateGenders(){ return Arrays.asList(Genero.values()); }

    @ModelAttribute("allDepartments")
    public List<Departamento> populateDepartments() { return departamentoService.findAll(); }
}
