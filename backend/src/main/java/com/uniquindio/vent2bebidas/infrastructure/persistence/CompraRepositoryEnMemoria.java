package com.uniquindio.vent2bebidas.infrastructure.persistence;

import com.uniquindio.vent2bebidas.domain.entity.Compra;
import com.uniquindio.vent2bebidas.domain.repository.BebidaVendida;
import com.uniquindio.vent2bebidas.domain.repository.CompraRepository;

import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

// Repositorio TEMPORAL mientras se implementa CompraRepositoryJpa: por ahora es el bean de Spring.
// Cuando llegue la versión JPA dejará de ser bean y se usará solo en las pruebas unitarias.
@Repository
public class CompraRepositoryEnMemoria implements CompraRepository {

    private final Map<UUID, Compra> baseDeDatos = new HashMap<>();

    @Override
    public Optional<Compra> obtenerPorId(UUID id) {
        return Optional.ofNullable(baseDeDatos.get(id));
    }

    @Override
    public void guardar(Compra compra) {
        baseDeDatos.put(compra.getId(), compra);
    }

    @Override
    public List<Compra> listarPorConsumidor(UUID consumidorId) {
        return baseDeDatos.values().stream()
                .filter(c -> c.getConsumidorId().equals(consumidorId))
                .sorted(Comparator.comparing(Compra::getFecha).reversed())
                .toList();
    }

    @Override
    public List<BebidaVendida> masVendidas(int limite) {
        // El ranking necesita el JOIN con las bebidas: eso lo hace la base de datos
        // (CompraRepositoryJpa). En memoria no se simula para no duplicar esa lógica en Java.
        throw new UnsupportedOperationException("El ranking solo se calcula en la base de datos.");
    }

    public int cantidad() {
        return baseDeDatos.size();
    }
}
