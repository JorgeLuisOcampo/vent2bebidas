package com.uniquindio.vent2bebidas.infrastructure.persistence;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;
import com.uniquindio.vent2bebidas.domain.repository.Pagina;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
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

    @Override
    public List<Bebida> listarDisponibles() {
        return baseDeDatos.values().stream()
                .filter(Bebida::estaDisponible)
                .toList();
    }

    @Override
    public Pagina<Bebida> buscar(String texto, GrupoAlcoholico grupo, int pagina, int tamano) {
        String patron = (texto == null) ? "" : texto.trim().toLowerCase();

        List<Bebida> coincidencias = baseDeDatos.values().stream()
                .filter(Bebida::estaDisponible)
                .filter(b -> b.getNombre().toLowerCase().contains(patron))
                .filter(b -> grupo == null || b.getGrupoAlcoholico() == grupo)
                .sorted(Comparator.comparing(Bebida::getNombre))
                .toList();

        int total = coincidencias.size();
        int totalPaginas = (int) Math.ceil((double) total / tamano);
        int desde = Math.min(pagina * tamano, total);
        int hasta = Math.min(desde + tamano, total);

        return new Pagina<>(coincidencias.subList(desde, hasta), total, totalPaginas, pagina, tamano);
    }
}