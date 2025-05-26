package kiricasa.programa.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import kiricasa.programa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;



/**
 * clase de configuración de la aplicación para la gestión de autenticación y seguridad.
 *
 * proporciona beans para AuthenticationManager, AuthenticationProvider, PasswordEncoder y UserDetailsService,
 * utilizando el repositorio de usuarios para la autenticación personalizada.
 *
 * @author recur
 */
@Configuration
@RequiredArgsConstructor
/**
 * * @author recur
 *
 */
public class ApplicationConfig {
        private final UsuarioRepository usuarioRepository;
    @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
        @Bean
        public AuthenticationProvider authenticationProvider () {
              DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
                provider.setUserDetailsService(authenticationService());
                provider.setPasswordEncoder(passwordEncoder());
                return provider;
        }
        @Bean
        public PasswordEncoder passwordEncoder() {
           return new BCryptPasswordEncoder();
        }
        @Bean
        public UserDetailsService authenticationService() {
            return nombre -> usuarioRepository.findByNombreIgnoreCase(nombre)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        }

        }

