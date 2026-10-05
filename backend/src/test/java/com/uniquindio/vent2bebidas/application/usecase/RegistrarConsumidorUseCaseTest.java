package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Consumidor;
import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.CodigoConsumidor;
import com.uniquindio.vent2bebidas.infrastructure.persistence.ConsumidorRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RegistrarConsumidorUseCaseTest {

    @Test
    void debeRegistrarUnConsumidorYGuardarlo() {
        // Arrange
        ConsumidorRepositoryEnMemoria repository = new ConsumidorRepositoryEnMemoria();
        RegistrarConsumidorUseCase useCase = new RegistrarConsumidorUseCase(repository);

        // Act
        Consumidor consumidor = useCase.ejecutar(
                "Ana Gómez", new CodigoConsumidor("AB12CD"), LocalDate.now().minusYears(30));

        // Assert
        assertTrue(repository.obtenerPorId(consumidor.getId()).isPresent());
        assertEquals(0, consumidor.getComprasHistoricas());
    }

    @Test
    void noDebePermitirDosConsumidoresConElMismoCodigo() {
        // Arrange
        ConsumidorRepositoryEnMemoria repository = new ConsumidorRepositoryEnMemoria();
        RegistrarConsumidorUseCase useCase = new RegistrarConsumidorUseCase(repository);
        useCase.ejecutar("Ana Gómez", new CodigoConsumidor("AB12CD"), LocalDate.now().minusYears(30));

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            useCase.ejecutar("Luis Pérez", new CodigoConsumidor("AB12CD"), LocalDate.now().minusYears(25));
        });
    }
}