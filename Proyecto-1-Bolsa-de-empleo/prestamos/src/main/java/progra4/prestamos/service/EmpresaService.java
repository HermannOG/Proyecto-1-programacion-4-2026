package progra4.prestamos.service;

import progra4.prestamos.model.Empresa;
import progra4.prestamos.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepo;
    private final PasswordEncoder encoder;

    public void registrar(Empresa e) {
        if (empresaRepo.existsByCorreo(e.getCorreo())) {
            throw new IllegalArgumentException("Ya existe una empresa registrada con ese correo.");
        }
        e.setClave(encoder.encode(e.getClave()));
        empresaRepo.save(e);
    }

    public List<Empresa> pendientes() {
        return empresaRepo.findByAprobadaFalse();
    }

    @Transactional
    public void aprobar(Integer id) {
        Empresa e = empresaRepo.findById(id).orElseThrow();
        e.setAprobada(true);
        empresaRepo.save(e);
    }
}