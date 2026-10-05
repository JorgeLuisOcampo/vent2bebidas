package com.uniquindio.vent2bebidas.application.dto.response;

import com.uniquindio.vent2bebidas.domain.valueobject.EstadoEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;

import java.util.UUID;

// Representa un Envase hacia afuera. usosRestantes es un dato calculado
// por el dominio (Envase.usosRestantes()), no se guarda.
public record EnvaseDetalleResponse(
        UUID id,
        MaterialEnvase material,
        int capacidadMl,
        boolean retornable,
        EstadoEnvase estado,
        int cantidadUsos,
        int usosRestantes
) {
}