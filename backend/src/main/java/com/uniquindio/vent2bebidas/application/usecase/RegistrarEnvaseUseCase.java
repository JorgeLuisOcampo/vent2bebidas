package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Envase;
import com.uniquindio.vent2bebidas.domain.repository.EnvaseRepository;
import com.uniquindio.vent2bebidas.domain.valueobject.TipoEnvase;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegistrarEnvaseUseCase {

    private final EnvaseRepository repository;

    public RegistrarEnvaseUseCase(EnvaseRepository repository) {
        this.repository = repository;
    }

    public Envase ejecutar(TipoEnvase tipo) {
        Envase envase = Envase.registrar(UUID.randomUUID(), tipo);
        repository.guardar(envase);
        return envase;
    }
}