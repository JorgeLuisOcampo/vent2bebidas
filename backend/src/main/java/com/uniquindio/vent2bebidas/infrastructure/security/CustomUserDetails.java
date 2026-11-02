package com.uniquindio.vent2bebidas.infrastructure.security;

import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Adapta nuestro UsuarioEntity a lo que Spring Security entiende (UserDetails).
public record CustomUserDetails(UsuarioEntity usuario) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(usuario.getRol().name())); // COMPRADOR o VENDEDOR
    }

    @Override
    public String getPassword() {
        return usuario.getPassword(); // el hash BCrypt
    }

    @Override
    public String getUsername() {
        return usuario.getEmail(); // se inicia sesión con el correo
    }
}