/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package kiricasa.programa.jwt;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kiricasa.programa.service.JwtService;
import lombok.RequiredArgsConstructor;


/**
 *
 * @author recur
 */
@Component
@RequiredArgsConstructor
public class JWTAuthentificationFilter  extends OncePerRequestFilter{
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
@Override
    @SuppressWarnings("UseSpecificCatch")
    /**
     * * Método que se ejecuta para cada petición
     */
protected void doFilterInternal(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull FilterChain filterChain) throws ServletException, IOException {

    String path = request.getRequestURI();
System.out.println("→ Ruta actual: " + path);
System.out.println("→ ¿Es pública? " + isRutaPublica(path));

    System.out.println("→ Filtro ejecutado para ruta: " + path);


    if (isRutaPublica(path)) {
        filterChain.doFilter(request, response);
        return;
    }

    String token = getTokenFromRequest(request);


    if (token == null) {
        Object sessionToken = request.getSession(false) != null
                ? request.getSession().getAttribute("jwt")
                : null;
        if (sessionToken instanceof String) {
            token = (String) sessionToken;
        }
    }

    try {
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = jwtService.getUsernameFromToken(token);
            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
        System.out.println("Token expirado.");
        request.getSession().removeAttribute("jwt");
        request.getSession().invalidate();

        if (isRutaPublica(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/auth/login?expired=true");
        return;
    } catch (Exception e) {
        System.out.println("Token inválido o error: " + e.getMessage());
        request.getSession().removeAttribute("jwt");
        request.getSession().invalidate();

        if (isRutaPublica(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.sendRedirect("/auth/login");
        return;
    }


    filterChain.doFilter(request, response);
}

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    private boolean isRutaPublica(String path) {
    return path.equals("/auth/recupera") ||
           path.equals("/auth/nueva-password") ||
           path.equals("/auth/login") ||
           path.equals("/auth/register") ||
           path.startsWith("/css") ||
           path.startsWith("/js") ||
           path.startsWith("/images") ||
           path.startsWith("/uploads") ||
              path.startsWith("/variado") ||
           path.startsWith("/nl") ||
           path.equals("/") ||
           path.endsWith(".html");
}



}
