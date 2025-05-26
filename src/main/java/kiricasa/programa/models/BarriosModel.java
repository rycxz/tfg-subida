/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package kiricasa.programa.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author recur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "barrios")
public class BarriosModel {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String descripcion;
    private String imagen;
    @Column(nullable = false)
    private String ubicacion;
    @Column(name= "codigo_postal", nullable = true)
    private String codigoPostal;
    @Column(name = "rango_precios", nullable = true)
    private String rangoPrecios;
    @Column(name = "cantidad_anuncios", nullable = true)
    private int cantidadAnuncios;
}
