package kiricasa.programa.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kiricasa.programa.enums.UsuarioRol;
import kiricasa.programa.models.UsuarioModel;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

   // Buscar por email
    Optional<UsuarioModel> findByEmail(String email);


    // Buscar por nombre ignorando mayúsculas/minúsculas
    Optional<UsuarioModel> findByNombreIgnoreCase(String nombre);


    // Buscar por rol
    List<UsuarioModel> findByRol(UsuarioRol rol);

    // Buscar por rol y esAdmin
    List<UsuarioModel> findByRolAndEsAdmin(UsuarioRol rol, boolean esAdmin);

    // Buscar por fecha de registro entre dos fechas
    List<UsuarioModel> findByFechaRegistroBetween(LocalDateTime inicio, LocalDateTime fin);

    // Buscar usuarios que son administradores
    List<UsuarioModel> findByEsAdminTrue();

    // Buscar usuarios que no son administradores
    List<UsuarioModel> findByEsAdminFalse();

    // Buscar por número de teléfono
    List<UsuarioModel> findByNumero(String numero);

    // Buscar por nombre que comience con una cadena específica
    List<UsuarioModel> findByNombreStartingWith(String prefijo);

    // Buscar por nombre que termine con una cadena específica
    List<UsuarioModel> findByNombreEndingWith(String sufijo);

    // Buscar por nombre que contenga una cadena específica
    List<UsuarioModel> findByNombreContaining(String fragmento);

    // Buscar por nombre o email
    List<UsuarioModel> findByNombreOrEmail(String nombre, String email);

    // Buscar por rol y ordenar por nombre ascendente
    List<UsuarioModel> findByRolOrderByNombreAsc(UsuarioRol rol);

    // Buscar por rol y ordenar por nombre descendente
    List<UsuarioModel> findByRolOrderByNombreDesc(UsuarioRol rol);


    boolean existsByEmailIgnoreCase(String email);



}
