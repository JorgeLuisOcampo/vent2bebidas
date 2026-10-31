package com.uniquindio.vent2bebidas.infrastructure.rest.mapper;

import com.uniquindio.vent2bebidas.application.dto.request.CambiarPrecioRequest;
import com.uniquindio.vent2bebidas.application.dto.request.RegistrarBebidaRequest;
import com.uniquindio.vent2bebidas.application.dto.response.BebidaDetalleResponse;
import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.EstadoBebida;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.MaterialEnvase;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BebidaMapperTest {
    private final BebidaMapper mapper = new BebidaMapper();

    @Test
    void deberiaAplanarLaBebidaEnSuRespuesta() {
// Arrange
        UUID id = UUID.randomUUID();
        UUID vendedorId = UUID.randomUUID();
        Bebida bebida = Bebida.publicar(id, vendedorId, "Cerveza Rubia",
                new TipoEnvase(MaterialEnvase.VIDRIO, new Capacidad(330), true),
                GrupoAlcoholico.FERMENTADA, new Precio(5000, "COP"));
// Act
        BebidaDetalleResponse respuesta = mapper.toResponse(bebida);
// Assert
        assertEquals(id, respuesta.id());
        assertEquals(vendedorId, respuesta.vendedorId());
        assertEquals(MaterialEnvase.VIDRIO, respuesta.material());
        assertEquals(330, respuesta.capacidadMl());
        assertEquals(5000.0, respuesta.monto());
        assertEquals("COP", respuesta.moneda());
        assertEquals(EstadoBebida.PUBLICADA, respuesta.estado());
    }

    @Test
    void deberiaConvertirLosRequestsEnValueObjectsDelDominio() {
// Arrange
        RegistrarBebidaRequest registrar = new RegistrarBebidaRequest(
                "Cerveza Rubia", MaterialEnvase.PLASTICO, 500, true,
                GrupoAlcoholico.FERMENTADA, 5200, "COP");
        CambiarPrecioRequest cambiar = new CambiarPrecioRequest(6500, "COP");
// Act & Assert
        assertEquals(new TipoEnvase(MaterialEnvase.PLASTICO, new Capacidad(500), true),
                mapper.toTipoEnvase(registrar));
        assertEquals(new Precio(5200, "COP"), mapper.toPrecio(registrar));
        assertEquals(new Precio(6500, "COP"), mapper.toPrecio(cambiar));
    }
}
