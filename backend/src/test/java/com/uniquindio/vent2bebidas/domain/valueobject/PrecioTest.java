package com.uniquindio.vent2bebidas.domain.valueobject;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrecioTest {

    @Test
    void dosPreciosConElMismoValorDebenSerIguales() {
        // Arrange
        Precio p1 = new Precio(5000, "COP");
        Precio p2 = new Precio(5000, "COP");

        // Act & Assert
        assertEquals(p1, p2); // Value Object: igual por VALOR
    }
    
}