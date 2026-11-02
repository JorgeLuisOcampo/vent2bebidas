package com.uniquindio.vent2bebidas.domain.entity;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.CodigoConsumidor;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConsumidorReconstruirTest {

    @Test
    void deberiaRestaurarElHistorialDeCompras() {
        // Act
        Consumidor consumidor = Consumidor.reconstruir(UUID.randomUUID(), "Ana",
                new CodigoConsumidor("AB12CD"), LocalDate.now().minusYears(30), 4);

        // Assert: con 4 compras históricas ya puede comprar más de 5 unidades
        assertEquals(4, consumidor.getComprasHistoricas());
        assertDoesNotThrow(() -> consumidor.verificarPuedeComprar(GrupoAlcoholico.FERMENTADA, 6));
    }

    @Test
    void noDeberiaReconstruirConUnHistorialNegativo() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> Consumidor.reconstruir(UUID.randomUUID(), "Ana",
                new CodigoConsumidor("AB12CD"), LocalDate.now().minusYears(30), -1));
    }
}