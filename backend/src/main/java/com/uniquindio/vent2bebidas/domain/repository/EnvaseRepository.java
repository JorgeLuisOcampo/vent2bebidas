package com.uniquindio.vent2bebidas.domain.repository;

import com.uniquindio.vent2bebidas.domain.entity.Envase;

import java.util.Optional;
import java.util.UUID;

public interface EnvaseRepository {
    Optional<Envase> obtenerPorId(UUID id);
    void guardar(Envase envase);
}