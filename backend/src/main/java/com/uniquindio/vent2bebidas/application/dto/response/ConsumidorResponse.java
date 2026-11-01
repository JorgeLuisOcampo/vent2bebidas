package com.uniquindio.vent2bebidas.application.dto.response;

import java.time.LocalDate;
import java.util.UUID;

// Perfil del consumidor hacia afuera. mayorDeEdad lo calcula el dominio.
public record ConsumidorResponse(
        UUID id,
        String nombre,
        String codigo,
        LocalDate fechaNacimiento,
        boolean mayorDeEdad,
        int comprasHistoricas
) {
}
