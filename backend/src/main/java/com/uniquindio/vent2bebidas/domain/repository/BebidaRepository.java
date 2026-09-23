package com.uniquindio.vent2bebidas.domain.repository;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;

import java.util.Optional;
import java.util.UUID;

public interface BebidaRepository {
    Optional<Bebida> obtenerPorId(UUID id);
    void guardar(Bebida bebida);
}
