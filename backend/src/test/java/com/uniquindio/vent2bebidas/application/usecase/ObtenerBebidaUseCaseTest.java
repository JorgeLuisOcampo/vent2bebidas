package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import
        com.uniquindio.vent2bebidas.infrastructure.persistence.BebidaRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ObtenerBebidaUseCaseTest {
    private final BebidaRepositoryEnMemoria repository = new
            BebidaRepositoryEnMemoria();
    private final ObtenerBebidaUseCase useCase = new ObtenerBebidaUseCase(repository);

    @Test
    void deberiaDevolverLaBebidaCuandoExiste() {
// Arrange
        Bebida bebida = Bebida.publicar(UUID.randomUUID(), UUID.randomUUID(), "Cerveza Rubia",
                new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true),
                GrupoAlcoholico.FERMENTADA, new Precio(5000, "COP"));
        repository.guardar(bebida);
// Act
        Bebida encontrada = useCase.ejecutar(bebida.getId());
// Assert
        assertEquals(bebida, encontrada);
    }

    @Test
    void deberiaLanzarNoSuchElementCuandoNoExiste() {
// Act & Assert
        assertThrows(NoSuchElementException.class, () ->
                useCase.ejecutar(UUID.randomUUID()));
    }
}
