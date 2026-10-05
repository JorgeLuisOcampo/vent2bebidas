package com.uniquindio.vent2bebidas.application.dto.response;

import com.uniquindio.vent2bebidas.domain.valueobject.EstadoBebida;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;

import java.util.UUID;

// Representa una Bebida hacia afuera: se aplana el dominio (Precio, TipoEnvase)
// para que el cliente no dependa de la estructura interna.
public record BebidaDetalleResponse(
        UUID id,
        UUID vendedorId,
        String nombre,
        MaterialEnvase material,
        int capacidadMl,
        boolean retornable,
        GrupoAlcoholico grupoAlcoholico,
        double monto,
        String moneda,
        EstadoBebida estado
) {
}