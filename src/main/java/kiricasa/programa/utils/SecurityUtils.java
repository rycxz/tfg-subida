/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package kiricasa.programa.utils;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;
import kiricasa.programa.models.UsuarioModel;
import kiricasa.programa.repository.UsuarioRepository;
import kiricasa.programa.service.JwtService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author 6003194
 */
@Data
@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final JwtService jwtService;
    private final UsuarioRepository userRepository;

    /**
     * Constructor for SecurityUtils.
     * @param jwtService the JWT service to use for token operations
     * @param userRepository the user repository to use for user operations
     */

     public boolean isLogged(HttpSession session){
        String token = (String) session.getAttribute("jwt");
        if (token == null|| !jwtService.isTokenValid(token )){
            return false;
        }
        String email = jwtService.getUsernameFromToken(token);
        UsuarioModel user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return false;
        }
        String refreshedToken = jwtService.generateToken(user);
        session.setAttribute("jwt", refreshedToken);
        session.setAttribute("usuario", user);
        return true;
     }



}
