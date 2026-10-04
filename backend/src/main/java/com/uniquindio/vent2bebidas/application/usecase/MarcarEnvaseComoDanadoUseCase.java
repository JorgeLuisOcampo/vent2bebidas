package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Envase;
import com.uniquindio.vent2bebidas.domain.repository.EnvaseRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MarcarEnvaseComoDanadoUseCase {

    private final EnvaseRepository repository;

    public MarcarEnvaseComoDanadoUseCase(EnvaseRepository repository) {
        this.repository = repository;
    }

    public void ejecutar(UUID envaseId) {
        Envase envase = repository.obtenerPorId(envaseId).orElseThrow();
        envase.marcarComoDanado();
        repository.guardar(envase);
    }
}