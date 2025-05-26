package kiricasa.programa.controller;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kiricasa.programa.models.UsuarioModel;
import kiricasa.programa.repository.UsuarioRepository;
import kiricasa.programa.requests.LoginRequest;
import kiricasa.programa.requests.RegisterRequest;
import kiricasa.programa.service.AuthService;
import kiricasa.programa.service.CorreoService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final CorreoService correoService;


   @PostMapping("/login")
   /**
    * * Muestra la vista de login
    * @param request
    * @param session
    * @param redirectAttributes
    * @return
    */
public String login(
        @ModelAttribute LoginRequest request,
        HttpSession session,
        RedirectAttributes redirectAttributes
) {
    try {
        // Intentamos autenticar
        String token = authService.login(request).getToken();
        session.setAttribute("jwt", token);

        UsuarioModel usuario = usuarioRepository
            .findByNombreIgnoreCase(request.getNombre())
            .orElse(null);
        session.setAttribute("usuario", usuario);

        return "redirect:/home";
    } catch (Exception ex) {
        // Capturamos cualquier excepción de credenciales inválidas
        redirectAttributes.addFlashAttribute("loginError", "Usuario o contraseña incorrectos");
        return "redirect:/auth/login";
    }
}

    @PostMapping("/register")
public String register(
        @Valid @ModelAttribute("registerRequest") RegisterRequest request,
        BindingResult bindingResult,
        Model model,
        HttpSession session
) {
        if (bindingResult.hasErrors()) {
        model.addAttribute("org.springframework.validation.BindingResult.registerRequest", bindingResult);
        model.addAttribute("registerRequest", request);
        return "registro";
    }
        if(request.getFechaNacimiento() != null && request.getFechaNacimiento().getYear() < 1950) {
             model.addAttribute("registerRequest", request);
            model.addAttribute("error", "La fecha de nacimiento no puede ser anterior a 1900");
            return "registro";
        }
         if (!request.getNumero().matches("\\d{9,15}")) {
        model.addAttribute("registerRequest", request);
        model.addAttribute("error", "El número de teléfono debe contener entre 9 y 15 dígitos.");
        return "registro";
    }
    try {
        // Intentamos registrar al usuario
        String token = authService.register(request).getToken();
        session.setAttribute("jwt", token);

        UsuarioModel usuario = usuarioRepository
                .findByNombreIgnoreCase(request.getNombre())
                .orElse(null);
        session.setAttribute("usuario", usuario);

        return "redirect:/home";

    } catch (RuntimeException ex) {
        // Capturamos errores como email, nombre o número duplicado
        model.addAttribute("registerRequest", request);
        model.addAttribute("error", ex.getMessage());
       return "registro";
    }
}



@GetMapping("/recupera")
/**
 * * Muestra el formulario de recuperación de contraseña
 */
public String mostrarFormularioRecuperacion() {

    return "recupera";
}
@PostMapping("/recupera")
/**
 * * Procesa el formulario de recuperación de contraseña
 * @param email
 */
public String procesarFormularioRecuperacion(
        @RequestParam("email") String email,
        Model model) {
    System.out.println("Email recibido: " + email);
    try {
        correoService.enviarCodigoRecuperacion(email);
        model.addAttribute("email", email);
        return "introduce-codigo"; // OK: solo si el email es válido
    } catch (UsernameNotFoundException e) {
        model.addAttribute("error", "No existe un usuario con ese correo");
        model.addAttribute("email", email); // opcional: para que el campo quede rellenado
        return "recupera"; // Volver a la página original
    }
}

@PostMapping("/nueva-password")
/**
 * * Procesa el formulario de nueva contraseña
 * @param email
 * @param codigo
 * @param nuevaPassword
 * @param model
 * @return
 */
public String cambiarPassword(
        @RequestParam("email") String email,
        @RequestParam("codigo") String codigo,
        @RequestParam("nuevaPassword") String nuevaPassword,
        Model model) {

    try {
        authService.cambiarPasswordConCodigo(email, codigo, nuevaPassword);
        return "redirect:/auth/login? ";
    } catch (RuntimeException e) {
        model.addAttribute("error", e.getMessage());
        model.addAttribute("email", email);
        return "introduce-codigo";
    }
}



}




