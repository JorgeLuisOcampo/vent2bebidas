package com.uniquindio.vent2bebidas.infrastructure.rest.mapper;

import com.uniquindio.vent2bebidas.application.dto.response.ConsumidorResponse;
import com.uniquindio.vent2bebidas.domain.entity.Consumidor;
import org.springframework.stereotype.Component;

@Component
public class ConsumidorMapper {

    public ConsumidorResponse toResponse(Consumidor consumidor) {
        return new ConsumidorResponse(
                consumidor.getId(),
                consumidor.getNombre(),
                consumidor.getCodigoConsumidor().valor(),
                consumidor.getFechaNacimiento(),
                consumidor.esMayorDeEdad(),
                consumidor.getComprasHistoricas());
    }
}
