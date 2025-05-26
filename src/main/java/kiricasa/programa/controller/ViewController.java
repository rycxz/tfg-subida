package kiricasa.programa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class ViewController {

    @GetMapping("/register")
    /**
     * Muestra la vista de registro
     * @return
     */
    public String showRegisterPage() {
        System.out.println("register--------------- pero por get");
        return "registro";
    }

    @GetMapping("/login")
    /**
     * Muestra la vista de login
     * @return
     */
    public String showLoginPage() {
        return "login";
    }
}
