package com.example.service.empleado;

import com.example.domain.model.entity.empleado.Correo;
import com.example.domain.model.entity.empleado.Empleado;
import com.example.domain.model.entity.empleado.Telefono;
import com.example.persistence.repository.empleado.CorreoRepository;
import com.example.persistence.repository.empleado.EmpleadoRepository;
import com.example.persistence.repository.empleado.TelefonoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/** Las 4 regls de oro de @Transactional (Propiedades ACID)
 * Atomicidad: El método es una "átomo", no se puede dividir. O se ejecutan todas las consultas SQL con éxito
 * o ninguna se guarda.
 * Consistencia: La base de datos pasa de un estado válido a otro estado válido, respetando todas las reglas
 * y restricciones.
 * Aislamiento (Isolation): Si dos usuarios intentan modificar o borrar al mismo empleado a la vez,
 * Spring y la base de datos los forman en fila para que no se alteren los datos del otro.
 * Durabilidad: Una vez que el método termina sin errores, los cambios se guardan de forma permanente en el disco
 * y no se perderán aunque el servidor se apague un segundo después.
 */

@Service
@RequiredArgsConstructor
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final CorreoRepository correoRepository;
    private final TelefonoRepository telefonoRepository;

    // Inyecta la propiedad y la convierte automáticamente en una List<String>
    @Value("#{'${app.upload.allowed-extensions}'.split(',')}")
    private List<String> allowedExtensions;

    //....................... findAll .......................................
    @Override
    @Transactional(readOnly = true)
    public List<Empleado> findAll() {
        return empleadoRepository.findAll();
    }

    //....................... findById .......................................
    @Override
    @Transactional(readOnly = true)
    public Empleado findById(Long id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El empleado con el ID " + id + " no existe en el sistema."));
    }

    //....................... save .......................................
    @Override
    @Transactional
    public Empleado save(Empleado empleado, String strTelefono, String strCorreo, MultipartFile image) {

        validarEmpleado(empleado);

        // Mapeamos las cadenas de texto a objetos y los asignamos de forma bidireccional
        empleado.setTelefonos(procesarTelefono(strTelefono, empleado));
        empleado.setEmails(procesarCorreo(strCorreo, empleado));

        validarFormatoCorreos(empleado.getEmails());
        validarCorreosUnicosAlCrear(empleado.getEmails());
        validarTelefonosUnicos(empleado.getTelefonos());

        // Primer Guardado: Genera el Long ID en la Base de Datos de manera obligatoria
        Empleado empleadoGuardado = empleadoRepository.save(empleado);

        // Procesamiento de imagen: Si se subió un archivo, lo procesamos usando el ID real obtenido
        if (image != null && !image.isEmpty()) {
            try {
                // Llama al método interno que creamos antes
                this.processEmpleadoImage(empleadoGuardado, image);

                // No hace falta un segundo .save() manual aquí, porque al estar bajo @Transactional,
                // Hibernate detectará el cambio en el atributo 'image' y hará el UPDATE automáticamente.
            } catch (IOException e) {
                throw new RuntimeException("Error crítico al guardar la imagen del empleado: " + e.getMessage(), e);
            }
        }

        return empleadoGuardado;
    }

    //....................... update .......................................
    @Override
    @Transactional
    public Empleado update(Long id, Empleado empleadoDetalles, String strTelefono, String strCorreo, MultipartFile image) {

        validarEmpleado(empleadoDetalles);

        Empleado empleadoExistente = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));

        // GESTIÓN DE LA IMAGEN EN MODIFICACIÓN
        if (image != null && !image.isEmpty()) {
            try {
                if (empleadoExistente.getImage() != null) {
                    Path rutaFotoAntigua = Paths.get("uploads/profiles/empleado").resolve(empleadoExistente.getImage());
                    Files.deleteIfExists(rutaFotoAntigua);
                }
                this.processEmpleadoImage(empleadoExistente, image);
            } catch (IOException e) {
                throw new RuntimeException("Error al actualizar el archivo físico de la imagen: " + e.getMessage(), e);
            }
        }

        // Sincronizamos los datos básicos del empleado
        empleadoExistente.setNombre(empleadoDetalles.getNombre());
        empleadoExistente.setPrimerApellido(empleadoDetalles.getPrimerApellido());
        empleadoExistente.setSegundoApellido(empleadoDetalles.getSegundoApellido());
        empleadoExistente.setGenero(empleadoDetalles.getGenero());
        empleadoExistente.setFechaAlta(empleadoDetalles.getFechaAlta());
        empleadoExistente.setSalario(empleadoDetalles.getSalario());
        empleadoExistente.setDepartamento(empleadoDetalles.getDepartamento());

        // Purgamos los datos: Sincronización Memoria ◄► Base de Datos
        if (empleadoExistente.getTelefonos() != null) {
            empleadoExistente.getTelefonos().clear();
        }
        if (empleadoExistente.getEmails() != null) {
            empleadoExistente.getEmails().clear();
        }

        // Forzamos a Hibernate a sincronizar los borrados en la BD en este mismo instante.
        // Esto elimina las filas antiguas y libera la restricción UNIQUE del correo inmediatamente.
        empleadoRepository.flush();

        // Procesamos los nuevos correos y teléfonos recibidos del formulario.
        Set<Telefono> nuevosTels = procesarTelefono(strTelefono, empleadoExistente);
        Set<Correo> nuevosCorreos = procesarCorreo(strCorreo, empleadoExistente);

        validarFormatoCorreos(nuevosCorreos);

        // Los correos viejos se borraron con el flush(), si este método encuentra algo, es de OTRA persona.
        validarCorreosUnicosAlCrear(nuevosCorreos);
        validarTelefonosUnicos(nuevosTels);

        // Volcamos las nuevas entidades limpias al objeto gestionado (addAll maneja vacíos de forma segura)
        empleadoExistente.getTelefonos().addAll(nuevosTels);
        empleadoExistente.getEmails().addAll(nuevosCorreos);

        // Guardamos y cerramos la transacción transaccional
        return empleadoRepository.saveAndFlush(empleadoExistente);
    }

    //....................... procesarTelefonos .......................................
    private Set<Telefono> procesarTelefono(String str, Empleado emp) {
        if (str == null || str.isBlank()) return new HashSet<>();

        return Arrays.stream(str.split(";"))
                .map(String::trim)                              //Quita espacios en blanco
                .filter(limpio -> !limpio.isEmpty())        //Solo pasan los valores con contenidi
                .map(numero -> Telefono.builder()           //Transforma cada cadena en un objeto
                        .numero(numero)
                        .empleado(emp)
                        .build())
                .collect(Collectors.toSet());                   //Agrupa los valores y los mete en la coleccion Set
    }

    //....................... procesarCorreo .......................................
    private Set<Correo> procesarCorreo(String str, Empleado emp) {
        if (str == null || str.isBlank())
            return new HashSet<>();

        return Arrays.stream(str.split(";"))
                .map(String::trim)
                .filter(limpio -> !limpio.isEmpty())
                .map(email -> Correo.builder()
                        .email(email)
                        .empleado(emp)
                        .build())
                .collect(Collectors.toSet());
    }

    //............................... Process image ..........................
    @Override
    public String processEmpleadoImage(Empleado empleado, MultipartFile image) throws IOException {

        // Si no se subió ningún archivo, mantenemos la imagen que ya tenía la entidad
        if (image == null || image.isEmpty()) {
            return empleado.getImage();
        }

        // Verificación de seguridad por si el ID sigue siendo null
        if (empleado.getId() == null) {
            throw new IllegalStateException("No se puede procesar la imagen porque el empleado no tiene un ID asignado. Asegúrate de guardarlo primero.");
        }

        // Definimos del directorio de almacenamiento
        Path uploadDir = Paths.get("uploads/profiles/empleado");

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        //  Obtención de la extensión del archivo
        String originalName = image.getOriginalFilename();
        String extension = ".jpg";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }
        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("Formato de archivo no permitido. Los formatos válidos son: " + allowedExtensions);
        }

        // Usamos el ID de la entidad en el nombre del archivo garantizando que si el mismo empleado sube otra foto,
        // se sobrescribirá la anterior automáticamente.
        String filename = "empleado-" + empleado.getId() + extension;

        Path filePath = uploadDir.resolve(filename);

        // Guardamos los bytes físicamente en el disco
        Files.write(filePath, image.getBytes());

        // Actualizamos el atributo de la entidad con el nombre definitivo del archivo
        empleado.setImage(filename);

        return filename;
    }

    //....................... VALIDACIONES .......................................

    private void validarEmpleado(Empleado emp) {
        if (emp.getSalario() == null || emp.getSalario().doubleValue() <= 0) throw new IllegalArgumentException("El salario debe ser mayor a 0.");
        if (emp.getFechaAlta() == null) throw new IllegalArgumentException("La fecha de alta es obligatoria.");
        if (emp.getDepartamento() == null || emp.getDepartamento().getId() == null) throw new IllegalArgumentException("Debe seleccionar un departamento.");
        System.out.println("[" + emp.getSegundoApellido() + "]");
    }

    private void validarFormatoCorreos(Set<Correo> correos) {
        if (correos != null) {
            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

            for (Correo correo : correos) {
                String emailTexto = correo.getEmail();

                if (emailTexto == null || !emailTexto.matches(emailRegex)) {
                    throw new IllegalArgumentException("El correo '" + (emailTexto != null ? emailTexto : "") + "' no tiene un formato válido (Ej: ejemplo@empresa.com).");
                }
            }
        }
    }

    // NUEVA VALIDACIÓN PARA EL MÉTODO SAVE (CREAR)
    private void validarCorreosUnicosAlCrear(Set<Correo> correos) {
        if (correos == null) return;
        for (Correo correo : correos) {
            Optional<Correo> correoExistente = correoRepository.findByEmail(correo.getEmail());
            if (correoExistente.isPresent()) {
                throw new IllegalArgumentException("El correo '" + correo.getEmail() + "' ya está registrado por otro empleado.");
            }
        }
    }

    private void validarTelefonosUnicos(Set<Telefono> telefonos) {
        if (telefonos == null) return;
        for (Telefono telefono : telefonos) {
            if (telefonoRepository.existsByNumero(telefono.getNumero())) {
                throw new IllegalArgumentException("El número de teléfono '" + telefono.getNumero() + "' ya está registrado por otro empleado.");
            }
        }
    }

    // ............................... Delete por ID ..........................
    @Override
    @Transactional
    public void deleteEmpleado(Long id) {
        // Buscamos el empleado para conocer el nombre de su imagen antes de borrarlo
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));

        String nombreImagen = empleado.getImage();
        empleadoRepository.deleteById(id);

        // Si el empleado tenía una imagen asignada, la borramos físicamente del disco
        if (nombreImagen != null && !nombreImagen.isEmpty()) {
            try {
                Path rutaArchivo = Paths.get("uploads/profiles/empleado").resolve(nombreImagen);
                // Borra el archivo solo si existe en el directorio, evitando excepciones
                Files.deleteIfExists(rutaArchivo);

            } catch (IOException e) {
                // Usamos un log o una excepción en tiempo de ejecución para no romper la firma del método
                throw new RuntimeException("Error al eliminar el archivo físico de la imagen: " + e.getMessage(), e);
            }
        }
    }

}
