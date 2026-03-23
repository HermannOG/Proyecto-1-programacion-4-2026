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
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final EmpresaRepository empresaRepo;
    private final OferenteRepository oferenteRepo;
    private final AdministradorRepository adminRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String user = username == null ? "" : username.trim();
        String userLower = user.toLowerCase();

        log.info("Intentando login con usuario: '{}'", user);

        // 1) Empresa por correo
        var empresa = empresaRepo.findByCorreo(userLower);
        if (empresa.isPresent()) {
            Empresa e = empresa.get();
            log.info("Encontrado como empresa. Aprobada: {}, Activa: {}", e.getAprobada(), e.getActiva());

            if (!Boolean.TRUE.equals(e.getAprobada())) {
                throw new LockedException("EMPRESA_NO_APROBADA");
            }
            if (!Boolean.TRUE.equals(e.getActiva())) {
                throw new DisabledException("EMPRESA_INACTIVA");
            }

            return User.builder()
                    .username(e.getCorreo())
                    .password(e.getClave())
                    .roles("EMPRESA")
                    .build();
        }

        // 2) Oferente por correo
        var oferentePorCorreo = oferenteRepo.findByCorreo(userLower);
        if (oferentePorCorreo.isPresent()) {
            Oferente o = oferentePorCorreo.get();
            log.info("Encontrado como oferente por correo. Aprobado: {}, Activo: {}", o.getAprobado(), o.getActivo());

            if (!Boolean.TRUE.equals(o.getAprobado())) {
                throw new LockedException("OFERENTE_NO_APROBADO");
            }
            if (!Boolean.TRUE.equals(o.getActivo())) {
                throw new DisabledException("OFERENTE_INACTIVO");
            }

            return User.builder()
                    .username(o.getCorreo())
                    .password(o.getClave())
                    .roles("OFERENTE")
                    .build();
        }

        // 3) Oferente por identificación
        var oferentePorId = oferenteRepo.findByIdentificacion(user);
        if (oferentePorId.isPresent()) {
            Oferente o = oferentePorId.get();
            log.info("Encontrado como oferente por identificación. Aprobado: {}, Activo: {}", o.getAprobado(), o.getActivo());

            if (!Boolean.TRUE.equals(o.getAprobado())) {
                throw new LockedException("OFERENTE_NO_APROBADO");
            }
            if (!Boolean.TRUE.equals(o.getActivo())) {
                throw new DisabledException("OFERENTE_INACTIVO");
            }

            return User.builder()
                    .username(o.getCorreo())
                    .password(o.getClave())
                    .roles("OFERENTE")
                    .build();
        }

        // 4) Admin por identificación
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
        throw new UsernameNotFoundException("USUARIO_NO_ENCONTRADO");
    }
}