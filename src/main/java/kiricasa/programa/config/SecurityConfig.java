package kiricasa.programa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import kiricasa.programa.jwt.JWTAuthentificationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JWTAuthentificationFilter jwtAuthentificationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    /**
     * * configura la seguridad de la aplicacion
     * @param http
     * @return
     * @throws Exception
     */
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.exceptionHandling(ex -> ex
    .accessDeniedHandler((request, response, accessDeniedException) -> {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
    })
    .authenticationEntryPoint((request, response, authException) -> {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autenticado");
    })
);

    return http
        //desactiva la proteccion csrf
        .csrf(csrf -> csrf.disable())
        //desactiva la proteccion de cabeceras
        .authorizeHttpRequests(auth -> auth
            .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
            //rutas permitidas
            .requestMatchers("/auth/**","/nl/**", "/css/**", "/js/**", "/images/**","/uploads/**","variado/**" ).permitAll()
            .anyRequest().authenticated()
        )

        .sessionManagement(sess -> sess
        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
    )

        //este código personaliza la autenticación y el cierre de sesión en la aplicación, asegurando que se manejen correctamente las sesiones y los tokens JWT.
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthentificationFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/nl/home")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )

        .build();

}

}
