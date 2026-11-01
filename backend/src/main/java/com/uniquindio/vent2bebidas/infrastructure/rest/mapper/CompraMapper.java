package com.uniquindio.vent2bebidas.infrastructure.rest.mapper;

import com.uniquindio.vent2bebidas.application.dto.response.CompraResponse;
import com.uniquindio.vent2bebidas.domain.entity.Compra;
import org.springframework.stereotype.Component;

// Traduce del dominio (Compra) al DTO de respuesta: aplana el Value Object Precio.
@Component
public class CompraMapper {

    public CompraResponse toResponse(Compra compra) {
        return new CompraResponse(
                compra.getId(),
                compra.getBebidaId(),
                compra.getVendedorId(),
                compra.getUnidades(),
                compra.getPrecioUnitario().monto(),
                compra.getPrecioUnitario().moneda(),
                compra.total().monto(),
                compra.getFecha(),
                compra.getEstado());
    }
}
