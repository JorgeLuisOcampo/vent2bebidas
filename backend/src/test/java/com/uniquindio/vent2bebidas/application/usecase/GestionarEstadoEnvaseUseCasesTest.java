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

class GestionarEstadoEnvaseUseCasesTest {

    private Envase registrarEnvase(EnvaseRepositoryEnMemoria repository) {
        return new RegistrarEnvaseUseCase(repository)
                .ejecutar(new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true));
    }

    @Test
    void debeMarcarElEnvaseComoDanadoYGuardarlo() {
        // Arrange
        EnvaseRepositoryEnMemoria repository = new EnvaseRepositoryEnMemoria();
        Envase envase = registrarEnvase(repository);
        MarcarEnvaseComoDanadoUseCase useCase = new MarcarEnvaseComoDanadoUseCase(repository);

        // Act
        useCase.ejecutar(envase.getId());

        // Assert
        assertEquals(EstadoEnvase.DANADO,
                repository.obtenerPorId(envase.getId()).orElseThrow().getEstado());
    }

    @Test
    void debeDescartarElEnvaseYGuardarlo() {
        // Arrange
        EnvaseRepositoryEnMemoria repository = new EnvaseRepositoryEnMemoria();
        Envase envase = registrarEnvase(repository);
        DescartarEnvaseUseCase useCase = new DescartarEnvaseUseCase(repository);

        // Act
        useCase.ejecutar(envase.getId());

        // Assert
        assertEquals(EstadoEnvase.DESCARTADO,
                repository.obtenerPorId(envase.getId()).orElseThrow().getEstado());
    }

    @Test
    void noDebeMarcarComoDanadoUnEnvaseYaDescartado() {
        // Arrange
        EnvaseRepositoryEnMemoria repository = new EnvaseRepositoryEnMemoria();
        Envase envase = registrarEnvase(repository);
        new DescartarEnvaseUseCase(repository).ejecutar(envase.getId());
        MarcarEnvaseComoDanadoUseCase useCase = new MarcarEnvaseComoDanadoUseCase(repository);

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            useCase.ejecutar(envase.getId());
        });
    }

    @Test
    void debeFallarSiElEnvaseAMarcarNoExiste() {
        // Arrange
        MarcarEnvaseComoDanadoUseCase useCase =
                new MarcarEnvaseComoDanadoUseCase(new EnvaseRepositoryEnMemoria());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            useCase.ejecutar(UUID.randomUUID());
        });
    }
}