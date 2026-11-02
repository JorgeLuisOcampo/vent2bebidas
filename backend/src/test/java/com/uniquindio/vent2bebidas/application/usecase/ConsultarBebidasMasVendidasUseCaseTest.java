package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.repository.BebidaVendida;
import com.uniquindio.vent2bebidas.domain.repository.CompraRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConsultarBebidasMasVendidasUseCaseTest {

    private final CompraRepository repository = mock(CompraRepository.class);
    private final ConsultarBebidasMasVendidasUseCase useCase = new ConsultarBebidasMasVendidasUseCase(repository);

    @Test
    void deberiaDevolverElRankingQueCalculaElRepositorio() {
        // Arrange
        List<BebidaVendida> ranking = List.of(new BebidaVendida(UUID.randomUUID(), "Cerveza Rubia", 13, 3));
        when(repository.masVendidas(5)).thenReturn(ranking);

        // Act
        List<BebidaVendida> resultado = useCase.ejecutar(5);

        // Assert
        assertEquals(ranking, resultado);
    }

    @Test
    void noDeberiaConsultarConUnLimiteFueraDeRango() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.ejecutar(0));
        assertThrows(IllegalArgumentException.class, () -> useCase.ejecutar(21));
        verify(repository, never()).masVendidas(anyInt());
    }
}