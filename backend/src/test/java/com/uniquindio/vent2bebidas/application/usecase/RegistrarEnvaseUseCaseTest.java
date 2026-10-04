package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Envase;
import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import com.uniquindio.vent2bebidas.infrastructure.persistence.EnvaseRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrarEnvaseUseCaseTest {

    @Test
    void debeRegistrarUnEnvaseNuevoYGuardarlo() {
        // Arrange
        EnvaseRepositoryEnMemoria repository = new EnvaseRepositoryEnMemoria();
        RegistrarEnvaseUseCase useCase = new RegistrarEnvaseUseCase(repository);
        TipoEnvase tipo = new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true);

        // Act
        Envase envase = useCase.ejecutar(tipo);

        // Assert
        assertEquals(EstadoEnvase.NUEVO, envase.getEstado());
        assertTrue(repository.obtenerPorId(envase.getId()).isPresent());
    }

    @Test
    void noDebeRegistrarUnEnvaseSinTipo() {
        // Arrange
        RegistrarEnvaseUseCase useCase = new RegistrarEnvaseUseCase(new EnvaseRepositoryEnMemoria());

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            useCase.ejecutar(null);
        });
    }
}