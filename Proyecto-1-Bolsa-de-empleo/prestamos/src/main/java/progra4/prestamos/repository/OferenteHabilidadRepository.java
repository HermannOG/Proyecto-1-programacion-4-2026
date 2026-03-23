package progra4.prestamos.repository;

import progra4.prestamos.model.OferenteHabilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OferenteHabilidadRepository extends JpaRepository<OferenteHabilidad, Integer> {
    List<OferenteHabilidad> findByOferenteId(Integer oferenteId);
    Optional<OferenteHabilidad> findByOferenteIdAndCaracteristicaId(Integer oferenteId,
                                                                    Integer caracteristicaId);
    void deleteByOferenteIdAndCaracteristicaId(Integer oferenteId, Integer caracteristicaId);
}