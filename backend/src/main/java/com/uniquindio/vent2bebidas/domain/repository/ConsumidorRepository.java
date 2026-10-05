package com.uniquindio.vent2bebidas.domain.repository;

import com.uniquindio.vent2bebidas.domain.entity.Consumidor;
import com.uniquindio.vent2bebidas.domain.valueobject.CodigoConsumidor;

import java.util.Optional;
import java.util.UUID;

public interface ConsumidorRepository {
    Optional<Consumidor> obtenerPorId(UUID id);
    void guardar(Consumidor consumidor);
    boolean existePorCodigo(CodigoConsumidor codigo);
}