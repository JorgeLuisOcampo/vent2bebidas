package com.uniquindio.vent2bebidas.domain.entity;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoCompra;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CompraTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 10, 20, 10, 0);
    private final UUID consumidorId = UUID.randomUUID();

    private Compra compraDe(int unidades) {
        return Compra.confirmar(UUID.randomUUID(), consumidorId, UUID.randomUUID(), UUID.randomUUID(),
                unidades, new Precio(5000, "COP"), FECHA);
    }

    @Test
    void confirmarCreaUnaCompraConfirmadaYCalculaElTotal() {
        // Act
        Compra compra = compraDe(3);

        // Assert
        assertEquals(EstadoCompra.CONFIRMADA, compra.getEstado());
        assertEquals(new Precio(15000, "COP"), compra.total());
    }

    @Test
    void noDebePermitirUnaCompraSinUnidades() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> compraDe(0));
    }

    @Test
    void noDebePermitirMasDe24UnidadesEnUnaCompra() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> compraDe(25));
    }

    @Test
    void elConsumidorPuedeCancelarDentroDeLas24Horas() {
        // Arrange
        Compra compra = compraDe(2);

        // Act
        compra.cancelar(consumidorId, FECHA.plusHours(23));

        // Assert
        assertEquals(EstadoCompra.CANCELADA, compra.getEstado());
    }

    @Test
    void noDebePermitirCancelarDespuesDe24Horas() {
        // Arrange
        Compra compra = compraDe(2);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> compra.cancelar(consumidorId, FECHA.plusHours(25)));
        assertEquals(EstadoCompra.CONFIRMADA, compra.getEstado()); // no cambió nada
    }

    @Test
    void otroConsumidorNoPuedeCancelarLaCompra() {
        // Arrange
        Compra compra = compraDe(2);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> compra.cancelar(UUID.randomUUID(), FECHA.plusHours(1)));
    }

    @Test
    void noDebePermitirCancelarDosVeces() {
        // Arrange
        Compra compra = compraDe(2);
        compra.cancelar(consumidorId, FECHA.plusHours(1));

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> compra.cancelar(consumidorId, FECHA.plusHours(2)));
    }
}
