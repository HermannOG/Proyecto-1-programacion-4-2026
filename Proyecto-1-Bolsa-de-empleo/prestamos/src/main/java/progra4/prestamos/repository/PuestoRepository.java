package progra4.prestamos.repository;

import progra4.prestamos.model.Puesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface PuestoRepository extends JpaRepository<Puesto, Integer> {

    List<Puesto> findTop5ByEsPublicoTrueAndActivoTrueOrderByFechaRegistroDesc();

    List<Puesto> findByEsPublicoTrueAndActivoTrue();

    List<Puesto> findByEmpresaId(Integer empresaId);

    @Query("SELECT p FROM Puesto p WHERE p.fechaRegistro BETWEEN :inicio AND :fin")
    List<Puesto> findByRangoDeFechas(@Param("inicio") LocalDateTime inicio,
                                     @Param("fin")   LocalDateTime fin);

    @Query("""
        SELECT DISTINCT p FROM Puesto p
        JOIN p.caracteristicas pc
        WHERE p.esPublico = true AND p.activo = true
          AND pc.caracteristica.id IN :ids
        """)
    List<Puesto> findPublicosByCaracteristicas(@Param("ids") List<Integer> ids);
}