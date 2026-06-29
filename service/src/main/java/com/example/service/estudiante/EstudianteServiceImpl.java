package com.example.service.estudiante;

import com.example.domain.model.entity.empleado.Correo;
import com.example.domain.model.entity.empleado.Empleado;
import com.example.domain.model.entity.empleado.Telefono;
import com.example.domain.model.entity.estudiante.CorreoEs;
import com.example.domain.model.entity.estudiante.Estudiante;
import com.example.domain.model.entity.estudiante.TelefonoEs;
import com.example.persistence.repository.estudiante.CorreoEsRepository;
import com.example.persistence.repository.estudiante.EstudianteRepository;
import com.example.persistence.repository.estudiante.TelefonoEsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstudianteServiceImpl implements EstudianteService{

    private final EstudianteRepository estudianteRepository;
    private final CorreoEsRepository correoEsRepository;
    private final TelefonoEsRepository telefonoEsRepository;

    // Inyecta la propiedad y la convierte automáticamente en una List<String>
    @Value("#{'${app.upload.allowed-extensions}'.split(',')}")
    private List<String> allowedExtensions;

    //....................... findAll .......................................
    @Override
    @Transactional(readOnly = true)
    public List<Estudiante> findAll() {
        return estudianteRepository.findAll();
    }

    //....................... findById .......................................
    @Override
    @Transactional(readOnly = true)
    public Estudiante findById(Long id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El empleado con el ID " + id + " no existe en el sistema."));
    }

    //....................... save .......................................
    @Override
    @Transactional
    public Estudiante save(Estudiante estudiante, String strTelefono, String strCorreo, MultipartFile image) {
        validarEstudiante(estudiante);

        // Mapeamos las cadenas de texto a objetos y los asignamos de forma bidireccional
        estudiante.setTelefonos(procesarTelefono(strTelefono, estudiante));
        estudiante.setEmails(procesarCorreo(strCorreo, estudiante));

        validarFormatoCorreos(estudiante.getEmails());
        validarCorreosUnicosAlCrear(estudiante.getEmails());
        validarTelefonosUnicos(estudiante.getTelefonos());

        // Primer Guardado: Genera el Long ID en la Base de Datos de manera obligatoria
        Estudiante estudianteGuardado = estudianteRepository.save(estudiante);

        // Procesamiento de imagen: Si se subió un archivo, lo procesamos usando el ID real obtenido
        if (image != null && !image.isEmpty()) {
            try {
                // Llama al método interno que creamos antes
                this.processEstudianteImage(estudianteGuardado, image);

                // No hace falta un segundo .save() manual aquí, porque al estar bajo @Transactional,
                // Hibernate detectará el cambio en el atributo 'image' y hará el UPDATE automáticamente.
            } catch (IOException e) {
                throw new RuntimeException("Error crítico al guardar la imagen del empleado: " + e.getMessage(), e);
            }
        }

        return estudianteGuardado;

    }

    //....................... update .......................................
    @Override
    @Transactional
    public Estudiante update(Long id, Estudiante estudianteDetalles, String strTelefono, String strCorreo, MultipartFile image) {
        validarEstudiante(estudianteDetalles);

        Estudiante estudianteExistente = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con ID: " + id));

        // GESTIÓN DE LA IMAGEN EN MODIFICACIÓN
        if (image != null && !image.isEmpty()) {
            try {
                // Si ya tenía una imagen previa asignada en el disco, la borramos antes de guardar la nueva
                if (estudianteExistente.getImage() != null) {
                    Path rutaFotoAntigua = Paths.get("uploads/profiles/estudiante").resolve(estudianteExistente.getImage());
                    Files.deleteIfExists(rutaFotoAntigua);
                }

                // Procesamos y guardamos físicamente la nueva imagen usando el ID existente
                this.processEstudianteImage(estudianteExistente, image);

            } catch (IOException e) {
                throw new RuntimeException("Error al actualizar el archivo físico de la imagen: " + e.getMessage(), e);
            }
        }
        // Si 'image' viene vacío, la entidad mantiene intacto su valor actual de 'image'
        // gracias al campo oculto <input type="hidden" th:field="*{image}" /> del formulario.

        // Sincronizamos los datos básicos del empleado
        estudianteExistente.setNombre(estudianteDetalles.getNombre());
        estudianteExistente.setPrimerApellido(estudianteDetalles.getPrimerApellido());
        estudianteExistente.setSegundoApellido(estudianteDetalles.getSegundoApellido());
        estudianteExistente.setGenero(estudianteDetalles.getGenero());
        estudianteExistente.setFechaNacimiento(estudianteDetalles.getFechaNacimiento());
        estudianteExistente.setBeca(estudianteDetalles.getBeca());
        estudianteExistente.setTotalAsignaturas(estudianteDetalles.getTotalAsignaturas());
        estudianteExistente.setFacultad(estudianteDetalles.getFacultad());

        // Purgamos los datos: Sincronización Memoria ◄► Base de Datos
        if (estudianteExistente.getTelefonos() != null) {
            estudianteExistente.getTelefonos().clear(); // Vacía el Set en memoria (activa orphanRemoval)
        }
        if (estudianteExistente.getEmails() != null) {
            estudianteExistente.getEmails().clear();    // Vacía el Set en memoria (activa orphanRemoval)
        }

        // Forzamos a Hibernate a sincronizar los borrados en la BD en este mismo instante.
        // Esto elimina las filas antiguas y libera la restricción UNIQUE del correo inmediatamente.
        estudianteRepository.flush();

        // Procesamos los nuevos correos y teléfonos recibidos del formulario.
        Set<TelefonoEs> nuevosTels = procesarTelefono(strTelefono, estudianteExistente);
        Set<CorreoEs> nuevosCorreos = procesarCorreo(strCorreo, estudianteExistente);

        validarFormatoCorreos(nuevosCorreos);

        // CAMBIO CLAVE: Como tus correos viejos ya se borraron con el flush(),
        // si este método encuentra algo, es 100% seguro que le pertenece a OTRA persona.
        validarCorreosUnicosAlCrear(nuevosCorreos);
        validarTelefonosUnicos(nuevosTels);

        estudianteExistente.getTelefonos().addAll(nuevosTels);
        estudianteExistente.getEmails().addAll(nuevosCorreos);

        // Guardamos y cerramos la transacción transaccional
        return estudianteRepository.saveAndFlush(estudianteExistente);
    }

    //....................... procesarTelefonos .......................................
    private Set<TelefonoEs> procesarTelefono(String str, Estudiante estud) {
        if (str == null || str.isBlank()) return new HashSet<>();
        return Arrays.stream(str.split(";"))
                .map(String::trim)                              //Quita espacios en blanco
                .filter(limpio -> !limpio.isEmpty())        //Solo pasan los valores con contenidi
                .map(numero -> TelefonoEs.builder()           //Transforma cada cadena en un objeto
                        .numero(numero)
                        .estudiante(estud)
                        .build())
                .collect(Collectors.toSet());                   //Agrupa los valores y los mete en la coleccion Set
    }

    //....................... procesarCorreo .......................................
    private Set<CorreoEs> procesarCorreo(String str, Estudiante estud) {
        if (str == null || str.isBlank()) return new HashSet<>();

        return Arrays.stream(str.split(";"))
                .map(String::trim)
                .filter(limpio -> !limpio.isEmpty())
                .map(email -> CorreoEs.builder()
                        .email(email)
                        .estudiante(estud)
                        .build())
                .collect(Collectors.toSet());
    }

    //............................... Process image ..........................
    public void processEstudianteImage(Estudiante estudiante, MultipartFile image) throws IOException {

        // Si no se subió ningún archivo, mantenemos la imagen que ya tenía la entidad
        if (image == null || image.isEmpty()) {
            return;
        }

        // Verificación de seguridad por si el ID sigue siendo null
        if (estudiante.getId() == null) {
            throw new IllegalStateException("No se puede procesar la imagen porque el estudiante no tiene un ID asignado. Asegúrate de guardarlo primero.");
        }

        // Definimos del directorio de almacenamiento
        Path uploadDir = Paths.get("uploads/profiles/estudiante");

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
        String filename = "estudiante-" + estudiante.getId() + extension;

        Path filePath = uploadDir.resolve(filename);

        // Guardamos los bytes físicamente en el disco
        Files.write(filePath, image.getBytes());

        // Actualizamos el atributo de la entidad con el nombre definitivo del archivo
        estudiante.setImage(filename);

    }

    //....................... VALIDACIONES .......................................

    private void validarEstudiante(Estudiante estud) {
        if (estud.getBeca() == null || estud.getBeca().doubleValue() <= 0) throw new IllegalArgumentException("El salario debe ser mayor a 0.");
        if (estud.getFechaNacimiento() == null) throw new IllegalArgumentException("La fecha de alta es obligatoria.");
        if (estud.getFacultad() == null || estud.getFacultad().getId() == null) throw new IllegalArgumentException("Debe seleccionar un departamento.");
    }

    private void validarFormatoCorreos(Set<CorreoEs> correos) {
        if (correos != null) {
            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

            for (CorreoEs correo : correos) {
                String emailTexto = correo.getEmail();

                if (emailTexto == null || !emailTexto.matches(emailRegex)) {
                    throw new IllegalArgumentException("El correo '" + (emailTexto != null ? emailTexto : "") + "' no tiene un formato válido (Ej: ejemplo@empresa.com).");
                }
            }
        }
    }

    // NUEVA VALIDACIÓN PARA EL MÉTODO SAVE (CREAR)
    private void validarCorreosUnicosAlCrear(Set<CorreoEs> correos) {
        if (correos == null) return;
        for (CorreoEs correo : correos) {
            Optional<CorreoEs> correoExistente = correoEsRepository.findByEmail(correo.getEmail());
            if (correoExistente.isPresent()) {
                throw new IllegalArgumentException("El correo '" + correo.getEmail() + "' ya está registrado por otro empleado.");
            }
        }
    }

    private void validarTelefonosUnicos(Set<TelefonoEs> telefonos) {
        if (telefonos == null) return;
        for (TelefonoEs telefono : telefonos) {
            if (telefonoEsRepository.existsByNumero(telefono.getNumero())) {
                throw new IllegalArgumentException("El número de teléfono '" + telefono.getNumero() + "' ya está registrado por otro empleado.");
            }
        }
    }

    // ............................... Delete por ID ..........................
    @Override
    @Transactional
    public void deleteEstudiante(Long id) {
        // Buscamos el estudiante para conocer el nombre de su imagen antes de borrarlo
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con ID: " + id));

        String nombreImagen = estudiante.getImage();

        // Eliminamos el registro de la base de datos
        estudianteRepository.deleteById(id);

        // Si el estudiante tenía una imagen asignada, la borramos físicamente del disco
        if (nombreImagen != null && !nombreImagen.isEmpty()) {
            try {
                Path rutaArchivo = Paths.get("uploads/profiles/estudiante").resolve(nombreImagen);

                // Borra el archivo solo si existe en el directorio, evitando excepciones
                Files.deleteIfExists(rutaArchivo);

            } catch (IOException e) {
                // Usamos un log o una excepción en tiempo de ejecución para no romper la firma del método
                throw new RuntimeException("Error al eliminar el archivo físico de la imagen: " + e.getMessage(), e);
            }
        }
    }
}
