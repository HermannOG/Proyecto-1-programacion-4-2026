// ---- AdministradorRepository.java ----
package progra4.prestamos.repository;

import progra4.prestamos.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
    Optional<Administrador> findByIdentificacion(String identificacion);
}