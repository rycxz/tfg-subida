package kiricasa.programa.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import kiricasa.programa.models.AnunciosVistosModel;
import kiricasa.programa.models.PublicacionModel;
import kiricasa.programa.models.UsuarioModel;

@Repository
public interface AnunciosVistosRepository extends JpaRepository<AnunciosVistosModel, Long> {

         void deleteByPublicacionId(Long publicacionId);
    // Buscar por usuario
    List<AnunciosVistosModel> findByUsuario(UsuarioModel usuario);

    // Buscar por publicación
    List<AnunciosVistosModel> findByPublicacion(PublicacionModel publicacion);
  /**
     * Spring Data JPA genera un DELETE
     * sobre favoritos por la propiedad usuario.id
     */
    @Transactional
    void deleteByUsuario_Id(Long userId);
    void deleteByPublicacion_Id(Long publicacionId);

    // Buscar por usuario y publicación
    Optional<AnunciosVistosModel> findByUsuarioAndPublicacion(UsuarioModel usuario, PublicacionModel publicacion);

    // Contar cuántas veces un usuario ha visto una publicación
    long countByUsuarioAndPublicacion(UsuarioModel usuario, PublicacionModel publicacion);

    // Obtener los más vistos por un usuario, ordenados por contador
    List<AnunciosVistosModel> findByUsuarioOrderByContadorVistoDesc(UsuarioModel usuario);

    // Obtener por publicación y ordenar por fecha
    List<AnunciosVistosModel> findByPublicacionOrderByFechaVistoDesc(PublicacionModel publicacion);
}
