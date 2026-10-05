package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.entity.Consumidor;
import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.CodigoConsumidor;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import com.uniquindio.vent2bebidas.infrastructure.persistence.BebidaRepositoryEnMemoria;
import com.uniquindio.vent2bebidas.infrastructure.persistence.ConsumidorRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConfirmarCompraDeBebidaUseCaseTest {

    private final ConsumidorRepositoryEnMemoria consumidores = new ConsumidorRepositoryEnMemoria();
    private final BebidaRepositoryEnMemoria bebidas = new BebidaRepositoryEnMemoria();
    private final ConfirmarCompraDeBebidaUseCase useCase =
            new ConfirmarCompraDeBebidaUseCase(consumidores, bebidas);

    private Consumidor registrarConsumidor(String codigo, int edad) {
        return new RegistrarConsumidorUseCase(consumidores).ejecutar(
                "Cliente " + codigo, new CodigoConsumidor(codigo), LocalDate.now().minusYears(edad));
    }

    private Bebida registrarBebida(GrupoAlcoholico grupo) {
        return new RegistrarBebidaUseCase(bebidas).ejecutar(
                UUID.randomUUID(), "Cerveza Artesanal",
                new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true),
                grupo, new Precio(5000, "COP"));
    }

    @Test
    void debeConfirmarLaCompraYSumarloAlHistorial() {
        // Arrange
        Consumidor consumidor = registrarConsumidor("AB12CD", 30);
        Bebida bebida = registrarBebida(GrupoAlcoholico.FERMENTADA);

        // Act
        useCase.ejecutar(consumidor.getId(), bebida.getId(), 2);

        // Assert
        Consumidor guardado = consumidores.obtenerPorId(consumidor.getId()).orElseThrow();
        assertEquals(1, guardado.getComprasHistoricas());
    }

    @Test
    void noDebeVenderBebidaAlcoholicaAUnMenor() {
        // Arrange
        Consumidor menor = registrarConsumidor("XY98ZW", 10);
        Bebida bebida = registrarBebida(GrupoAlcoholico.FERMENTADA);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            useCase.ejecutar(menor.getId(), bebida.getId(), 1);
        });
        assertEquals(0, menor.getComprasHistoricas()); // la compra fallida no se registró
    }

    @Test
    void noDebeVenderUnaBebidaEliminada() {
        // Arrange
        Consumidor consumidor = registrarConsumidor("AB12CD", 30);
        Bebida bebida = registrarBebida(GrupoAlcoholico.SIN_ALCOHOL);
        new EliminarBebidaUseCase(bebidas).ejecutar(bebida.getId());

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            useCase.ejecutar(consumidor.getId(), bebida.getId(), 1);
        });
    }

    @Test
    void debeFallarSiElConsumidorNoExiste() {
        // Arrange
        Bebida bebida = registrarBebida(GrupoAlcoholico.SIN_ALCOHOL);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            useCase.ejecutar(UUID.randomUUID(), bebida.getId(), 1);
        });
    }
}