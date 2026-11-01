package com.uniquindio.vent2bebidas.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

// Mapea a: ConfirmarCompraDeBebidaUseCase -> Consumidor.verificarPuedeComprar(...) + Compra.confirmar(...)
// El consumidor NO viaja en el cuerpo: se toma del claim "uid" del token JWT,
// así nadie puede comprar a nombre de otra persona.
public record ConfirmarCompraRequest(
        @NotNull(message = "La bebida es obligatoria") UUID bebidaId, // qué bebida compra
        @Min(value = 1, message = "Debe comprar al menos una unidad") int unidades // el máximo lo valida el dominio
) {
}
