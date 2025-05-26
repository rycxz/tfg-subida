package kiricasa.programa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import kiricasa.programa.models.FavoritosModel;
import kiricasa.programa.models.PublicacionModel;
import kiricasa.programa.models.UsuarioModel;

@Repository
public interface FavoritosRepository extends JpaRepository<FavoritosModel, Long> {

    // Buscar favoritos por usuario
    List<FavoritosModel> findByUsuario(UsuarioModel usuario);

    // Buscar favoritos por publicación
    List<FavoritosModel> findByPublicacion(PublicacionModel publicacion);

    // Verificar si una publicación está marcada como favorita por un usuario
    Optional<FavoritosModel> findByUsuarioAndPublicacion(UsuarioModel usuario, PublicacionModel publicacion);

    // Contar favoritos de una publicación
    long countByPublicacion(PublicacionModel publicacion);

    // Eliminar un favorito por usuario y publicación
    void deleteByUsuarioAndPublicacion(UsuarioModel usuario, PublicacionModel publicacion);
        @Transactional
        void deleteByUsuario_Id(Long userId);
        void deleteByPublicacion_Id(Long publicacionId);

}
