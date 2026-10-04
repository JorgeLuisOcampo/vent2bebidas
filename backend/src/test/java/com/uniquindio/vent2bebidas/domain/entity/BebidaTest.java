package com.uniquindio.vent2bebidas.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoBebida;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import org.junit.jupiter.api.Test;

class BebidaTest {

    private TipoEnvase tipoEnvaseEjemplo() {
        return new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true);
    }

    private Bebida bebidaEjemplo() {
        return Bebida.publicar(UUID.randomUUID(), UUID.randomUUID(), "Cerveza Artesanal",
                tipoEnvaseEjemplo(), GrupoAlcoholico.FERMENTADA, new Precio(5000, "COP"));
    }

    //Identidad

    @Test
    void dosBebidasConLaMismaIdentidadSonLaMisma() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID vendedor = UUID.randomUUID();
        Bebida original = Bebida.publicar(id, vendedor, "Cerveza Artesanal", tipoEnvaseEjemplo(),
                GrupoAlcoholico.FERMENTADA, new Precio(5000, "COP"));
        Bebida conOtrosDatos = Bebida.publicar(id, vendedor, "Malta Sin Alcohol", tipoEnvaseEjemplo(),
                GrupoAlcoholico.SIN_ALCOHOL, new Precio(3000, "COP"));

        // Act & Assert
        assertEquals(original, conOtrosDatos); // Entidad: igual por IDENTIDAD (mismo id)
    }

    //Publicación

    @Test
    void publicarCreaUnaBebidaDisponible() {
        // Act
        Bebida bebida = bebidaEjemplo();

        // Assert
        assertEquals(EstadoBebida.PUBLICADA, bebida.getEstado());
        assertTrue(bebida.estaDisponible());
    }

    @Test
    void noDebePermitirPublicarUnaBebidaSinNombre() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            Bebida.publicar(UUID.randomUUID(), UUID.randomUUID(), "  ", tipoEnvaseEjemplo(),
                    GrupoAlcoholico.FERMENTADA, new Precio(5000, "COP"));
        });
    }

    @Test
    void noDebePermitirPublicarUnaBebidaSinVendedor() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            Bebida.publicar(UUID.randomUUID(), null, "Cerveza Artesanal", tipoEnvaseEjemplo(),
                    GrupoAlcoholico.FERMENTADA, new Precio(5000, "COP"));
        });
    }

    //Precio

    @Test
    void cambiarPrecioActualizaElPrecio() {
        // Arrange
        Bebida bebida = bebidaEjemplo();

        // Act
        bebida.cambiarPrecio(new Precio(6500, "COP"));

        // Assert
        assertEquals(6500.0, bebida.getPrecio().monto());
    }

    @Test
    void noDebePermitirCambiarElPrecioAlMismoValorActual() {
        // Arrange
        Bebida bebida = bebidaEjemplo();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            bebida.cambiarPrecio(new Precio(5000, "COP"));
        });
        assertEquals(5000.0, bebida.getPrecio().monto()); // no cambió nada
    }

    @Test
    void noDebePermitirCambiarLaMonedaDelPrecio() {
        // Arrange
        Bebida bebida = bebidaEjemplo();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            bebida.cambiarPrecio(new Precio(5000, "USD"));
        });
    }

    //Eliminación lógica

    @Test
    void eliminarLogicamenteMarcaLaBebidaComoEliminadaYNoDisponible() {
        // Arrange
        Bebida bebida = bebidaEjemplo();

        // Act
        bebida.eliminarLogicamente();

        // Assert
        assertEquals(EstadoBebida.ELIMINADA, bebida.getEstado());
        assertFalse(bebida.estaDisponible());
    }

    @Test
    void eliminarLogicamenteDosVecesDebeLanzarExcepcion() {
        // Arrange
        Bebida bebida = bebidaEjemplo();
        bebida.eliminarLogicamente();

        // Act & Assert
        assertThrows(ReglaDominioException.class, bebida::eliminarLogicamente);
    }

    @Test
    void noDebePermitirCambiarElPrecioDeUnaBebidaEliminada() {
        // Arrange
        Bebida bebida = bebidaEjemplo();
        bebida.eliminarLogicamente();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            bebida.cambiarPrecio(new Precio(7000, "COP"));
        });
    }

    @Test
    void unaBebidaEliminadaNoPuedeVenderse() {
        // Arrange
        Bebida bebida = bebidaEjemplo();
        bebida.eliminarLogicamente();

        // Act & Assert
        assertThrows(ReglaDominioException.class, bebida::verificarQueEstaDisponible);
    }
}