package com.uniquindio.vent2bebidas.domain.valueobject;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodigoConsumidorTest {

    @Test
    void dosCodigosConElMismoValorDebenSerIguales() {
        // Arrange
        CodigoConsumidor c1 = new CodigoConsumidor("AB12CD");
        CodigoConsumidor c2 = new CodigoConsumidor("AB12CD");

        // Act & Assert
        assertEquals(c1, c2); // Value Object: igual por VALOR
    }

    @Test
    void noDebeCrearUnCodigoVacio() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            new CodigoConsumidor("  ");
        });
    }

    @Test
    void noDebeCrearUnCodigoDeLongitudDistintaDeSeis() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            new CodigoConsumidor("AB12");
        });
    }
}