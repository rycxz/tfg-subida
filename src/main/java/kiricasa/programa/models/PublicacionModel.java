package kiricasa.programa.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kiricasa.programa.enums.TipoPiso;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "publicaciones")
public class PublicacionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String descripcion;

    @CreationTimestamp
    private LocalDateTime fechaPublicacion;

    private String carpeta = "publicacion_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyyHHmmss"));
@Column(nullable = false)
    private String imagen;
    @Column(nullable = false)
    private String imagen2;
    @Column(nullable = false)
    private String imagen3;
    @Column(nullable = false)
    private String imagen4;
    @Column(nullable = false)
    private String imagen5;
    @Column(nullable = false)
    private String imagen6;
    @Column(nullable = false)
    private String imagen7;
    @Column(nullable = false)
    private String imagen8;
    @Column(nullable = false)
    private String imagen9;

    @Column(nullable = false)
    private String ubicacion;

    @Enumerated(EnumType.STRING)
    private TipoPiso tipo;

    private Integer  precio;
    private String estado;

    @Column(nullable = false)
    private int metrosCuadrados;

    private String habitaciones;
    private boolean permiteMascotas;
    private int numeroCompañeros;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @ToString.Exclude
    private UsuarioModel usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_barrio", nullable = false)
    @ToString.Exclude
    private BarriosModel barrio;

// Devuelve la lista cruda, sin predeterminadas
public List<String> getFotosOriginales() {
    return List.of(imagen, imagen2, imagen3, imagen4, imagen5, imagen6, imagen7, imagen8, imagen9);
}

public void setImagenPorIndice(int index, String valor) {
    switch (index) {
        case 0 -> this.imagen = valor;
        case 1 -> this.imagen2 = valor;
        case 2 -> this.imagen3 = valor;
        case 3 -> this.imagen4 = valor;
        case 4 -> this.imagen5 = valor;
        case 5 -> this.imagen6 = valor;
        case 6 -> this.imagen7 = valor;
        case 7 -> this.imagen8 = valor;
        case 8 -> this.imagen9 = valor;
    }
}


 public String getImagePrincipal() {
    return (imagen == null || imagen.isEmpty()) ? "predeterminada.png" : imagen;
}

    public List<String> getFotos() {
    List<String> imagenesOriginales = List.of(imagen, imagen2, imagen3, imagen4, imagen5, imagen6, imagen7, imagen8, imagen9);

    List<String> imagenes = new ArrayList<>();
    for (String img : imagenesOriginales) {
        if (img == null || img.isEmpty()) {
            imagenes.add("predeterminada.png");
        } else {
            imagenes.add(img);
        }
    }

    return imagenes;
}

public String getImagenPorIndice(int index) {
    return switch (index) {
        case 0 -> imagen;
        case 1 -> imagen2;
        case 2 -> imagen3;
        case 3 -> imagen4;
        case 4 -> imagen5;
        case 5 -> imagen6;
        case 6 -> imagen7;
        case 7 -> imagen8;
        case 8 -> imagen9;
        default -> null;
    };
}


    public String getCarpetaImagen() {
        return carpeta;
    }

    public void setCarpetaImagen(String carpetaImagen) {
        this.carpeta = carpetaImagen;
    }
}
