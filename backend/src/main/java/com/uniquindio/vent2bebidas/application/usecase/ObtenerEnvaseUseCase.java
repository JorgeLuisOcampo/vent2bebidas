package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Envase;
import com.uniquindio.vent2bebidas.domain.repository.EnvaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ObtenerEnvaseUseCase {

    private final EnvaseRepository repository;

    public ObtenerEnvaseUseCase(EnvaseRepository repository) {
        this.repository = repository;
    }

    // Si no existe lanza NoSuchElementException -> 404.
    public Envase ejecutar(UUID envaseId) {
        return repository.obtenerPorId(envaseId).orElseThrow();
    }
}