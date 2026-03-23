package progra4.prestamos.repository;

import progra4.prestamos.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    Optional<Empresa> findByCorreo(String correo);
    List<Empresa> findByAprobadaFalse();
    boolean existsByCorreo(String correo);
}