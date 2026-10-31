package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Prueba UNITARIA: el repositorio se simula con Mockito, así comprobamos qué se llamó (y qué NO).
class CambiarPrecioBebidaUseCaseTest {
    private BebidaRepository repository;
    private CambiarPrecioBebidaUseCase useCase;

    @BeforeEach
    void preparar() {
        repository = mock(BebidaRepository.class);
        useCase = new CambiarPrecioBebidaUseCase(repository);
    }

    private Bebida bebidaConPrecio5000() {
        return Bebida.publicar(UUID.randomUUID(), UUID.randomUUID(), "Cerveza Rubia",
                new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true),
                GrupoAlcoholico.FERMENTADA, new Precio(5000, "COP"));
    }

    @Test
    void noDeberiaGuardarSiLaBebidaNoExiste() {
// Arrange
        UUID id = UUID.randomUUID();
        when(repository.obtenerPorId(id)).thenReturn(Optional.empty());
// Act & Assert
        assertThrows(NoSuchElementException.class, () -> useCase.ejecutar(id, new
                Precio(6500, "COP")));
        verify(repository, never()).guardar(any());
    }

    @Test
    void noDeberiaGuardarSiSeRompeUnaReglaDeNegocio() {
// Arrange: el nuevo precio es IGUAL al actual, y eso rompe la regla
        Bebida bebida = bebidaConPrecio5000();
        when(repository.obtenerPorId(bebida.getId())).thenReturn(Optional.of(bebida));
// Act & Assert
        assertThrows(ReglaDominioException.class, () -> useCase.ejecutar(bebida.getId(),
                new Precio(5000, "COP")));
        verify(repository, never()).guardar(any());
    }

    @Test
    void deberiaGuardarLaBebidaConElNuevoPrecioCuandoTodoEstaBien() {
// Arrange
        Bebida bebida = bebidaConPrecio5000();
        when(repository.obtenerPorId(bebida.getId())).thenReturn(Optional.of(bebida));
// Act
        useCase.ejecutar(bebida.getId(), new Precio(6500, "COP"));
// Assert
        verify(repository).guardar(bebida);
        assertEquals(6500.0, bebida.getPrecio().monto());
    }
}
