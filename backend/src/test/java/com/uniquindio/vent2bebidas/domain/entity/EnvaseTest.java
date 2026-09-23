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

        Capacidad capacidad = new Capacidad(500);
        TipoEnvase tipoNoRetornable = new TipoEnvase(MaterialEnvase.PLASTICO, capacidad, false);
        Envase envase = new Envase(UUID.randomUUID(), tipoNoRetornable);

        ReglaDominioException excepcionCapturada = assertThrows(
                ReglaDominioException.class,
                () -> envase.registrarRetorno()
        );

        assertEquals("El envase no es retornable.", excepcionCapturada.getMessage());
    }
}