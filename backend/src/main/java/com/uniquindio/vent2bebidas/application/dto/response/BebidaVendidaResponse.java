package com.uniquindio.vent2bebidas.application.dto.response;

import com.uniquindio.vent2bebidas.domain.repository.BebidaVendida;

import java.util.UUID;

// Una fila del ranking hacia afuera. Todos los valores salen de la base de datos (nada quemado).
public record BebidaVendidaResponse(int posicion, UUID bebidaId, String nombre,
                                    long unidadesVendidas, long numeroCompras) {

    public static BebidaVendidaResponse desde(int posicion, BebidaVendida fila) {
        return new BebidaVendidaResponse(posicion, fila.bebidaId(), fila.nombre(),
                fila.unidadesVendidas(), fila.numeroCompras());
    }
}