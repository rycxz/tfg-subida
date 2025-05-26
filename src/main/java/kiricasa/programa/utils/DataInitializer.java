/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package kiricasa.programa.utils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import kiricasa.programa.enums.TipoPiso;
import kiricasa.programa.enums.UsuarioRol;
import kiricasa.programa.models.AnunciosVistosModel;
import kiricasa.programa.models.BarriosModel;
import kiricasa.programa.models.FavoritosModel;
import kiricasa.programa.models.PublicacionModel;
import kiricasa.programa.models.UsuarioModel;
import kiricasa.programa.repository.AnunciosVistosRepository;
import kiricasa.programa.repository.BarriosRepository;
import kiricasa.programa.repository.FavoritosRepository;
import kiricasa.programa.repository.PublicacionRepository;
import kiricasa.programa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author 6003194
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

        private final UsuarioRepository usuarioRepository;
        private final PublicacionRepository publicacionRepository;
        private final BarriosRepository barrioRepository;
        private final FavoritosRepository favoritosRepository;
        private final AnunciosVistosRepository anunciosVistosRepository;

        private final PasswordEncoder passwordEncoder;

        Faker faker = new Faker(new Locale("es"));
        @Override
        public void run(String... args) throws Exception {
            System.out.println(">>> DataInitializer se está ejecutando...");
       //modelos
            List<UsuarioModel> usuarios = usuarioRepository.findAll();
            List<PublicacionModel> publicaciones = publicacionRepository.findAll();



            //creo siempre un usuario administrador para que en caso de que no haya
            if (!usuarioRepository.findByEmail("admin@kiricasa.com").isPresent()) {
                UsuarioModel admin = UsuarioModel.builder()
                    .nombre("admin")
                    .password(passwordEncoder.encode("1234"))
                    .email("admin@kiricasa.com")
                    .numero("123456789")
                    .rol(UsuarioRol.ADMIN)
                    .fechaNacimiento(LocalDateTime.now().toLocalDate())
                    .fechaRegistro(LocalDateTime.now())
                    .fechaAdmin(LocalDateTime.now())
                    .esAdmin(true)
                    .recibirNotificaciones(true)
                    .build();
                usuarioRepository.save(admin);
            }
            //ahora mediante el faker creo 10 usuarios
            //es lo mas parecido al factory de laravel
            if (usuarioRepository.count() <= 1) {

            for (int i = 0; i < 10; i++) {
                UsuarioModel user = UsuarioModel.builder()
                    .nombre(faker.name().username())
                    .email(faker.internet().emailAddress())
                    .numero(faker.phoneNumber().cellPhone().replaceAll("[^0-9]", "").substring(0, 9))
                    .password(passwordEncoder.encode("test123"))
                    .fechaNacimiento(faker.date().birthday().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate())
                    .fechaRegistro(LocalDateTime.now())
                    .esAdmin(false)
                    .verificado(faker.bool().bool())
                    .rol(UsuarioRol.USER)
                    .recibirNotificaciones(faker.bool().bool())
                    .build();

                usuarioRepository.save(user);
            }
            System.out.println("Creando usuarios...");
        }
            //creo 10 barrios
            publicaciones = publicacionRepository.findAll();
            usuarios   = usuarioRepository.findAll();
            List<BarriosModel> barrios = barrioRepository.findAll();
            //recupero los barrios de la base  y compruebo antes d ehacer el insert
            //si no hay barrios, los creo
            if (barrioRepository.count() == 0) {
                System.out.println("Creando barrios...");
            barrios = List.of(
                new BarriosModel(null, "Delicias", "Barrio popular y multicultural con mucha vida comercial", "delicias.jpg", "Avenida de Madrid", "50017", "€450 - €800", 0),
                new BarriosModel(null, "Actur-Rey Fernando", "Moderno y bien comunicado, junto al río Ebro", "actur.jpg", "Calle Gertrudis Gómez de Avellaneda", "50018", "€500 - €900", 0),
                new BarriosModel(null, "San José", "Zona tradicional con buen acceso al centro y servicios", "sanjose.jpg", "Avenida San José", "50008", "€400 - €750", 0),
                new BarriosModel(null, "Centro", "Zona más céntrica, con comercios, cultura y ocio", "centro.jpg", "Paseo Independencia", "50001", "€600 - €1200", 0),
                new BarriosModel(null, "Universidad", "Barrio tranquilo con ambiente estudiantil", "universidad.jpg", "Calle Corona de Aragón", "50005", "€500 - €850", 0),
                new BarriosModel(null, "La Almozara", "Zona residencial junto al Ebro y Expo 2008", "almozara.jpg", "Avenida Pablo Gargallo", "50003", "€450 - €800", 0),
                new BarriosModel(null, "Torrero-La Paz", "Tradicional y familiar, con zonas verdes cercanas", "torrero.jpg", "Calle Fray Julián Garás", "50007", "€400 - €700", 0),
                new BarriosModel(null, "Las Fuentes", "Barrio con mucha historia y vida de barrio", "lasfuentes.jpg", "Compromiso de Caspe", "50002", "€400 - €750", 0),
                new BarriosModel(null, "Oliver-Valdefierro", "Zona en transformación con servicios y viviendas amplias", "valdefierro.jpg", "Calle Lagos de Coronas", "50011", "€420 - €750", 0),
                new BarriosModel(null, "Miralbueno", "Barrio residencial y tranquilo en expansión", "miralbueno.jpg", "Camino del Pilón", "50011", "€500 - €900", 0)
            );

                barrios.forEach(barrioRepository::save);
            }
            //creo 10 publicaciones
            if (publicacionRepository.count() == 0 && !usuarios.isEmpty() && !barrios.isEmpty()) {
                System.out.println("Creando publicaciones...");
            for (int i = 0; i < 10; i++) {
                PublicacionModel pub = new PublicacionModel();
                pub.setTitulo(faker.company().name());
                pub.setDescripcion(faker.lorem().paragraph(2));
                pub.setUbicacion(faker.address().streetAddress());
               pub.setTipo(faker.options().option(TipoPiso.HOMBRES, TipoPiso.MUJERES, TipoPiso.MIXTO, TipoPiso.SOLO));

                pub.setPrecio(faker.number().numberBetween(300, 1200));
                pub.setEstado(faker.options().option("Disponible", "Alquilado"));
                pub.setMetrosCuadrados(faker.number().numberBetween(30, 120));
                pub.setHabitaciones(String.valueOf(faker.number().numberBetween(1, 4)));
                pub.setPermiteMascotas(faker.bool().bool());
                pub.setNumeroCompañeros(faker.number().numberBetween(0, 3));
                pub.setUsuario(faker.options().nextElement(usuarios));
                pub.setBarrio(faker.options().nextElement(barrios));
                pub.setCarpeta("pisos_auto");  // carpeta donde están las imágenes fake
                pub.setImagen("imagen_0.jpg");
                pub.setImagen2("imagen_2.jpg");
                pub.setImagen3("imagen_3.jpg");
                pub.setImagen4("imagen_4.jpg");
                pub.setImagen5("imagen_5.jpg");
                pub.setImagen6("imagen_6.jpg");
                pub.setImagen7("imagen_7.jpg");
                pub.setImagen8("imagen_8.jpg");
                pub.setImagen9("imagen_9.jpg");

                publicacionRepository.save(pub);
            }
        }
            if (favoritosRepository.count() == 0 && !usuarios.isEmpty() && !publicaciones.isEmpty()) {
                System.out.println("Creando favoritos...");
                for (int i = 0; i < 10; i++) {
                    FavoritosModel fav = new FavoritosModel();
                    fav.setUsuario(faker.options().nextElement(usuarios));
                    fav.setPublicacion(faker.options().nextElement(publicaciones));
                    fav.setFechaGuardadoFavorito(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)));

                    favoritosRepository.save(fav);
                }
                System.out.println(" Favoritos creados correctamente.");
            }
            if (anunciosVistosRepository.count() == 0 && !usuarios.isEmpty() && !publicaciones.isEmpty()) {
                System.out.println("Creando anuncios vistos...");
                for (int i = 0; i < 15; i++) {
                    AnunciosVistosModel visto = new AnunciosVistosModel();
                    visto.setUsuario(faker.options().nextElement(usuarios));
                    visto.setPublicacion(faker.options().nextElement(publicaciones));
                    visto.setFechaVisto(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 10)));
                    visto.setContadorVisto(faker.number().numberBetween(1, 5));
                    visto.setContadorTipoPiso(faker.number().numberBetween(0, 3));

                    anunciosVistosRepository.save(visto);
                }
                System.out.println(" Anuncios vistos generados.");
            }





            System.out.println("Base de datos inicializada con usuarios por defecto.");
        }
}
