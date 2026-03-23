package progra4.prestamos.repository;

import progra4.prestamos.model.Oferente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OferenteRepository extends JpaRepository<Oferente, Integer> {
    Optional<Oferente> findByCorreo(String correo);
    Optional<Oferente> findByIdentificacion(String identificacion);
    List<Oferente> findByAprobadoFalse();
    boolean existsByCorreo(String correo);
    boolean existsByIdentificacion(String identificacion);
}