package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.mapper;

import com.uniquindio.vent2bebidas.domain.entity.Consumidor;
import com.uniquindio.vent2bebidas.domain.valueobject.CodigoConsumidor;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.ConsumidorEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Traduce entre el DOMINIO (Consumidor) y la BASE DE DATOS (ConsumidorEntity).
@Component
public class ConsumidorPersistenceMapper {

    public ConsumidorEntity toEntity(Consumidor consumidor) {
        ConsumidorEntity entity = new ConsumidorEntity();
        entity.setId(consumidor.getId().toString());
        entity.setNombre(consumidor.getNombre());
        entity.setCodigo(consumidor.getCodigoConsumidor().valor());
        entity.setFechaNacimiento(consumidor.getFechaNacimiento());
        entity.setComprasHistoricas(consumidor.getComprasHistoricas());
        return entity;
    }

    public Consumidor toDomain(ConsumidorEntity entity) {
        return Consumidor.reconstruir(
                UUID.fromString(entity.getId()),
                entity.getNombre(),
                new CodigoConsumidor(entity.getCodigo()),
                entity.getFechaNacimiento(),
                entity.getComprasHistoricas());
    }
}