package com.uniquindio.vent2bebidas.infrastructure.persistence;

import com.uniquindio.vent2bebidas.domain.entity.Consumidor;
import com.uniquindio.vent2bebidas.domain.repository.ConsumidorRepository;
import com.uniquindio.vent2bebidas.domain.valueobject.CodigoConsumidor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ConsumidorRepositoryEnMemoria implements ConsumidorRepository {

    private final Map<UUID, Consumidor> baseDeDatos = new HashMap<>();

    @Override
    public Optional<Consumidor> obtenerPorId(UUID id) {
        return Optional.ofNullable(baseDeDatos.get(id));
    }

    @Override
    public void guardar(Consumidor consumidor) {
        baseDeDatos.put(consumidor.getId(), consumidor);
    }

    @Override
    public boolean existePorCodigo(CodigoConsumidor codigo) {
        return baseDeDatos.values().stream()
                .anyMatch(c -> c.getCodigoConsumidor().equals(codigo));
    }
}