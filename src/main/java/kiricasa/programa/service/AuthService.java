package kiricasa.programa.service;


import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import kiricasa.programa.enums.UsuarioRol;
import kiricasa.programa.models.UsuarioModel;
import kiricasa.programa.repository.UsuarioRepository;
import kiricasa.programa.requests.LoginRequest;
import kiricasa.programa.requests.RegisterRequest;
import kiricasa.programa.response.AuthReponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

     private final UsuarioRepository usuarioRepository;
     private final JwtService jwtService;
     private final PasswordEncoder passwordEncoder;
     private final AuthenticationManager authenticationManager;



     public AuthReponse login(LoginRequest request) {


      try {
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(request.getNombre(), request.getPassword())
          );

      } catch (AuthenticationException e) {

          throw e; // relanzamos para mantener el comportamiento original (403)
      }

      UserDetails userdetails = usuarioRepository.findByNombreIgnoreCase(request.getNombre())
              .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        System.out.println("userdetails: " + userdetails);
      String token = jwtService.getToken(userdetails);
      return AuthReponse.builder().token(token).build();
  }

    /**
     * metodo que recibe el requets para crear un nuevo objeto usuario con el builder  lo guarda en la BBDD
     * y devuelve el token de acceso
     * @param request
     * @return
     */
    public AuthReponse register(RegisterRequest request) {
      // Verificar duplicados
      if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
          throw new RuntimeException("El email ya está registrado");
      }
       if (usuarioRepository.findByNombreIgnoreCase(request.getNombre()).isPresent()) {
        throw new RuntimeException("El nombre de usuario ya está en uso");
    }



      UsuarioModel usuario = UsuarioModel.builder()
              .nombre(request.getNombre())
              .password(passwordEncoder.encode(request.getPassword()))
              .email(request.getEmail())
              .numero(request.getNumero())
              .verificado(false)
              .esAdmin(false)
              .fechaNacimiento(request.getFechaNacimiento())
              .fechaRegistro(LocalDateTime.now())
              .fechaAdmin(null)
              .rol(UsuarioRol.USER)
              .recibirNotificaciones(request.isRecibirNotificaciones())
              .build();

      usuarioRepository.save(usuario);

      String token = jwtService.getToken(usuario);


      return AuthReponse.builder()
              .token(token)
              .build();
  }
public void cambiarPasswordConCodigo(String email, String codigo, String nuevaPassword) {
    UsuarioModel usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Email no registrado"));

    if (usuario.getCodigoRecuperacion() == null || usuario.getExpiracionCodigo() == null) {
        throw new RuntimeException("No hay solicitud de recuperación activa");
    }

    if (!codigo.equals(usuario.getCodigoRecuperacion())) {
        throw new RuntimeException("Código incorrecto");
    }

    if (usuario.getExpiracionCodigo().isBefore(LocalDateTime.now())) {
        throw new RuntimeException("El código ha expirado");
    }

    // Actualizamos la contraseña
    usuario.setPassword(passwordEncoder.encode(nuevaPassword));

    // Limpiamos los datos temporales
    usuario.setCodigoRecuperacion(null);
    usuario.setExpiracionCodigo(null);

    usuarioRepository.save(usuario);
}




}
