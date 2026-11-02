package com.uniquindio.vent2bebidas.infrastructure.persistence;

import com.uniquindio.vent2bebidas.domain.entity.Envase;
import com.uniquindio.vent2bebidas.domain.repository.EnvaseRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// Ya NO es un bean de Spring: el repositorio real es EnvaseRepositoryJpa.
// Se conserva para las pruebas unitarias de los casos de uso (se crea con new).
public class EnvaseRepositoryEnMemoria implements EnvaseRepository {

    private final Map<UUID, Envase> baseDeDatos = new HashMap<>();

    @Override
    public Optional<Envase> obtenerPorId(UUID id) {
        return Optional.ofNullable(baseDeDatos.get(id));
    }

    @Override
    public void guardar(Envase envase) {
        baseDeDatos.put(envase.getId(), envase);
    }
}