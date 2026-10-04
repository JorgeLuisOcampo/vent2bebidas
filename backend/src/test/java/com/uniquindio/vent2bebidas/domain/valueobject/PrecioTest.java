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

    @Test
    void noDebeCrearPrecioEnCero() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            new Precio(0, "COP");
        });
    }

    @Test
    void noDebeCrearPrecioNegativo() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            new Precio(-1000, "COP");
        });
    }

    @Test
    void noDebeCrearPrecioSinMoneda() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            new Precio(5000, " ");
        });
    }

    @Test
    void laMonedaSeNormalizaAMayusculas() {
        // Act
        Precio precio = new Precio(5000, "cop");

        // Assert
        assertEquals("COP", precio.moneda());
    }

    @Test
    void conDescuentoDebeReducirElMontoSinModificarElOriginal() {
        // Arrange
        Precio original = new Precio(10000, "COP");

        // Act
        Precio conDescuento = original.conDescuento(20);

        // Assert
        assertEquals(8000.0, conDescuento.monto());
        assertEquals(10000.0, original.monto()); // el original no cambió
    }

    @Test
    void noDebePermitirUnDescuentoMayorA99() {
        // Arrange
        Precio precio = new Precio(10000, "COP");

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            precio.conDescuento(100);
        });
    }
}