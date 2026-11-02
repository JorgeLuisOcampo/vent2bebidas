package com.uniquindio.vent2bebidas.domain.entity;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EnvaseReconstruirTest {

    private final TipoEnvase plastico = new TipoEnvase(MaterialEnvase.PLASTICO, new Capacidad(500), true);

    @Test
    void deberiaRestaurarEstadoYUsosYSeguirProtegiendoElLimite() {
        // Arrange: un envase de plástico que ya llegó a sus 10 usos
        Envase envase = Envase.reconstruir(UUID.randomUUID(), plastico, EstadoEnvase.RETORNADO, 10);

        // Act & Assert
        assertEquals(0, envase.usosRestantes());
        assertThrows(ReglaDominioException.class, envase::registrarRetorno);
    }

    @Test
    void noDeberiaReconstruirConUsosNegativos() {
        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> Envase.reconstruir(UUID.randomUUID(), plastico, EstadoEnvase.NUEVO, -3));
    }
}