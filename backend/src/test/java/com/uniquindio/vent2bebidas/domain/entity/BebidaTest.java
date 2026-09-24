package com.uniquindio.vent2bebidas.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import org.junit.jupiter.api.Test;

class BebidaTest {

    private TipoEnvase tipoEnvaseEjemplo() {
        return new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true);
    }

    @Test
    void dosBebidasConLaMismaIdentidadSonLaMisma() {
        UUID id = UUID.randomUUID();
        Bebida original = new Bebida(id, "Cerveza Artesanal", tipoEnvaseEjemplo(), GrupoAlcoholico.FERMENTADA, 5000);
        Bebida conOtrosDatos = new Bebida(id, "Malta Sin Alcohol", tipoEnvaseEjemplo(), GrupoAlcoholico.SIN_ALCOHOL, 3000);

        assertEquals(original, conOtrosDatos); // Entidad: igual por IDENTIDAD (mismo id)
    }

    @Test
    void noDebePermitirCambiarElPrecioAlMismoValorActual() {
        Bebida bebida = new Bebida(UUID.randomUUID(), "Cerveza Artesanal", tipoEnvaseEjemplo(), GrupoAlcoholico.FERMENTADA, 5000);

        assertThrows(ReglaDominioException.class, () -> {
            bebida.cambiarPrecio(5000);
        });
        assertEquals(5000, bebida.getPrecio()); // no cambió nada
    }
}
