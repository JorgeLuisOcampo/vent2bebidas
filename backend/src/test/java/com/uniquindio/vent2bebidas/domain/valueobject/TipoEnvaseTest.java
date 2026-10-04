package com.uniquindio.vent2bebidas.domain.valueobject;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TipoEnvaseTest {

    @Test
    void dosTiposConLosMismosValoresDebenSerIguales() {
        // Arrange
        TipoEnvase t1 = new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true);
        TipoEnvase t2 = new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true);

        // Act & Assert
        assertEquals(t1, t2); // Value Object: igual por VALOR
    }

    @Test
    void noDebeCrearUnTipoRetornableDeAluminio() {
        // Arrange
        Capacidad capacidad = new Capacidad(330);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            new TipoEnvase(MaterialEnvase.ALUMINIO, capacidad, true);
        });
    }

    @Test
    void noDebeCrearUnTipoSinMaterial() {
        // Arrange
        Capacidad capacidad = new Capacidad(330);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            new TipoEnvase(null, capacidad, false);
        });
    }

    @Test
    void unTipoRetornableDeVidrioPermiteTreintaUsos() {
        // Arrange
        TipoEnvase tipo = new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true);

        // Act
        int usos = tipo.usosMaximos();

        // Assert
        assertEquals(30, usos);
    }
}