package com.example.persistence.repository.empleado;


import com.example.domain.model.entity.empleado.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/* Para generar metodos, ademas de los que ya se tienen por defecto en la interfaces
 * de las cuales hereda JpaRepository hay que hacer suministrando la sintaxis correcta
 * como se indica en los enlaces siguientes:
 * Baeldung: https://www.baeldung.com/spring-data-derived-queries
 * Oficial: https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html // Buscar JPA Query Methods
 */

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    List<Empleado> findByNombre(String nombre);

}
