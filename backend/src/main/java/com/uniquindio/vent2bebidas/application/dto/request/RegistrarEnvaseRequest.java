package com.uniquindio.vent2bebidas.application.dto.request;

import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Mapea a: Envase.registrar(...) (caso de uso RegistrarEnvaseUseCase)
// Que el aluminio NO pueda ser retornable lo valida el dominio (TipoEnvase), no el DTO.
public record RegistrarEnvaseRequest(
        @NotNull(message = "El material es obligatorio") MaterialEnvase material,
        @Positive(message = "La capacidad debe ser mayor que cero") int capacidadMl,
        boolean retornable
) {
}