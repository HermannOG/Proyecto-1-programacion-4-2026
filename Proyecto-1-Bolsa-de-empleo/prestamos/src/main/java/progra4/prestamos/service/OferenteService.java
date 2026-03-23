package progra4.prestamos.service;

import progra4.prestamos.model.*;
import progra4.prestamos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OferenteService {

    private final OferenteRepository oferenteRepo;
    private final OferenteHabilidadRepository habilidadRepo;
    private final PuestoRepository puestoRepo;
    private final CaracteristicaRepository caracRepo;
    private final PasswordEncoder encoder;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public void registrar(Oferente o) {
        if (o.getCorreo() != null) {
            o.setCorreo(o.getCorreo().trim().toLowerCase());
        }
        if (o.getIdentificacion() != null) {
            o.setIdentificacion(o.getIdentificacion().trim());
        }
        if (o.getClave() == null || o.getClave().trim().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }

        if (oferenteRepo.existsByCorreo(o.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un oferente con ese correo.");
        }
        if (oferenteRepo.existsByIdentificacion(o.getIdentificacion())) {
            throw new IllegalArgumentException("Ya existe un oferente con esa identificación.");
        }

        o.setClave(encoder.encode(o.getClave()));
        o.setAprobado(false);
        o.setActivo(true);

        oferenteRepo.save(o);
    }

    public List<Oferente> pendientes() {
        return oferenteRepo.findByAprobadoFalse();
    }

    @Transactional
    public void aprobar(Integer id) {
        Oferente o = oferenteRepo.findById(id).orElseThrow();
        o.setAprobado(true);
        oferenteRepo.save(o);
    }

    public Oferente obtener(Integer id) {
        return oferenteRepo.findById(id).orElseThrow();
    }

    public List<OferenteHabilidad> misHabilidades() {
        return habilidadRepo.findByOferenteId(oferenteActual().getId());
    }

    @Transactional
    public void agregarHabilidad(Integer caracId, Integer nivel) {
        Oferente o = oferenteActual();
        OferenteHabilidad h = habilidadRepo
                .findByOferenteIdAndCaracteristicaId(o.getId(), caracId)
                .orElse(new OferenteHabilidad());
        h.setOferente(o);
        h.setCaracteristica(caracRepo.findById(caracId).orElseThrow());
        h.setNivel(nivel);
        habilidadRepo.save(h);
    }

    @Transactional
    public void eliminarHabilidad(Integer habilidadId) {
        habilidadRepo.deleteById(habilidadId);
    }

    @Transactional
    public void guardarCurriculum(MultipartFile archivo) {
        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            String nombre = "cv_" + oferenteActual().getId() + ".pdf";
            Path destino = dir.resolve(nombre);
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            Oferente o = oferenteActual();
            o.setCurriculumPdf(nombre);
            oferenteRepo.save(o);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el CV", e);
        }
    }

    public List<Map<String, Object>> buscarCandidatos(Integer puestoId) {
        Puesto puesto = puestoRepo.findById(puestoId).orElseThrow();
        List<PuestoCaracteristica> requisitos = puesto.getCaracteristicas();
        List<Oferente> todos = oferenteRepo.findAll().stream()
                .filter(Oferente::getAprobado)
                .filter(Oferente::getActivo)
                .toList();

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Oferente o : todos) {
            long cumplidos = requisitos.stream().filter(r -> {
                Optional<OferenteHabilidad> h =
                        habilidadRepo.findByOferenteIdAndCaracteristicaId(
                                o.getId(), r.getCaracteristica().getId());
                return h.isPresent() && h.get().getNivel() >= r.getNivelRequerido();
            }).count();

            if (cumplidos > 0) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("oferente", o);
                entry.put("cumplidos", cumplidos);
                entry.put("total", requisitos.size());
                double pct = requisitos.isEmpty() ? 0 : (cumplidos * 100.0 / requisitos.size());
                entry.put("porcentaje", String.format("%.2f%%", pct));
                resultado.add(entry);
            }
        }
        resultado.sort((a, b) -> Long.compare((long) b.get("cumplidos"), (long) a.get("cumplidos")));
        return resultado;
    }

    private Oferente oferenteActual() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return oferenteRepo.findByCorreo(correo.trim().toLowerCase()).orElseThrow();
    }
}