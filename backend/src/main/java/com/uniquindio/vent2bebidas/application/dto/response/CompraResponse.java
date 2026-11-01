package com.uniquindio.vent2bebidas.application.dto.response;

import com.uniquindio.vent2bebidas.domain.valueobject.EstadoCompra;

import java.time.LocalDateTime;
import java.util.UUID;

// Representa una Compra hacia afuera. "total" lo calcula el dominio (Compra.total()), no se guarda.
public record CompraResponse(
        UUID id,
        UUID bebidaId,
        UUID vendedorId,
        int unidades,
        double precioUnitario,
        String moneda,
        double total,
        LocalDateTime fecha,
        EstadoCompra estado
) {
}
