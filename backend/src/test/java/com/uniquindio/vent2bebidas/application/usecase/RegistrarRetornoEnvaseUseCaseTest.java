package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Envase;
import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import com.uniquindio.vent2bebidas.infrastructure.persistence.EnvaseRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RegistrarRetornoEnvaseUseCaseTest {

    @Test
    void debeRegistrarElRetornoYGuardarElEnvaseActualizado() {
        // Arrange
        EnvaseRepositoryEnMemoria repository = new EnvaseRepositoryEnMemoria();
        Envase envase = new RegistrarEnvaseUseCase(repository)
                .ejecutar(new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true));
        RegistrarRetornoEnvaseUseCase useCase = new RegistrarRetornoEnvaseUseCase(repository);

        // Act
        useCase.ejecutar(envase.getId());

        // Assert
        Envase guardado = repository.obtenerPorId(envase.getId()).orElseThrow();
        assertEquals(EstadoEnvase.RETORNADO, guardado.getEstado());
        assertEquals(1, guardado.getCantidadUsos());
    }

    @Test
    void debeFallarSiElEnvaseNoExiste() {
        // Arrange
        RegistrarRetornoEnvaseUseCase useCase =
                new RegistrarRetornoEnvaseUseCase(new EnvaseRepositoryEnMemoria());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            useCase.ejecutar(UUID.randomUUID());
        });
    }

    @Test
    void debePropagarLaReglaDeDominioSiElEnvaseNoEsRetornable() {
        // Arrange
        EnvaseRepositoryEnMemoria repository = new EnvaseRepositoryEnMemoria();
        Envase envase = new RegistrarEnvaseUseCase(repository)
                .ejecutar(new TipoEnvase(MaterialEnvase.ALUMINIO, new Capacidad(330), false));
        RegistrarRetornoEnvaseUseCase useCase = new RegistrarRetornoEnvaseUseCase(repository);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            useCase.ejecutar(envase.getId());
        });
    }
}