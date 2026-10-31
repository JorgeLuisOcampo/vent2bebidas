package com.uniquindio.vent2bebidas.infrastructure.rest.mapper;

import com.uniquindio.vent2bebidas.application.dto.request.CambiarPrecioRequest;
import com.uniquindio.vent2bebidas.application.dto.request.RegistrarBebidaRequest;
import com.uniquindio.vent2bebidas.application.dto.response.BebidaDetalleResponse;
import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import org.springframework.stereotype.Component;

// Traduce entre los DTOs de la API y los objetos del dominio.
@Component
public class BebidaMapper {
    public TipoEnvase toTipoEnvase(RegistrarBebidaRequest request) {
        return new TipoEnvase(request.material(), new Capacidad(request.capacidadMl()),
                request.retornable());
    }

    public Precio toPrecio(RegistrarBebidaRequest request) {
        return new Precio(request.monto(), request.moneda());
    }

    public Precio toPrecio(CambiarPrecioRequest request) {
        return new Precio(request.monto(), request.moneda());
    }

    // Aplana el dominio: el cliente recibe monto y moneda, no el record Precio.
    public BebidaDetalleResponse toResponse(Bebida bebida) {
        return new BebidaDetalleResponse(
                bebida.getId(),
                bebida.getVendedorId(),
                bebida.getNombre(),
                bebida.getTipoEnvase().material(),
                bebida.getTipoEnvase().capacidad().mililitros(),
                bebida.getTipoEnvase().retornable(),
                bebida.getGrupoAlcoholico(),
                bebida.getPrecio().monto(),
                bebida.getPrecio().moneda(),
                bebida.getEstado());
    }
}