package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;
import com.uniquindio.vent2bebidas.domain.valueobject.Precio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CambiarPrecioBebidaUseCase {

    private final BebidaRepository repository;

    public CambiarPrecioBebidaUseCase(BebidaRepository repository) {
        this.repository = repository;
    }

    // Recibe tipos del DOMINIO (UUID, Precio), nunca el DTO de la API.
    // Busca -> pide al dominio (que valida dueño, moneda y valor) -> guarda.
    @Transactional
    public Bebida ejecutar(UUID bebidaId, UUID vendedorId, Precio nuevoPrecio) {
        Bebida bebida = repository.obtenerPorId(bebidaId).orElseThrow();
        bebida.cambiarPrecio(vendedorId, nuevoPrecio);
        repository.guardar(bebida);
        return bebida;
    }
}
