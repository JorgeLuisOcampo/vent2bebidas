package com.uniquindio.vent2bebidas.domain.repository;

import com.uniquindio.vent2bebidas.domain.entity.Compra;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// PUERTO del dominio: dice QUÉ necesita el negocio, no CÓMO se guarda.
public interface CompraRepository {
    Optional<Compra> obtenerPorId(UUID id);

    void guardar(Compra compra);

    // Compras de un consumidor, de la más reciente a la más antigua.
    List<Compra> listarPorConsumidor(UUID consumidorId);

    // Ranking: bebidas con más unidades vendidas en compras CONFIRMADAS (sin las eliminadas).
    List<BebidaVendida> masVendidas(int limite);
}
