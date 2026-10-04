package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EliminarBebidaUseCase {

    private final BebidaRepository repository;

    public EliminarBebidaUseCase(BebidaRepository repository) {
        this.repository = repository;
    }

    public void ejecutar(UUID bebidaId) {
        Bebida bebida = repository.obtenerPorId(bebidaId).orElseThrow();
        bebida.eliminarLogicamente(); // borrado LÓGICO: la bebida sigue existiendo
        repository.guardar(bebida);
    }
}