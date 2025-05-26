package kiricasa.programa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kiricasa.programa.enums.TipoPiso;
import kiricasa.programa.models.BarriosModel;
import kiricasa.programa.models.PublicacionModel;
import kiricasa.programa.models.UsuarioModel;

@Repository
public interface PublicacionRepository extends JpaRepository<PublicacionModel, Long> {

    // Buscar publicaciones por usuario
    List<PublicacionModel> findByUsuario(UsuarioModel usuario);

    // Buscar publicaciones por barrio
    List<PublicacionModel> findByBarrio(BarriosModel barrio);

    // Buscar publicaciones disponibles
    List<PublicacionModel> findByEstado(String estado);

    // Buscar publicaciones por tipo (piso, habitación, etc.)
 List<PublicacionModel> findByTipo(TipoPiso tipo);

List<PublicacionModel> findByUsuario_Id(Long id);

    // Buscar por usuario y estado
    List<PublicacionModel> findByUsuarioAndEstado(UsuarioModel usuario, String estado);

    // Buscar por barrio y estado
    List<PublicacionModel> findByBarrioAndEstado(BarriosModel barrio, String estado);

    // Buscar publicaciones que permitan mascotas
    List<PublicacionModel> findByPermiteMascotasTrue();

    // Buscar publicaciones con mínimo de metros cuadrados
    List<PublicacionModel> findByMetrosCuadradosGreaterThanEqual(int metros);

    List<PublicacionModel> findByTituloContainingIgnoreCaseOrUbicacionContainingIgnoreCase(String titulo, String ubicacion);


@Query("""
SELECT p FROM PublicacionModel p
WHERE (:titulo IS NULL OR LOWER(p.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
  AND (:ubicacion IS NULL OR LOWER(p.ubicacion) LIKE LOWER(CONCAT('%', :ubicacion, '%')))
  AND (:estado IS NULL OR LOWER(p.estado) LIKE LOWER(CONCAT('%', :estado, '%')))
  AND (:precioMax IS NULL OR p.precio <= :precioMax)
  AND (:metrosCuadradosMin IS NULL OR p.metrosCuadrados >= :metrosCuadradosMin)
  AND (:habitaciones IS NULL OR LOWER(p.habitaciones) LIKE LOWER(CONCAT('%', :habitaciones, '%')))
  AND (:permiteMascotas IS NULL OR p.permiteMascotas = :permiteMascotas)
  AND (:numeroCompañeros IS NULL OR p.numeroCompañeros = :numeroCompañeros)
  AND (
        :barrio IS NULL
     OR :barrio = ''
     OR LOWER(p.barrio.nombre) = LOWER(:barrio)
      )
""")
List<PublicacionModel> buscarConFiltros(
    @Param("titulo") String titulo,
    @Param("ubicacion") String ubicacion,
    @Param("estado") String estado,
    @Param("precioMax") Integer precioMax,
    @Param("metrosCuadradosMin") Integer metrosCuadradosMin,
    @Param("habitaciones") String habitaciones,
    @Param("permiteMascotas") Boolean permiteMascotas,
    @Param("numeroCompañeros") Integer numeroCompañeros,
    @Param("barrio") String barrio
);

List<PublicacionModel> findByBarrio_Id(Long id);


}
