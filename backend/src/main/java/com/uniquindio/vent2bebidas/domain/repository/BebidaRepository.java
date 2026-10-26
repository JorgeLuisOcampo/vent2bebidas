package com.uniquindio.vent2bebidas.domain.repository;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BebidaRepository {
    Optional<Bebida> obtenerPorId(UUID id);
    void guardar(Bebida bebida);
    List<Bebida> listarDisponibles();

    // Busca entre las bebidas DISPONIBLES, por texto en el nombre y (opcional) por grupo alcohólico.
    // Siempre ordenadas por nombre. Las páginas empiezan en 0.
    Pagina<Bebida> buscar(String texto, GrupoAlcoholico grupo, int pagina, int tamano);
}