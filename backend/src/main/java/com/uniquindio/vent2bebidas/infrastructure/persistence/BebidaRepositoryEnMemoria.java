package com.uniquindio.vent2bebidas.infrastructure.persistence;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BebidaRepositoryEnMemoria implements BebidaRepository {
    private final Map<UUID, Bebida> baseDeDatos = new HashMap<>();

    @Override
    public Optional<Bebida> obtenerPorId(UUID id) {
        return Optional.ofNullable(baseDeDatos.get(id));
    }

    @Override
    public void guardar(Bebida bebida) {
        baseDeDatos.put(bebida.getId(), bebida);
    }
}
