package progra4.prestamos.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "administrador")
public class Administrador {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String identificacion;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 150)
    private String correo;

    @Column(nullable = false, length = 255)
    private String clave;
}