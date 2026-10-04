package com.uniquindio.vent2bebidas.domain.valueobject;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;

public record TipoEnvase(MaterialEnvase material,
                         Capacidad capacidad,
                         boolean retornable) {

    public TipoEnvase {
        if (material == null) {
            throw new ReglaDominioException("El material del envase es obligatorio.");
        }
        if (capacidad == null) {
            throw new ReglaDominioException("La capacidad del envase es obligatoria.");
        }
        if (retornable && !material.permiteRetorno()) {
            throw new ReglaDominioException(
                    "Un envase de " + material + " no puede ser retornable.");
        }
    }

    // Cuántas veces puede volver a usarse un envase de este tipo (0 si no es retornable)
    public int usosMaximos() {
        return retornable ? material.usosMaximos() : 0;
    }
}