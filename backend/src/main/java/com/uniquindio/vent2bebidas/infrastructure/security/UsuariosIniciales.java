package com.uniquindio.vent2bebidas.infrastructure.security;

import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.RolUsuario;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

// Crea los usuarios de prueba al arrancar (después de data.sql).
// Se hace en Java y no en data.sql porque la contraseña debe guardarse con BCrypt.
// Los ids coinciden con los vendedores y consumidores de data.sql.
@Component
public class UsuariosIniciales implements CommandLineRunner {

    private record Semilla(String id, String email, String clave, String nombre, RolUsuario rol) {
    }

    private static final List<Semilla> SEMILLAS = List.of(
            new Semilla("a1111111-1111-1111-1111-111111111111", "cerveceria@vent2bebidas.com", "Vendedor123*",
                    "Cerveceria del Quindio", RolUsuario.VENDEDOR),
            new Semilla("a2222222-2222-2222-2222-222222222222", "licores@vent2bebidas.com", "Vendedor123*",
                    "Licores La Montana", RolUsuario.VENDEDOR),
            new Semilla("c1111111-1111-1111-1111-111111111111", "ana@vent2bebidas.com", "Comprador123*",
                    "Ana Maria Gomez", RolUsuario.COMPRADOR),
            new Semilla("c2222222-2222-2222-2222-222222222222", "carlos@vent2bebidas.com", "Comprador123*",
                    "Carlos Ruiz", RolUsuario.COMPRADOR),
            new Semilla("c3333333-3333-3333-3333-333333333333", "sofia@vent2bebidas.com", "Comprador123*",
                    "Sofia Herrera", RolUsuario.COMPRADOR));

    private final UsuarioJpaDataRepository repository;
    private final PasswordEncoder encoder;

    public UsuariosIniciales(UsuarioJpaDataRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        for (Semilla s : SEMILLAS) {
            if (!repository.existsByEmail(s.email())) {
                repository.save(new UsuarioEntity(s.id(), s.email(), encoder.encode(s.clave()), s.nombre(), s.rol()));
            }
        }
    }
}