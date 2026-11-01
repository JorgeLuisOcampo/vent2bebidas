package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Compra;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.infrastructure.persistence.CompraRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListarComprasDelConsumidorUseCaseTest {

    private final CompraRepositoryEnMemoria repository = new CompraRepositoryEnMemoria();
    private final ListarComprasDelConsumidorUseCase useCase = new ListarComprasDelConsumidorUseCase(repository);

    private Compra guardarCompra(UUID consumidorId, LocalDateTime fecha) {
        Compra compra = Compra.confirmar(UUID.randomUUID(), consumidorId, UUID.randomUUID(),
                UUID.randomUUID(), 1, new Precio(4000, "COP"), fecha);
        repository.guardar(compra);
        return compra;
    }

    @Test
    void deberiaDevolverSoloLasComprasDelConsumidorDeLaMasRecienteALaMasAntigua() {
        // Arrange
        UUID ana = UUID.randomUUID();
        Compra antigua = guardarCompra(ana, LocalDateTime.now().minusDays(5));
        Compra reciente = guardarCompra(ana, LocalDateTime.now());
        guardarCompra(UUID.randomUUID(), LocalDateTime.now()); // de otra persona

        // Act
        List<Compra> resultado = useCase.ejecutar(ana);

        // Assert
        assertEquals(List.of(reciente, antigua), resultado);
    }
}
