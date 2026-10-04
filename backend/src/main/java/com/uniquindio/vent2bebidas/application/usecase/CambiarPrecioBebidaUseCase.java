package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CambiarPrecioBebidaUseCase {

    private final BebidaRepository repository;

    public CambiarPrecioBebidaUseCase(BebidaRepository repository) {
        this.repository = repository;
    }

    public void ejecutar(UUID bebidaId, Precio nuevoPrecio) {
        Bebida bebida = repository.obtenerPorId(bebidaId).orElseThrow();
        bebida.cambiarPrecio(nuevoPrecio);
        repository.guardar(bebida);
    }
}