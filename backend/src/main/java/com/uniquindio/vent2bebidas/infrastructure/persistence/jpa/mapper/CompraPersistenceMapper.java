package com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.mapper;

import com.uniquindio.vent2bebidas.domain.entity.Compra;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.CompraEntity;
import com.uniquindio.vent2bebidas.infrastructure.persistence.jpa.entity.PrecioEmbeddable;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Traduce entre el DOMINIO (Compra) y la BASE DE DATOS (CompraEntity).
@Component
public class CompraPersistenceMapper {

    public CompraEntity toEntity(Compra compra) {
        CompraEntity entity = new CompraEntity();
        entity.setId(compra.getId().toString());
        entity.setConsumidorId(compra.getConsumidorId().toString());
        entity.setBebidaId(compra.getBebidaId().toString());
        entity.setVendedorId(compra.getVendedorId().toString());
        entity.setUnidades(compra.getUnidades());
        entity.setPrecioUnitario(new PrecioEmbeddable(
                compra.getPrecioUnitario().monto(), compra.getPrecioUnitario().moneda()));
        entity.setFecha(compra.getFecha());
        entity.setEstado(compra.getEstado());
        return entity;
    }

    public Compra toDomain(CompraEntity entity) {
        PrecioEmbeddable precio = entity.getPrecioUnitario();
        return Compra.reconstruir(
                UUID.fromString(entity.getId()),
                UUID.fromString(entity.getConsumidorId()),
                UUID.fromString(entity.getBebidaId()),
                UUID.fromString(entity.getVendedorId()),
                entity.getUnidades(),
                new Precio(precio.getMonto(), precio.getMoneda()),
                entity.getFecha(),
                entity.getEstado());
    }
}