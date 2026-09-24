package com.uniquindio.vent2bebidas.domain.valueobject;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;

public record Capacidad(int mililitros) {

    public  Capacidad {
        if (mililitros <= 0) {
            throw new ReglaDominioException(
                    "La capacidad del envase debe ser mayor que cero."
            );
        }
    }
}
