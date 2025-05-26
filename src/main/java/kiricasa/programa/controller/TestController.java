package kiricasa.programa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("mensaje", "Kiricasa está vivo 🎉");
        return "home";
    }

    @GetMapping("/test")
    public String test(Model model) {
        model.addAttribute("mensaje", "Vista de prueba");
        return "home";
    }
}
