package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Envase;
import com.uniquindio.vent2bebidas.domain.repository.EnvaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DescartarEnvaseUseCase {

    private final EnvaseRepository repository;

    public DescartarEnvaseUseCase(EnvaseRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Envase ejecutar(UUID envaseId) {
        Envase envase = repository.obtenerPorId(envaseId).orElseThrow();
        envase.descartar();
        repository.guardar(envase);
        return envase;
    }
}