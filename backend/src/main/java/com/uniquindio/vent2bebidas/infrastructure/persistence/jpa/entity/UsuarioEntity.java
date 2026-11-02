package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Credenciales para iniciar sesión. Es un asunto de SEGURIDAD (infraestructura), no del dominio.
// El id es el MISMO del consumidor (si es COMPRADOR) o del vendedor (si es VENDEDOR):
// así el claim "uid" del token sirve para buscar al consumidor o al dueño de una bebida.
@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "email", length = 120, nullable = false, unique = true)
    private String email;

    // NUNCA la contraseña en texto plano: aquí se guarda el hash BCrypt ($2a$10$...).
    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "nombre", length = 120, nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", length = 20, nullable = false)
    private RolUsuario rol;

    public UsuarioEntity() {
        // requerido por JPA
    }

    public UsuarioEntity(String id, String email, String password, String nombre, RolUsuario rol) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
}
