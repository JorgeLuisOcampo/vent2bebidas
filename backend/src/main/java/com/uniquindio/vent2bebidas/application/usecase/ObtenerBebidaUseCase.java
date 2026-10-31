package com.uniquindio.vent2bebidas.application.usecase;

import com.uniquindio.vent2bebidas.domain.entity.Bebida;
import com.uniquindio.vent2bebidas.domain.repository.BebidaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ObtenerBebidaUseCase {
    private final BebidaRepository repository;

    public ObtenerBebidaUseCase(BebidaRepository repository) {
        this.repository = repository;
    }

    // Si no existe lanza NoSuchElementException, que el GlobalExceptionHandler convierte en 404.
    public Bebida ejecutar(UUID id) {
        return repository.obtenerPorId(id).orElseThrow();
    }
}