package com.uniquindio.vent2bebidas.infrastructure.rest.mapper;

import com.uniquindio.vent2bebidas.application.dto.request.RegistrarEnvaseRequest;
import com.uniquindio.vent2bebidas.application.dto.response.EnvaseDetalleResponse;
import com.uniquindio.vent2bebidas.domain.entity.Envase;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import org.springframework.stereotype.Component;

// Traduce entre los DTOs de envases y el dominio.
@Component
public class EnvaseMapper {

    // Al crear los Value Objects, el dominio valida (capacidad > 0, aluminio no retornable).
    public TipoEnvase toTipoEnvase(RegistrarEnvaseRequest request) {
        return new TipoEnvase(request.material(), new Capacidad(request.capacidadMl()), request.retornable());
    }

    public EnvaseDetalleResponse toResponse(Envase envase) {
        return new EnvaseDetalleResponse(
                envase.getId(),
                envase.getTipo().material(),
                envase.getTipo().capacidad().mililitros(),
                envase.getTipo().retornable(),
                envase.getEstado(),
                envase.getCantidadUsos(),
                envase.usosRestantes());
    }
}