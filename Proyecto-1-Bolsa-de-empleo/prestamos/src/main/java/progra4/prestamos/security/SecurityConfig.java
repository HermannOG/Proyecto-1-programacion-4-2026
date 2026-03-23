package progra4.prestamos.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName(null);

        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(requestHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/puestos/**", "/registro/**",
                                "/login", "/logout", "/error",
                                "/css/**", "/js/**", "/images/,/puestos/*/aplicar/**").permitAll()
                        .requestMatchers("/empresa/**").hasAnyRole("EMPRESA", "ADMIN")
                        .requestMatchers("/oferente/**").hasAnyRole("OFERENTE", "ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureHandler((request, response, exception) -> {
                            String target = "/login?error=true";

                            if (exception instanceof LockedException) {
                                String msg = exception.getMessage();
                                if ("OFERENTE_NO_APROBADO".equals(msg)) {
                                    target = "/login?pendienteOferente=true";
                                } else if ("EMPRESA_NO_APROBADA".equals(msg)) {
                                    target = "/login?pendienteEmpresa=true";
                                }
                            } else if (exception instanceof DisabledException) {
                                target = "/login?inactivo=true";
                            }

                            response.sendRedirect(target);
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}