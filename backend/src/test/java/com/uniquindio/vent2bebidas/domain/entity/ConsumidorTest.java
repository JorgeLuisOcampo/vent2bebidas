package com.uniquindio.vent2bebidas.domain.entity;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.CodigoConsumidor;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConsumidorTest {

    private Consumidor adulto() {
        return Consumidor.registrar(UUID.randomUUID(), "Ana Gómez", new CodigoConsumidor("AB12CD"),
                LocalDate.now().minusYears(30));
    }

    private Consumidor menor() {
        return Consumidor.registrar(UUID.randomUUID(), "Luis Pérez", new CodigoConsumidor("XY98ZW"),
                LocalDate.now().minusYears(10));
    }

    // ---------- Identidad y registro ----------

    @Test
    void dosConsumidoresConLaMismaIdentidadSonElMismo() {
        // Arrange
        UUID id = UUID.randomUUID();
        Consumidor uno = Consumidor.registrar(id, "Ana Gómez", new CodigoConsumidor("AB12CD"),
                LocalDate.now().minusYears(30));
        Consumidor otro = Consumidor.registrar(id, "Ana G.", new CodigoConsumidor("ZZ99ZZ"),
                LocalDate.now().minusYears(31));

        // Act & Assert
        assertEquals(uno, otro); // Entidad: igual por IDENTIDAD (mismo id)
    }

    @Test
    void registrarCreaUnConsumidorSinComprasHistoricas() {
        // Act
        Consumidor consumidor = adulto();

        // Assert
        assertEquals(0, consumidor.getComprasHistoricas());
    }

    @Test
    void noDebePermitirRegistrarConFechaDeNacimientoFutura() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            Consumidor.registrar(UUID.randomUUID(), "Ana Gómez", new CodigoConsumidor("AB12CD"),
                    LocalDate.now().plusDays(1));
        });
    }

    @Test
    void noDebePermitirRegistrarSinNombre() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            Consumidor.registrar(UUID.randomUUID(), "  ", new CodigoConsumidor("AB12CD"),
                    LocalDate.now().minusYears(30));
        });
    }

    // ---------- Mayoría de edad ----------

    @Test
    void esMayorDeEdadDebeDistinguirAdultosDeMenores() {
        // Act & Assert
        assertTrue(adulto().esMayorDeEdad());
        assertFalse(menor().esMayorDeEdad());
    }

    @Test
    void unMenorNoPuedeComprarBebidasAlcoholicas() {
        // Arrange
        Consumidor consumidor = menor();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            consumidor.verificarPuedeComprar(GrupoAlcoholico.FERMENTADA, 1);
        });
    }

    @Test
    void unMenorSiPuedeComprarBebidasSinAlcohol() {
        // Arrange
        Consumidor consumidor = menor();

        // Act & Assert (no debe lanzar excepción)
        consumidor.verificarPuedeComprar(GrupoAlcoholico.SIN_ALCOHOL, 1);
    }

    // ---------- Volumen y compras ----------

    @Test
    void noDebePermitirComprarMasDeCincoUnidadesSinHistorial() {
        // Arrange
        Consumidor consumidor = adulto(); // 0 compras históricas

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            consumidor.verificarPuedeComprar(GrupoAlcoholico.FERMENTADA, 6);
        });
    }

    @Test
    void debePermitirComprarMasDeCincoUnidadesConMasDeTresComprasHistoricas() {
        // Arrange
        Consumidor consumidor = adulto();
        for (int i = 0; i < 4; i++) {
            consumidor.registrarCompraExitosa();
        }

        // Act & Assert (no debe lanzar excepción)
        consumidor.verificarPuedeComprar(GrupoAlcoholico.FERMENTADA, 6);
    }

    @Test
    void noDebePermitirComprarCeroUnidades() {
        // Arrange
        Consumidor consumidor = adulto();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            consumidor.verificarPuedeComprar(GrupoAlcoholico.FERMENTADA, 0);
        });
    }

    @Test
    void registrarCompraExitosaSumaExactamenteUnaCompra() {
        // Arrange
        Consumidor consumidor = adulto();

        // Act
        consumidor.registrarCompraExitosa();

        // Assert
        assertEquals(1, consumidor.getComprasHistoricas());
    }
}