package progra4.prestamos.repository;

import progra4.prestamos.model.Aplicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AplicacionRepository extends JpaRepository<Aplicacion, Integer> {

    boolean existsByOferenteIdAndPuestoId(Integer oferenteId, Integer puestoId);

    Optional<Aplicacion> findByOferenteIdAndPuestoId(Integer oferenteId, Integer puestoId);

    List<Aplicacion> findByOferenteId(Integer oferenteId);

    List<Aplicacion> findByPuestoId(Integer puestoId);
}