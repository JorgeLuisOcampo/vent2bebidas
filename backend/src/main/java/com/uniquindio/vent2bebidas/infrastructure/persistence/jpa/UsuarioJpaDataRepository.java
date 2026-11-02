package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa;

import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Público porque lo usa el paquete de seguridad (login y registro).
public interface UsuarioJpaDataRepository extends JpaRepository<UsuarioEntity, String> {

    Optional<UsuarioEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
