//package com.example;
//
//import com.example.domain.model.entity.empleado.Empleado;
//import com.example.domain.model.entity.empleado.Correo;
//import com.example.domain.model.entity.empleado.Telefono;
//import com.example.domain.model.entity.empleado.Departamento;
//import com.example.domain.constant.Genero;
//import com.example.service.empleado.DepartamentoService;
//import com.example.service.empleado.EmpleadoService;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.Set;
//
//@Component
//@RequiredArgsConstructor
//public class empleadosDataInitializer implements ApplicationRunner {
//
//    private final EmpleadoService empleadoService;
//    private final DepartamentoService departamentoService;
//
//    private static final Logger logger = LoggerFactory.getLogger(empleadosDataInitializer.class);
//
//    @Override
//    @Transactional("empleadosTransactionManager")
//    public void run(ApplicationArguments args) {
//        logger.info("Insertando registros con comunicaciones en la construcción del Empleado...");
//
//        // 1. Insertar Departamentos primero (necesarios para la clave foránea)
//        Departamento srvIT = departamentoService.saveDepartamento(
//                Departamento.builder().nombre("Sistemas y Tecnología").build()
//        );
//        Departamento srvRRHH = departamentoService.saveDepartamento(
//                Departamento.builder().nombre("Recursos Humanos").build()
//        );
//
//        // 2. Construir e insertar Empleado (Jerónimo) con sus listas incluidas en una sola sentencia
//        Empleado jeronimo = Empleado.builder()
//                .nombre("Jerónimo")
//                .primerApellido("Arenal")
//                .segundoApellido("Gómez")
//                .genero(Genero.HOMBRE)
//                .fechaAlta(LocalDate.now())
//                .salario(new BigDecimal("2500.00"))
//                .departamento(srvIT)
//                // Usamos un truco de inicialización: creamos el empleado de forma diferida para pasárselo a los hijos
//                .emails(Set.of(Correo.builder().email("jeronimo.arenal@example.com").build()))
//                .telefonos(Set.of(Telefono.builder().numero("600111222").build(),
//                        Telefono.builder().numero("918581619").build()))
//                .build();
//
//        // Sincronizamos la relación antes de guardar (Obligatorio para Hibernate)
////        jeronimo.getEmails().forEach(correo -> correo.setEmpleado(jeronimo));
////        jeronimo.getTelefonos().forEach(tel -> tel.setEmpleado(jeronimo));
//        // Comentamos los métodos porque utilizamos el método de la entidad Empleado
//
//        empleadoService.save(jeronimo);
//
//        // 3. Construir e insertar Empleado (Esther) con sus listas incluidas
//        Empleado esther = Empleado.builder()
//                .nombre("Esther")
//                .primerApellido("Arenal")
//                .segundoApellido("Martínez")
//                .genero(Genero.MUJER)
//                .fechaAlta(LocalDate.now())
//                .salario(new BigDecimal("2500.00"))
//                .departamento(srvRRHH)
//                .emails(Set.of(Correo.builder().email("esther.arenal@example.com").build()))
//                .telefonos(Set.of(Telefono.builder().numero("600333444").build()))
//                .build();
//
//        esther.getEmails().forEach(correo -> correo.setEmpleado(esther));
//        esther.getTelefonos().forEach(tel -> tel.setEmpleado(esther));
//
//        empleadoService.save(esther);
//
//        logger.info("Inserción directa y en cascada finalizada con éxito.");
//    }
//}
