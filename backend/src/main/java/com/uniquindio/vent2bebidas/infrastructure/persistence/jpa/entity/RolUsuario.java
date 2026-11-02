package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity;

// Roles de SEGURIDAD del sistema. Viajan en el claim "roles" del token JWT.
public enum RolUsuario {
    COMPRADOR,
    VENDEDOR
}
