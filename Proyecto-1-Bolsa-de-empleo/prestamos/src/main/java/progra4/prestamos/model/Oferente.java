package progra4.prestamos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "oferente")
public class Oferente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String identificacion;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String primerApellido;

    @Column(length = 80)
    private String nacionalidad;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @Column(length = 200)
    private String residencia;

    @Column(nullable = false, length = 255)
    private String clave;

    @Column(length = 300)
    private String curriculumPdf;

    @Column(nullable = false)
    private Boolean aprobado = false;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @OneToMany(mappedBy = "oferente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OferenteHabilidad> habilidades;
}