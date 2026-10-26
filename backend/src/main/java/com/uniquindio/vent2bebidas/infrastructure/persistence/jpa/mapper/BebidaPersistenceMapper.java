package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.mapper;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.valueobject.Capacidad;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.BebidaEntity;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.PrecioEmbeddable;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.TipoEnvaseEmbeddable;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Traduce entre el modelo del DOMINIO (Bebida) y el modelo de la BASE DE DATOS (BebidaEntity).
@Component
public class BebidaPersistenceMapper {

    public BebidaEntity toEntity(Bebida bebida) {
        BebidaEntity entity = new BebidaEntity();
        entity.setId(bebida.getId().toString());
        entity.setVendedorId(bebida.getVendedorId().toString());
        entity.setNombre(bebida.getNombre());
        entity.setPrecio(new PrecioEmbeddable(bebida.getPrecio().monto(), bebida.getPrecio().moneda()));

        TipoEnvase tipo = bebida.getTipoEnvase();
        entity.setTipoEnvase(new TipoEnvaseEmbeddable(
                tipo.material(), tipo.capacidad().mililitros(), tipo.retornable()));

        entity.setGrupoAlcoholico(bebida.getGrupoAlcoholico());
        entity.setEstado(bebida.getEstado());
        return entity;
    }

    public Bebida toDomain(BebidaEntity entity) {
        TipoEnvaseEmbeddable tipo = entity.getTipoEnvase();
        PrecioEmbeddable precio = entity.getPrecio();

        return Bebida.reconstruir(
                UUID.fromString(entity.getId()),
                UUID.fromString(entity.getVendedorId()),
                entity.getNombre(),
                new TipoEnvase(tipo.getMaterial(), new Capacidad(tipo.getCapacidadMl()), tipo.isRetornable()),
                entity.getGrupoAlcoholico(),
                new Precio(precio.getMonto(), precio.getMoneda()),
                entity.getEstado());
    }
}