package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Compra;
import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.repository.CompraRepository;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoCompra;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Prueba UNITARIA con el repositorio simulado (Mockito): se comprueba qué se guardó y qué NO.
class CancelarCompraUseCaseTest {

    private CompraRepository repository;
    private CancelarCompraUseCase useCase;
    private final UUID consumidorId = UUID.randomUUID();

    @BeforeEach
    void preparar() {
        repository = mock(CompraRepository.class);
        useCase = new CancelarCompraUseCase(repository);
    }

    private Compra compraHecha(LocalDateTime fecha) {
        return Compra.confirmar(UUID.randomUUID(), consumidorId, UUID.randomUUID(), UUID.randomUUID(),
                2, new Precio(5000, "COP"), fecha);
    }

    @Test
    void deberiaCancelarYGuardarUnaCompraReciente() {
        // Arrange
        Compra compra = compraHecha(LocalDateTime.now().minusHours(2));
        when(repository.obtenerPorId(compra.getId())).thenReturn(Optional.of(compra));

        // Act
        Compra resultado = useCase.ejecutar(compra.getId(), consumidorId);

        // Assert
        assertEquals(EstadoCompra.CANCELADA, resultado.getEstado());
        verify(repository).guardar(compra);
    }

    @Test
    void noDeberiaGuardarSiPasaronMasDe24Horas() {
        // Arrange: regla de negocio RN-13
        Compra compra = compraHecha(LocalDateTime.now().minusDays(3));
        when(repository.obtenerPorId(compra.getId())).thenReturn(Optional.of(compra));

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> useCase.ejecutar(compra.getId(), consumidorId));
        verify(repository, never()).guardar(any());
    }

    @Test
    void noDeberiaGuardarSiQuienCancelaNoEsElComprador() {
        // Arrange
        Compra compra = compraHecha(LocalDateTime.now().minusHours(1));
        when(repository.obtenerPorId(compra.getId())).thenReturn(Optional.of(compra));

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> useCase.ejecutar(compra.getId(), UUID.randomUUID()));
        verify(repository, never()).guardar(any());
    }

    @Test
    void deberiaFallarSiLaCompraNoExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repository.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> useCase.ejecutar(id, consumidorId));
        verify(repository, never()).guardar(any());
    }
}
