package com.uniquindio.vent2bebidas.domain.entity;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoBebida;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BebidaReconstruirTest {

    private TipoEnvase tipo() {
        return new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true);
    }

    @Test
    void deberiaReconstruirUnaBebidaConTodosSusDatos() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID vendedorId = UUID.randomUUID();

        // Act
        Bebida bebida = Bebida.reconstruir(id, vendedorId, "Cerveza Rubia", tipo(),
                GrupoAlcoholico.FERMENTADA, new Precio(5000, "COP"), EstadoBebida.PUBLICADA);

        // Assert
        assertEquals(id, bebida.getId());
        assertEquals(vendedorId, bebida.getVendedorId());
        assertEquals("Cerveza Rubia", bebida.getNombre());
        assertEquals(new Precio(5000, "COP"), bebida.getPrecio());
        assertTrue(bebida.estaDisponible());
    }

    @Test
    void deberiaConservarElEstadoEliminadaYSeguirProtegiendoLasInvariantes() {
        // Arrange
        Bebida bebida = Bebida.reconstruir(UUID.randomUUID(), UUID.randomUUID(), "Cerveza Vieja", tipo(),
                GrupoAlcoholico.FERMENTADA, new Precio(4500, "COP"), EstadoBebida.ELIMINADA);

        // Act & Assert
        assertEquals(EstadoBebida.ELIMINADA, bebida.getEstado());
        assertThrows(ReglaDominioException.class, () -> bebida.cambiarPrecio(new Precio(5000, "COP")));
    }
}