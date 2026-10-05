package com.uniquindio.vent2bebidas.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

// Mapea a: Consumidor.verificarPuedeComprar(...) + registrarCompraExitosa()
// (caso de uso ConfirmarCompraDeBebidaUseCase)
public record ConfirmarCompraRequest(
        @NotNull UUID consumidorId,   // quién compra
        @NotNull UUID bebidaId,       // qué bebida compra
        @Min(1) int unidades          // cuántas unidades (la regla de volumen se valida en el dominio)
) {
}