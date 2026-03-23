package progra4.prestamos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "aplicacion",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"oferente_id", "puesto_id"}
        ))
public class Aplicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oferente_id", nullable = false)
    private Oferente oferente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "puesto_id", nullable = false)
    private Puesto puesto;

    @Column(nullable = false)
    private LocalDateTime fechaAplicacion = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado estado = Estado.PENDIENTE;

    public enum Estado { PENDIENTE, REVISADO, RECHAZADO }
}