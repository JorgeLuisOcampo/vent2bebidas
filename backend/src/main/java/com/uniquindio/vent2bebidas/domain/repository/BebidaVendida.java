package com.uniquindio.vent2bebidas.domain.repository;

import java.util.UUID;

// Una fila del ranking de bebidas más vendidas. Es del dominio: no depende de JPA.
public record BebidaVendida(UUID bebidaId, String nombre, long unidadesVendidas, long numeroCompras) {
}
