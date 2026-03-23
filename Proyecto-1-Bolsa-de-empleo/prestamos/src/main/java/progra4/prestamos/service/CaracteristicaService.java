package progra4.prestamos.service;

import progra4.prestamos.model.Caracteristica;
import progra4.prestamos.repository.CaracteristicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CaracteristicaService {

    private final CaracteristicaRepository caracRepo;

    public List<Caracteristica> raices() {
        return caracRepo.findByPadreIsNull();
    }

    public List<Caracteristica> todasJerarquicas() {
        return caracRepo.findAll();
    }

    public List<Caracteristica> hojas() {
        return caracRepo.findAll().stream()
                .filter(c -> c.getHijos().isEmpty())
                .toList();
    }

    public Caracteristica obtener(Integer id) {
        return caracRepo.findById(id).orElseThrow();
    }

    public void crear(Caracteristica c, Integer padreId) {
        if (padreId != null && padreId != 0) {
            c.setPadre(caracRepo.findById(padreId).orElse(null));
        }
        caracRepo.save(c);
    }
}