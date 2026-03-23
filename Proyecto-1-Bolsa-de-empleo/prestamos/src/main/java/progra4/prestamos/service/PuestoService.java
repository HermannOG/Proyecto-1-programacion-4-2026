package progra4.prestamos.service;

import progra4.prestamos.model.*;
import progra4.prestamos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PuestoService {

    private final PuestoRepository puestoRepo;
    private final EmpresaRepository empresaRepo;
    private final CaracteristicaRepository caracRepo;

    public List<Puesto> ultimos5Publicos() {
        return puestoRepo.findTop5ByEsPublicoTrueAndActivoTrueOrderByFechaRegistroDesc();
    }

    public List<Puesto> buscarPorCaracteristicas(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return puestoRepo.findByEsPublicoTrueAndActivoTrue();
        return puestoRepo.findPublicosByCaracteristicas(ids);
    }

    public List<Puesto> puestosDeEmpresaActual() {
        Empresa e = empresaActual();
        return puestoRepo.findByEmpresaId(e.getId());
    }

    @Transactional
    public void publicar(Puesto puesto, List<Integer> caracIds, List<Integer> niveles) {
        puesto.setEmpresa(empresaActual());
        puesto.setCaracteristicas(new ArrayList<>());
        Puesto saved = puestoRepo.save(puesto);
        for (int i = 0; i < caracIds.size(); i++) {
            // Ignorar filas vacías del formulario
            if (caracIds.get(i) == null || caracIds.get(i) == 0) continue;
            PuestoCaracteristica pc = new PuestoCaracteristica();
            pc.setPuesto(saved);
            pc.setCaracteristica(caracRepo.findById(caracIds.get(i)).orElseThrow());
            pc.setNivelRequerido(niveles.get(i) != null ? niveles.get(i) : 1);
            saved.getCaracteristicas().add(pc);
        }
        puestoRepo.save(saved);
    }

    @Transactional
    public void desactivar(Integer id) {
        Puesto p = puestoRepo.findById(id).orElseThrow();
        p.setActivo(false);
        puestoRepo.save(p);
    }

    public Puesto obtener(Integer id) {
        return puestoRepo.findById(id).orElseThrow();
    }

    private Empresa empresaActual() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return empresaRepo.findByCorreo(correo).orElseThrow();
    }
}