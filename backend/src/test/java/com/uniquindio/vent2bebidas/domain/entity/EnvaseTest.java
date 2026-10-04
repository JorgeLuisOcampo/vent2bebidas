package com.uniquindio.vent2bebidas.domain.entity;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EnvaseTest {

    private TipoEnvase tipoVidrioRetornable() {
        return new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(500), true);
    }

    private TipoEnvase tipoPlasticoRetornable() {
        return new TipoEnvase(MaterialEnvase.PLASTICO, new Capacidad(500), true);
    }

    @Test
    void dosEnvasesConLaMismaIdentidadSonElMismo() {
        // Arrange
        UUID id = UUID.randomUUID();
        Envase vidrio = Envase.registrar(id, tipoVidrioRetornable());
        Envase plastico = Envase.registrar(id, tipoPlasticoRetornable());

        // Act & Assert
        assertEquals(vidrio, plastico);
    }

    @Test
    void registrarCreaUnEnvaseNuevoConCeroUsos() {
        // Act
        Envase envase = Envase.registrar(UUID.randomUUID(), tipoVidrioRetornable());

        // Assert
        assertEquals(EstadoEnvase.NUEVO, envase.getEstado());
        assertEquals(0, envase.getCantidadUsos());
    }

    @Test
    void noDebePermitirRegistrarUnEnvaseSinTipo() {
        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            Envase.registrar(UUID.randomUUID(), null);
        });
    }

    @Test
    void noDebeRetornarEnvaseNoRetornable() {
        // Arrange
        TipoEnvase tipoNoRetornable = new TipoEnvase(MaterialEnvase.PLASTICO, new Capacidad(500), false);
        Envase envase = Envase.registrar(UUID.randomUUID(), tipoNoRetornable);

        // Act
        ReglaDominioException excepcionCapturada = assertThrows(
                ReglaDominioException.class,
                () -> envase.registrarRetorno()
        );

        // Assert
        assertEquals("El envase no es retornable.", excepcionCapturada.getMessage());
    }

    @Test
    void debeAumentarUsosYCambiarEstadoAlRetornar() {
        // Arrange
        Envase envase = Envase.registrar(UUID.randomUUID(), tipoVidrioRetornable());
        int usosIniciales = envase.getCantidadUsos();

        // Act
        envase.registrarRetorno();

        // Assert
        assertEquals(EstadoEnvase.RETORNADO, envase.getEstado());
        assertEquals(usosIniciales + 1, envase.getCantidadUsos());
    }

    @Test
    void noDebeRetornarUnEnvaseDanado() {
        // Arrange
        Envase envase = Envase.registrar(UUID.randomUUID(), tipoVidrioRetornable());
        envase.marcarComoDanado();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> envase.registrarRetorno());
        assertEquals(0, envase.getCantidadUsos()); // no sumó ningún uso
    }

    @Test
    void noDebeSuperarElMaximoDeUsosDeSuMaterial() {
        // Arrange: el plástico retornable permite 10 usos
        Envase envase = Envase.registrar(UUID.randomUUID(), tipoPlasticoRetornable());
        for (int i = 0; i < 10; i++) {
            envase.registrarRetorno();
        }

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> envase.registrarRetorno());
        assertEquals(10, envase.getCantidadUsos());
        assertEquals(0, envase.usosRestantes());
    }

    @Test
    void debeCambiarEstadoADanado() {
        // Arrange
        Envase envase = Envase.registrar(UUID.randomUUID(), tipoVidrioRetornable());

        // Act
        envase.marcarComoDanado();

        // Assert
        assertEquals(EstadoEnvase.DANADO, envase.getEstado());
    }

    @Test
    void unEnvaseDescartadoNoPuedeRetornarse() {
        // Arrange
        Envase envase = Envase.registrar(UUID.randomUUID(), tipoVidrioRetornable());
        envase.descartar();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> envase.registrarRetorno());
    }

    @Test
    void unEnvaseDescartadoNoPuedeMarcarseComoDanado() {
        // Arrange
        Envase envase = Envase.registrar(UUID.randomUUID(), tipoVidrioRetornable());
        envase.descartar();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> envase.marcarComoDanado());
        assertEquals(EstadoEnvase.DESCARTADO, envase.getEstado());
    }

    @Test
    void descartarDosVecesDebeLanzarExcepcion() {
        // Arrange
        Envase envase = Envase.registrar(UUID.randomUUID(), tipoVidrioRetornable());
        envase.descartar();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> envase.descartar());
    }
}