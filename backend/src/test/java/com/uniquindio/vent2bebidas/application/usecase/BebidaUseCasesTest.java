package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoBebida;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import com.uniquindio.vent2bebidas.infrastructure.persistence.BebidaRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BebidaUseCasesTest {

    private final BebidaRepositoryEnMemoria repository = new BebidaRepositoryEnMemoria();

    private Bebida registrarBebida(String nombre) {
        return new RegistrarBebidaUseCase(repository).ejecutar(
                UUID.randomUUID(), nombre,
                new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true),
                GrupoAlcoholico.FERMENTADA, new Precio(5000, "COP"));
    }

    @Test
    void debeRegistrarUnaBebidaDisponibleYGuardarla() {
        // Act
        Bebida bebida = registrarBebida("Cerveza Artesanal");

        // Assert
        assertTrue(bebida.estaDisponible());
        assertTrue(repository.obtenerPorId(bebida.getId()).isPresent());
    }

    @Test
    void debeCambiarElPrecioYGuardarElCambio() {
        // Arrange
        Bebida bebida = registrarBebida("Cerveza Artesanal");
        CambiarPrecioBebidaUseCase useCase = new CambiarPrecioBebidaUseCase(repository);

        // Act
        useCase.ejecutar(bebida.getId(), new Precio(6500, "COP"));

        // Assert
        Bebida guardada = repository.obtenerPorId(bebida.getId()).orElseThrow();
        assertEquals(6500.0, guardada.getPrecio().monto());
    }

    @Test
    void debeFallarAlCambiarElPrecioDeUnaBebidaQueNoExiste() {
        // Arrange
        CambiarPrecioBebidaUseCase useCase = new CambiarPrecioBebidaUseCase(repository);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            useCase.ejecutar(UUID.randomUUID(), new Precio(6500, "COP"));
        });
    }

    @Test
    void debeEliminarLogicamenteLaBebidaSinBorrarla() {
        // Arrange
        Bebida bebida = registrarBebida("Cerveza Artesanal");
        EliminarBebidaUseCase useCase = new EliminarBebidaUseCase(repository);

        // Act
        useCase.ejecutar(bebida.getId());

        // Assert
        Bebida guardada = repository.obtenerPorId(bebida.getId()).orElseThrow(); // sigue existiendo
        assertEquals(EstadoBebida.ELIMINADA, guardada.getEstado());
    }

    @Test
    void listarDisponiblesNoDebeIncluirLasBebidasEliminadas() {
        // Arrange
        Bebida visible = registrarBebida("Cerveza Rubia");
        Bebida eliminada = registrarBebida("Cerveza Negra");
        new EliminarBebidaUseCase(repository).ejecutar(eliminada.getId());
        ListarBebidasDisponiblesUseCase useCase = new ListarBebidasDisponiblesUseCase(repository);

        // Act
        var disponibles = useCase.ejecutar();

        // Assert
        assertEquals(1, disponibles.size());
        assertTrue(disponibles.contains(visible));
    }
}