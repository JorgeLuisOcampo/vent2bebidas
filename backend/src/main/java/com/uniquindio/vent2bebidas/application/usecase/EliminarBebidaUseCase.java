package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EliminarBebidaUseCase {

    private final BebidaRepository repository;

    public EliminarBebidaUseCase(BebidaRepository repository) {
        this.repository = repository;
    }

    // Borrado LÓGICO: la fila sigue en la base con estado ELIMINADA (soft delete).
    @Transactional
    public void ejecutar(UUID bebidaId, UUID vendedorId) {
        Bebida bebida = repository.obtenerPorId(bebidaId).orElseThrow();
        bebida.eliminarLogicamentePor(vendedorId);
        repository.guardar(bebida);
    }
}
