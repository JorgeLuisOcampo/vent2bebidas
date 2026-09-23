package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;

import java.util.UUID;

public class RegistrarBebidaUseCase {
    private final BebidaRepository repository;

    public RegistrarBebidaUseCase(BebidaRepository repository) {
        this.repository = repository;
    }

    public Bebida ejecutar (UUID id, String nombre, TipoEnvase tipoEnvase, GrupoAlcoholico grupoAlcoholico, double precio) {
        Bebida bebida = new Bebida(id, nombre, tipoEnvase, grupoAlcoholico, precio);
        repository.guardar(bebida);
        return bebida;
    }
}
