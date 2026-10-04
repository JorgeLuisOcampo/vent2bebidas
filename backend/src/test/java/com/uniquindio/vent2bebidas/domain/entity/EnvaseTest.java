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

    @Test
    void noDebeRetornarEnvaseNoRetornable() {
        // Arrange
        Capacidad capacidad = new Capacidad(500);
        TipoEnvase tipoNoRetornable = new TipoEnvase(MaterialEnvase.PLASTICO, capacidad, false);
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
    void debeCambiarEstadoADanado() {
        // Arrange
        Capacidad capacidad = new Capacidad(500);
        TipoEnvase tipoRetornable = new TipoEnvase(MaterialEnvase.VIDRIO, capacidad, true);
        Envase envase = Envase.registrar(UUID.randomUUID(), tipoRetornable);

        // Act
        envase.marcarComoDanado();

        // Assert
        assertEquals(EstadoEnvase.DANADO, envase.getEstado());
    }

    @Test
    void debeAumentarUsosYCambiarEstadoAlRetornar() {
        // Arrange
        Capacidad capacidad = new Capacidad(500);
        TipoEnvase tipoRetornable = new TipoEnvase(MaterialEnvase.VIDRIO, capacidad, true);
        Envase envase = Envase.registrar(UUID.randomUUID(), tipoRetornable);

        int usosIniciales = envase.getCantidadUsos();

        // Act
        envase.registrarRetorno();

        // Assert
        assertEquals(EstadoEnvase.RETORNADO, envase.getEstado());
        assertEquals(usosIniciales + 1, envase.getCantidadUsos());
    }
}