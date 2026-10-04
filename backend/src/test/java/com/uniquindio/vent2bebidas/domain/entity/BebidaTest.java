package com.uniquindio.vent2bebidas.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import org.junit.jupiter.api.Test;

class BebidaTest {

    private TipoEnvase tipoEnvaseEjemplo() {
        return new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true);
    }

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

    @Test
    void noDebePermitirCambiarElPrecioAlMismoValorActual() {
        // Arrange
        Bebida bebida = Bebida.publicar(UUID.randomUUID(), UUID.randomUUID(), "Cerveza Artesanal",
                tipoEnvaseEjemplo(), GrupoAlcoholico.FERMENTADA, new Precio(5000, "COP"));

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> {
            bebida.cambiarPrecio(new Precio(5000, "COP"));
        });
        assertEquals(5000.0, bebida.getPrecio().monto()); // no cambió nada
    }
}