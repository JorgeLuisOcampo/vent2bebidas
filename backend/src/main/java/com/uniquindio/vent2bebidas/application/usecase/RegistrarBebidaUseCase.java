package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;
import com.uniquindio.vent2bebidas.domain.valueobject.GrupoAlcoholico;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegistrarBebidaUseCase {

    private final BebidaRepository repository;

    public RegistrarBebidaUseCase(BebidaRepository repository) {
        this.repository = repository;
    }

    public Bebida ejecutar(UUID vendedorId, String nombre, TipoEnvase tipoEnvase,
                           GrupoAlcoholico grupoAlcoholico, Precio precio) {
        Bebida bebida = Bebida.publicar(UUID.randomUUID(), vendedorId, nombre, tipoEnvase, grupoAlcoholico, precio);
        repository.guardar(bebida);
        return bebida;
    }
}