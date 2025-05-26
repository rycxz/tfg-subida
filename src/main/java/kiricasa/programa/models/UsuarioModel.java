/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package kiricasa.programa.models;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import kiricasa.programa.enums.UsuarioRol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author recur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")

public class UsuarioModel implements UserDetails {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private  String nombre;
    private String password;
    @Column(nullable = false, unique = true)
    private  String email;
    private String numero;
    private  boolean esAdmin;
       @Column(nullable = false)
    private boolean verificado = false;
    @Column(name = "codigo_recuperacion")
    private String codigoRecuperacion;

    @Column(name = "expiracion_codigo")
    private LocalDateTime expiracionCodigo;


    private LocalDate fechaNacimiento;
    @CreationTimestamp
    private LocalDateTime fechaRegistro;
    @UpdateTimestamp
    private LocalDateTime fechaAdmin;
    @Enumerated(EnumType.STRING)
    UsuarioRol rol;
    @Column(name="recibir_noti",nullable = false)
        private boolean recibirNotificaciones;
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
     @ToString.Exclude
    private List<PublicacionModel> anuncios;




    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.name()));
    }





      @Override
      public String getUsername() {
          return nombre;
      }

      @Override
      public boolean isAccountNonExpired() {
          return true;
      }

      @Override
      public boolean isAccountNonLocked() {
          return true;
      }

      @Override
      public boolean isCredentialsNonExpired() {
          return true;
      }

    @Override
    public boolean isEnabled() {
    return true;
    }


        }








