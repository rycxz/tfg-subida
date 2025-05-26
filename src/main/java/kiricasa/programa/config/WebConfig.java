package kiricasa.programa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @SuppressWarnings("null")
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Esto hace que http://localhost:8080/uploads/** apunte a la carpeta real uploads/ en el proyecto la ruta es absoluta
        // cambia la ruta a la carpeta donde tienes los archivos subidos
registry.addResourceHandler("/uploads/**")
        .addResourceLocations("classpath:/static/uploads/");

    }
}
