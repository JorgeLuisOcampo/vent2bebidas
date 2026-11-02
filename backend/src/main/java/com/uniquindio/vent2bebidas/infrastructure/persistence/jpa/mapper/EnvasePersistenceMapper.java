package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.mapper;

import com.uniquindio.vent2bebidas.domain.entity.Envase;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.EnvaseEntity;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.TipoEnvaseEmbeddable;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Traduce entre el DOMINIO (Envase) y la BASE DE DATOS (EnvaseEntity).
@Component
public class EnvasePersistenceMapper {

    public EnvaseEntity toEntity(Envase envase) {
        TipoEnvase tipo = envase.getTipo();
        EnvaseEntity entity = new EnvaseEntity();
        entity.setId(envase.getId().toString());
        entity.setTipo(new TipoEnvaseEmbeddable(tipo.material(), tipo.capacidad().mililitros(), tipo.retornable()));
        entity.setEstado(envase.getEstado());
        entity.setCantidadUsos(envase.getCantidadUsos());
        return entity;
    }

    public Envase toDomain(EnvaseEntity entity) {
        TipoEnvaseEmbeddable tipo = entity.getTipo();
        return Envase.reconstruir(
                UUID.fromString(entity.getId()),
                new TipoEnvase(tipo.getMaterial(), new Capacidad(tipo.getCapacidadMl()), tipo.isRetornable()),
                entity.getEstado(),
                entity.getCantidadUsos());
    }
}