package kiricasa.programa.service;

import java.time.LocalDateTime;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import kiricasa.programa.models.UsuarioModel;
import kiricasa.programa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CorreoService {

    private final JavaMailSender mailSender;
    private final UsuarioRepository usuarioRepository;

    public void enviarCodigo(String destinatario, String codigo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Código de verificación - Kiricasa");
        mensaje.setText("Tu código de verificación es: " + codigo);
        mailSender.send(mensaje);
    }

    public void enviarCodigoRecuperacion(String email) {
        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email no registrado"));

        String codigo = String.valueOf((int)(Math.random() * 900000) + 100000);
        usuario.setCodigoRecuperacion(codigo);
        usuario.setExpiracionCodigo(LocalDateTime.now().plusMinutes(15));
        usuarioRepository.save(usuario);

        this.enviarCodigo(usuario.getEmail(), codigo); // ya no se llama a sí misma
    }
}
