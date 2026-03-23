package progra4.prestamos.repository;

import progra4.prestamos.model.Caracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CaracteristicaRepository extends JpaRepository<Caracteristica, Integer> {
    List<Caracteristica> findByPadreIsNull();
    List<Caracteristica> findByPadreId(Integer padreId);
}