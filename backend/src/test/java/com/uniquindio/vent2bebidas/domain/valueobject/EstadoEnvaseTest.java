package com.uniquindio.vent2bebidas.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstadoEnvaseTest {

    @Test
    void unEnvaseNuevoPuedeSerRetornado() {
        // Act
        boolean permitido = EstadoEnvase.NUEVO.puedeTransicionarA(EstadoEnvase.RETORNADO);

        // Assert
        assertTrue(permitido);
    }

    @Test
    void unEnvaseRetornadoPuedeRetornarseDeNuevo() {
        // Act
        boolean permitido = EstadoEnvase.RETORNADO.puedeTransicionarA(EstadoEnvase.RETORNADO);

        // Assert
        assertTrue(permitido);
    }

}