package progra4.prestamos.service;

import progra4.prestamos.model.Caracteristica;
import progra4.prestamos.repository.CaracteristicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<Caracteristica> hojasDeNodo(Caracteristica nodo) {
        List<Caracteristica> resultado = new ArrayList<>();
        recolectarHojas(nodo, resultado);
        return resultado;
    }

    private void recolectarHojas(Caracteristica nodo, List<Caracteristica> resultado) {
        List<Caracteristica> hijos = nodo.getHijos();
        if (hijos == null || hijos.isEmpty()) {
            resultado.add(nodo);
        } else {
            for (Caracteristica hijo : hijos) {
                recolectarHojas(hijo, resultado);
            }
        }
    }

    public List<Caracteristica> ancestros(Caracteristica nodo) {
        List<Caracteristica> lista = new ArrayList<>();
        Caracteristica cursor = nodo.getPadre();
        while (cursor != null) {
            lista.add(0, cursor);
            cursor = cursor.getPadre();
        }
        return lista;
    }

    public List<Integer> expandirConDescendientes(List<Integer> ids) {
        List<Integer> expandidos = new ArrayList<>();
        for (Integer id : ids) {
            Caracteristica c = caracRepo.findById(id).orElse(null);
            if (c != null) {
                recolectarIds(c, expandidos);
            }
        }
        return expandidos.stream().distinct().toList();
    }

    private void recolectarIds(Caracteristica nodo, List<Integer> ids) {
        ids.add(nodo.getId());
        if (nodo.getHijos() != null) {
            for (Caracteristica hijo : nodo.getHijos()) {
                recolectarIds(hijo, ids);
            }
        }
    }
}