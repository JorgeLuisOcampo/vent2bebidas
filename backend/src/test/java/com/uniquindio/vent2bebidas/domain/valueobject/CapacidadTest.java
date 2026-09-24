package com.uniquindio.vent2bebidas.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;
import com.uniquindio.vent2bebidas.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.Test;

class CapacidadTest {

    @Test
    void dosCapacidadesConElMismoValorDebenSerIguales() {
        Capacidad c1 = new Capacidad(500);
        Capacidad c2 = new Capacidad(500);

        assertEquals(c1, c2); // Value Object: igual por VALOR
    }

    @Test
    void noDebeCrearCapacidadConMililitrosMenoresOIgualesACero() {
        assertThrows(ReglaDominioException.class, () -> {
            new Capacidad(0);
        });
    }
}

