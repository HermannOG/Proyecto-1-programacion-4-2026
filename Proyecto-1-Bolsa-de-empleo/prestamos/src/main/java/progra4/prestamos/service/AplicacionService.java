package progra4.prestamos.service;

import progra4.prestamos.model.*;
import progra4.prestamos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AplicacionService {

    private final AplicacionRepository aplicacionRepo;
    private final OferenteRepository   oferenteRepo;
    private final PuestoRepository     puestoRepo;

    /**
     * Retorna true si el oferente autenticado ya aplicó al puesto.
     * Retorna false si no está autenticado o no ha aplicado.
     */
    public boolean yaAplico(Integer puestoId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return oferenteRepo.findByCorreo(auth.getName())
                .map(o -> aplicacionRepo.existsByOferenteIdAndPuestoId(o.getId(), puestoId))
                .orElse(false);
    }

    @Transactional
    public void aplicar(Integer puestoId) {
        String correo = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        Oferente oferente = oferenteRepo.findByCorreo(correo)
                .orElseThrow(() -> new IllegalStateException("Oferente no encontrado"));

        Puesto puesto = puestoRepo.findById(puestoId)
                .orElseThrow(() -> new IllegalArgumentException("Puesto no encontrado"));

        // Validar doble aplicación
        if (aplicacionRepo.existsByOferenteIdAndPuestoId(oferente.getId(), puestoId)) {
            throw new IllegalStateException("Ya aplicaste a este puesto anteriormente.");
        }

        Aplicacion a = new Aplicacion();
        a.setOferente(oferente);
        a.setPuesto(puesto);
        aplicacionRepo.save(a);
    }

    public List<Aplicacion> misAplicaciones() {
        String correo = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Oferente o = oferenteRepo.findByCorreo(correo).orElseThrow();
        return aplicacionRepo.findByOferenteId(o.getId());
    }
}