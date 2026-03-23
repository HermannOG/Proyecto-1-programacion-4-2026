package progra4.prestamos.security;

import progra4.prestamos.model.Administrador;
import progra4.prestamos.model.Empresa;
import progra4.prestamos.model.Oferente;
import progra4.prestamos.repository.AdministradorRepository;
import progra4.prestamos.repository.EmpresaRepository;
import progra4.prestamos.repository.OferenteRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final EmpresaRepository  empresaRepo;
    private final OferenteRepository oferenteRepo;
    private final AdministradorRepository adminRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Limpiar espacios por si acaso
        String user = username.trim();
        log.info("Intentando login con usuario: '{}'", user);

        // 1. Buscar como empresa por correo
        var empresa = empresaRepo.findByCorreo(user);
        if (empresa.isPresent()) {
            Empresa e = empresa.get();
            log.info("Encontrado como empresa. Aprobada: {}", e.getAprobada());
            if (!e.getAprobada()) throw new UsernameNotFoundException("Empresa no aprobada");
            return User.builder()
                    .username(e.getCorreo())
                    .password(e.getClave())
                    .roles("EMPRESA")
                    .build();
        }

        // 2. Buscar como oferente por correo
        var oferentePorCorreo = oferenteRepo.findByCorreo(user);
        if (oferentePorCorreo.isPresent()) {
            Oferente o = oferentePorCorreo.get();
            log.info("Encontrado como oferente por correo. Aprobado: {}", o.getAprobado());
            if (!o.getAprobado()) throw new UsernameNotFoundException("Oferente no aprobado");
            return User.builder()
                    .username(o.getCorreo())
                    .password(o.getClave())
                    .roles("OFERENTE")
                    .build();
        }

        // 3. Buscar como oferente por identificacion
        var oferentePorId = oferenteRepo.findByIdentificacion(user);
        if (oferentePorId.isPresent()) {
            Oferente o = oferentePorId.get();
            log.info("Encontrado como oferente por identificacion. Aprobado: {}", o.getAprobado());
            if (!o.getAprobado()) throw new UsernameNotFoundException("Oferente no aprobado");
            return User.builder()
                    .username(o.getCorreo())
                    .password(o.getClave())
                    .roles("OFERENTE")
                    .build();
        }

        // 4. Buscar como administrador por identificacion
        var admin = adminRepo.findByIdentificacion(user);
        if (admin.isPresent()) {
            Administrador a = admin.get();
            log.info("Encontrado como administrador: {}", a.getIdentificacion());
            return User.builder()
                    .username(a.getIdentificacion())
                    .password(a.getClave())
                    .roles("ADMIN")
                    .build();
        }

        log.warn("Usuario no encontrado: '{}'", user);
        throw new UsernameNotFoundException("Usuario no encontrado: " + user);
    }
}