package com.uniquindio.vent2bebidas.domain.service;

import com.uniquindio.vent2bebidas.domain.entity.Consumidor;
import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.CodigoConsumidor;
import com.uniquindio.vent2bebidas.infrastructure.persistence.ConsumidorRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VerificadorCodigoConsumidorTest {

    private final ConsumidorRepositoryEnMemoria repository = new ConsumidorRepositoryEnMemoria();
    private final VerificadorCodigoConsumidor verificador = new VerificadorCodigoConsumidor(repository);

    @Test
    void deberiaAceptarUnCodigoQueNadieUsa() {
        // Arrange
        CodigoConsumidor codigo = new CodigoConsumidor("NUEVO1");

        // Act & Assert
        assertDoesNotThrow(() -> verificador.verificarQueEstaDisponible(codigo));
    }

    @Test
    void deberiaRechazarUnCodigoQueYaTieneOtroConsumidor() {
        // Arrange
        repository.guardar(Consumidor.registrar(UUID.randomUUID(), "Ana",
                new CodigoConsumidor("AB12CD"), LocalDate.now().minusYears(30)));

        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> verificador.verificarQueEstaDisponible(new CodigoConsumidor("AB12CD")));
    }
}
